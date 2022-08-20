package org.merlin.tertis
package game

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Peripheral
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, PolygonSpriteBatch}
import org.merlin.tertis.Geometry._
import org.merlin.tertis.Tertis
import org.merlin.tertis.util.Average

case class BlockLoc(block: Block, rotation: Int, column: Int, y: Float)

object BlockLoc {
  def apply(block: Block): BlockLoc =
    BlockLoc(block, 0, (10 - block.size) / 2, Dimension * Rows)
}

class Player(game: Game) {
  // how many seconds has the piece been touched down
  var touchdown: Option[Float] = None
  // average speed
  val speed = new Average

  var blockOpt: Option[BlockLoc] = None

  def next(block: Block): Unit = {
    speed.reset()
    blockOpt = Some(BlockLoc(block))
  }

  def draw(batch: PolygonSpriteBatch): Unit = {
    blockOpt foreach { loc =>
      val blockColour = loc.block.getColor
      val colour = touchdown.fold(blockColour)(td =>
        blockColour.cpy
          .lerp(Color.WHITE, td / GracePeriodSeconds * .75f) // only go to 75%
      )
      loc.block.eachSquare(
        loc.rotation,
        (i, j, test) =>
          BlockRenderer.render(
            batch,
            colour,
            OffsetX + (loc.column + i) * Dimension,
            OffsetY + loc.y.floor + j * Dimension,
            Dimension,
            Dimension,
            Bevel,
            test
          )
      )
    }
  }

  def update(delta: Float): Unit =
    blockOpt.foreach(update(delta, _))

  def update(delta: Float, oldLoc: BlockLoc): Unit = {
    val now = System.currentTimeMillis
    val block = oldLoc.block
    val fastness =
      game.fast.fold(1f, Prefs.TiltSpeed.fold(tiltSpeed, 0f))

    val speedup =
      game.zenMode.fold(
        0f,
        game.score.count.toFloat / SpeedupRate.toFloat
      )
    val velocityY = game.gravity.fold(
      GravitySpeed,
      SlowSpeed + (FastSpeed - SlowSpeed) * (fastness + speedup)
    )
    speed += velocityY

    // if you move two dimension units you could jump through blocks
    val deltaY = (velocityY * Dimension * delta) min (Dimension * 15 / 8)
    val newY = oldLoc.y - deltaY

    val floorY =
      Some(quantize(oldLoc.y)).filter(_ > newY) // floor of y if above newY
    val newColumn = game.shift
      .orElse(game.autoShift.filter(_.timestamp < now - AutoRepeatMillis))
      .filter(shift => shift.auto || shift.timestamp > now - KeyDurationMillis)
      .map(change => oldLoc.column + change.value)
    val newRotation = game.rotate
      .filter(_.timestamp > now - KeyDurationMillis)
      .map(change => (oldLoc.rotation + 4 + change.value) % 4)

    val wallKicks = newRotation
      .map(newRot => {
        // > 0 if the rotation sticks out to the left
        val kickRight =
          block.firstColumn(oldLoc.rotation) - block.firstColumn(newRot)
        // < 0 if the rotation sticks out to the right
        val kickLeft =
          block.lastColumn(oldLoc.rotation) - block.lastColumn(newRot)
        ((1 to kickRight) ++ (kickLeft to -1)).map(_ + oldLoc.column).toList
      })
      .getOrElse(Nil)

    // Floor kicks are right out.

    // search all combinations of shifts rotates and moves, including into any position
    // that we passed on this slide down
    val newLocations = for {
      rotation <- optList(newRotation, oldLoc.rotation)
      column <- optList(newColumn, oldLoc.column) ::: wallKicks
      y <- optList(floorY, newY).reverse
    } yield oldLoc.copy(rotation = rotation, column = column, y = y)

    newLocations.find(isValid(game, _)) match {
      case Some(newLoc) =>
        blockOpt = Some(newLoc)
        val shifted = newLoc.column != oldLoc.column
        val rotated = newLoc.rotation != oldLoc.rotation
        // drop the shift if it's a one-off that succeeded or it's an auto that did not
        game.shift = game.shift.filter(_.auto == shifted)
        if (newLoc.rotation != oldLoc.rotation)
          game.rotate = None // wrong
        if (newLoc.y > newY) { // didn't move full amount so hit something
          if (
            !shifted && !rotated && touchdown.exists(_ >= GracePeriodSeconds)
          ) {
            game.board.drop(
              block,
              newLoc.rotation,
              newLoc.column,
              (newLoc.y / Dimension).floor.toInt
            )
            touchdown = None
            blockOpt = None
            game.gravity = false
            game.score.drop(speed.value / SlowSpeed)
          } else {
            touchdown = touchdown.map(_ + delta).orElse(Some(0f))
          }
        } else {
          touchdown = None
        }
        // In addition to clicking after you have just touched down, we deliver haptic and audio feedback if you
        // would touch down next frame. This gives you a few milliseconds warning.. We have to use slow
        // speed lest you drop to slow speed next frame and we have clicked prematurely
        val nextLoc = newLoc.copy(y = newLoc.y - SlowSpeed * Dimension * delta)
        if (touchdown.isDefined || !isValid(game, nextLoc)) {
          if (!game.clickPlayed) {
            game.clickPlayed = true
            dropClick((velocityY / FastSpeed) min 1f, newLoc.y)
          }
        } else {
          game.clickPlayed = false
        }
      case None =>
        game.shift = game.shift.filterNot(_.auto)
        // initial piece placement invalid => endgame
        if (!Prefs.MuteAudio.isTrue)
          Tertis.end.play(1f)
        game.score.recordHighScore()
        game.state = Game.LostState
        ScoreIO.saveScore(game.score)

    }
  }

  def dropClick(fastness: Float, y: Float): Unit = {
    if (!Prefs.MuteAudio.isTrue) {
      val volume = .25f + .5f * fastness
      val pitch = 1f + PitchShift * y / (Dimension * Rows)
      val pan = 0f
      Tertis.drop.play(volume, pitch, pan)
      if (input.isPeripheralAvailable(Peripheral.Vibrator))
        input.vibrate(10)
    }
  }

  private def tiltSpeed: Float = // -45 is 1f, -15 is 0f
    (-input.getPitch / 30f - .5f) max 0f min 1f

  private def quantize(y: Float): Float = Dimension * (y / Dimension).floor

  private def optList[A](opt: Option[A], a: A): List[A] =
    opt.fold(List(a))(a0 => List(a0, a))

  private def isValid(game: Game, loc: BlockLoc): Boolean =
    loc.block.forall(
      loc.rotation,
      (i, j) => {
        val column = loc.column + i
        val row0 = Math.floor(loc.y / Dimension).toInt + j
        val row1 = Math.ceil(loc.y / Dimension).toInt + j
        column >= 0 && column < Columns && row0 >= 0 && !game.board
          .test(column, row0) && !game.board.test(column, row1)
      }
    )

  // blocks per second
  val SlowSpeed = 4f
  val FastSpeed = 16f
  val GravitySpeed = 60f

  // for how many milliseconds is an action key valid (i.e. will effect if it becomes possible within this period)
  val KeyDurationMillis = 100L
  // how long before autorepeat kicks in
  val AutoRepeatMillis = 300L
  // for how long can you manipulate a landed piece
  val GracePeriodSeconds = .2f

  // 0f-1f how much to shift the pitch up at the top
  val PitchShift = .1f

  // double speed after 800 pieces = 200 rows, triple speed after 1600 etc
  val SpeedupRate = 800

  val font = new BitmapFont()
}
