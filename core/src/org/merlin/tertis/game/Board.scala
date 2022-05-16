package org.merlin.tertis
package game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Geometry._
import org.merlin.tertis.{Prefs, Tertis}

import scala.collection.mutable

/** Identifier allows us to identify all the pixels of a single block. They may get split if
  * a row is dropped.
  */
final case class Region(
    color: Color,
    identifier: Int,
    dead: Boolean
)

object Region {
  def apply(color: Color, dead: Boolean = false): Region = {
    generator += 1
    Region(color, generator, dead)
  }

  private[this] var generator: Int = 0
}

class Board(game: Game) {
  import Board._

  // null? Yes!
  val board = Array.fill[Region](BoardRows * Columns)(null)
  val dropRows = mutable.Set.empty[Int]
  var redAlpha: Float = 0f
  var redVelocity: Float = 0f
  var redShift: Float = 0f // 1f per row shifted
  // if a row drop has two pieces to it, the length of the first run
  var firstChunk: Int = 0
  var ending: Boolean = false
  private val flash = mutable.Set.empty[Int]
  private var flashdown = 0f

//    val reg = Region(Color.ROYAL)
//    for (j <- 0 until 20 * Columns) if (j % Columns > 0) board.update(j, reg)

  def animating: Boolean = dropRows.nonEmpty

  def ended: Boolean = redShift >= BoardRows

  def update(delta: Float): Unit = {
    flashdown = flashdown.alphaDown(delta, FlashdownSeconds)
    if (ending) {
      redAlpha = redAlpha.alphaUp(delta, RedFadeSeconds)
      redVelocity = redVelocity + delta / RedShiftAccelerationSeconds
      redShift = (redShift + redVelocity) min BoardRows.toFloat
    } else if (animating) {
      if (redAlpha < 1f) {
        redAlpha = redAlpha.alphaUp(delta, RedFadeSeconds)
      } else {
        redVelocity = redVelocity + delta / RedShiftAccelerationSeconds
        val xShift = redShift
        redShift = (redShift + redVelocity) min dropRows.size.toFloat
        if (redShift >= dropRows.size) {
          var drop = 0
          var mass = 0
          for (j <- 0 until BoardRows) {
            while (dropRows.contains(j + drop)) drop = drop + 1
            for (i <- 0 until Columns) {
              val region = get(i, j + drop)
              set(i, j, region)
              if (drop > 0 && (region ne null)) mass = mass + 1
            }
          }
          if (!Prefs.MuteAudio.isTrue && mass > 0)
            Tertis.crash.play(
              (mass * drop / 400f) min 1f
            ) // max mass is ~140, max drop is 4
          dropRows.clear()
          redAlpha = 0f
          redShift = 0f
          redVelocity = 0f
          game.score.cleared(drop, Columns * drop + mass)
        } else if (xShift < firstChunk && redShift >= firstChunk) {
          if (!Prefs.MuteAudio.isTrue)
            Tertis.crash.play(15f / 400f) // arbitrary..
        }
      }
    }
  }

  def draw(batch: PolygonSpriteBatch): Unit = {
    var dropped = 0
    for (j <- 0 until BoardRows) {
      if (dropRows.contains(j)) dropped = dropped + 1
      val yShift = ending.fold(
        (redShift * Dimension).floor,
        (redShift * Dimension).floor min (dropped * Dimension)
      )
      for (i <- 0 until Columns) {
        val region = board(j * Columns + i)
        if (region ne null) {
          Red.a = 1f - redAlpha // fade through red to invisible
          val baseColor =
            (flash.contains(region.identifier) && flashdown > 0).fold(
              region.color.cpy
                .lerp(Color.WHITE, flashdown),
              region.color
            )
          val color =
            (ending || region.dead)
              .fold(baseColor.cpy.lerp(Red, redAlpha * redAlpha), baseColor)
          BlockRenderer.render(
            batch,
            color,
            OffsetX + i * Dimension,
            OffsetY + j * Dimension - yShift.floor,
            Dimension,
            Dimension,
            Bevel,
            (di, dj) => get(i + di, j + dj) eq region
          )
        }
      }
    }
  }

  private def set(i: Int, j: Int, region: Region /* | null */ ): Unit =
    if ((i >= 0) && (i < Columns) && (j >= 0) && (j < BoardRows))
      board.update(j * Columns + i, region)

  private def get(i: Int, j: Int): Region /* | null */ =
    if ((i >= 0) && (i < Columns) && (j >= 0) && (j < BoardRows))
      board(j * Columns + i)
    else null

  def test(i: Int, j: Int): Boolean = get(i, j) ne null

  def drop(
      block: Block,
      rotation: Int,
      column: Int,
      row: Int
  ): Unit = {
    val region = Region(block.getColor)
    flash.clear()
    flash.add(region.identifier)
    flashdown = 1f
    block.foreach(
      rotation,
      (i, j) => set(column + i, row + j, region)
    )
    // map regions that are broken by a drop into new regions so they render as separate pieces
    val regionMap = mutable.Map.empty[Int, Region]
    var dropping = false // are we it a group of drops
    var firstSpan = true
    for (j <- 0 until BoardRows) {
      val drop = rowIsFull(j)
      if (drop) {
        dropRows.add(j)
      }
      if (dropping != drop) {
        regionMap.clear()
        if (dropping && firstSpan) {
          // have I just ended the first span of drops
          firstSpan = false
          firstChunk = dropRows.size
        }
      }
      if (drop || dropping != drop) {
        for (i <- 0 until Columns) {
          val region = get(i, j)
          if (region ne null) {
            val newRegion = regionMap.getOrElseUpdate(
              region.identifier,
              Region(region.color, drop)
            )
            if (flash.contains(region.identifier))
              flash.add(newRegion.identifier)
          }
        }
        dropping = drop
      }
      if (dropRows.nonEmpty) {
        for (i <- 0 until Columns) {
          val region = get(i, j)
          val mapped =
            if (region eq null) region
            else regionMap.getOrElse(region.identifier, region)
          set(i, j, mapped)
        }
      }
    }
  }

  def rowIsFull(row: Int): Boolean =
    (row >= 0) && (row < BoardRows) && (0 until Columns).forall(column =>
      board(row * Columns + column) ne null
    )

  def reset(): Unit =
    for (j <- 0 until BoardRows)
      for (i <- 0 until Columns)
        set(i, j, null)
}

object Board {
  // 84 extra for some room off the top
  val BoardRows = Rows + 4

  private val Red = new Color(1, 0, 0, 1f) // nb: mutates
  val RedFadeSeconds = .5f
  val RedShiftAccelerationSeconds = .5f
  val FlashdownSeconds = 0.5f
}
