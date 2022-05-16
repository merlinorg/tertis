package org.merlin.tertis
package game

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Scene
import org.merlin.tertis.common.{Frame, Starfield}
import org.merlin.tertis.home.Home

class Game extends Scene {
  import Game._

  var state: State = PlayingState

  val zenMode: Boolean = Prefs.ZenMode.isTrue
  val stuffHappens: Boolean = Prefs.StuffHappens.isTrue
  val weakRandomness: Boolean = Prefs.WeakRandomness.isTrue

  val board: Board = new Board(this)
  val nextUp: NextUp = new NextUp(this)
  var player: Player = new Player(this)
  val score: Score = new Score
  var fast: Boolean = false
  var gravity: Boolean = false
  // TODO: a queue of changes instead? How to do so...
  var shift: Option[Change] = None
  var autoShift: Option[Change] = None
  var rotate: Option[Change] = None
  var clickPlayed: Boolean = false

  override def init(): GameControl = {
    state = PlayingState
    Frame.targetAlpha = 1f
    new GameControl(this)
  }

  override def update(delta: Float): Option[Scene] = {
    Starfield.update(delta)
    Frame.update(delta)
    player.update(delta)
    nextUp.update(delta)
    score.update(delta)
    board.update(delta)
    if (player.blockOpt.isEmpty && !board.animating) {
      player.next(nextUp.shift())
    }
    PartialFunction.condOpt(state) {
      case QuitState => Home(this)
      case LostState => new Over(board, score)
    }
  }

  override def render(batch: PolygonSpriteBatch): Unit = {
    Starfield.render(batch)
    board.draw(batch)
    player.draw(batch)
    Frame.render(batch)
    score.draw(batch)
    nextUp.render(batch)
  }
}

object Game {
  sealed trait State
  case object PlayingState extends State
  case object LostState extends State
  case object QuitState extends State
}
