package org.merlin.tertis.game

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import org.merlin.tertis.Geometry

import scala.collection.mutable

class GameControl(game: Game) extends InputAdapter:
  import GameControl.*

  private val down = mutable.Map.empty[Int, (Int, Int)]

  // this doesn't actually use circle like the help suggests because it is not good
  override def touchDown(
    screenX: Int,
    screenY: Int,
    pointer: Int,
    button: Int
  ): Boolean =
    down.put(pointer, (screenX, screenY))
    true

  override def touchUp(
    screenX: Int,
    screenY: Int,
    pointer: Int,
    button: Int
  ): Boolean =
    down
      .remove(pointer)
      .foreach: (oldX, oldY) =>
        val third = oldX * 3 / Geometry.ScreenWidth

        val swipe =
          (oldX - screenX) * (oldX - screenX) + (oldY - screenY) * (oldY - screenY) > SwipeDistance * SwipeDistance

        if !swipe then
          if third < 1 then game.shift = Some(Change.down)
          else if third > 1 then game.shift = Some(Change.up)
        else if (screenX - oldX < 0) && ((oldY - screenY).abs < (oldX - screenX).abs) then // swipe left
          game.shift = Some(Change.down.copy(auto = true))
        else if (screenX - oldX > 0) && ((oldY - screenY).abs < (oldX - screenX).abs) then // swipe right
          game.shift = Some(Change.up.copy(auto = true))
        else if (screenY - oldY < 0) && ((oldY - screenY).abs > (oldX - screenX).abs) then // swipe up
          if third > 1 then game.rotate = Some(Change.down)
          else if third < 1 then game.rotate = Some(Change.up)
        else if (screenY - oldY > 0) && ((oldY - screenY).abs > (oldX - screenX).abs) then // swipe down
          if third < 1 then game.rotate = Some(Change.down)
          else if third < 2 then game.gravity = true
          else game.rotate = Some(Change.up)
    true

  override def keyDown(keycode: Int): Boolean =
    if keycode == Left then
      game.shift = Some(Change.down)
      game.autoShift = Some(Change.autoDown)
    else if keycode == Right then
      game.shift = Some(Change.up)
      game.autoShift = Some(Change.autoUp)
    else if keycode == Keys.HOME then game.shift = Some(Change.autoDown)
    else if keycode == Keys.END then game.shift = Some(Change.autoUp)
    else if keycode == Rotate then game.rotate = Some(Change.down)
    else if keycode == Drop then game.gravity = true
    else if Speeds.contains(keycode) then game.fast = true
    else if keycode == Keys.ESCAPE || keycode == Keys.BACK then game.state = Game.State.QuitState
    true

  override def keyUp(keycode: Int): Boolean =
    if keycode == Left then
      game.shift = game.shift.filterNot(_.value < 0)
      game.autoShift = game.autoShift.filterNot(_.value < 0)
    else if keycode == Right then
      game.shift = game.shift.filterNot(_.value > 0)
      game.autoShift = game.autoShift.filterNot(_.value > 0)
    else if Speeds.contains(keycode) then game.fast = false
    true

object GameControl:
  private val Left          = Keys.LEFT
  private val Right         = Keys.RIGHT
  private val Rotate        = Keys.UP
  private val Drop          = Keys.DOWN
  private val Speeds        = Set(
    Keys.CONTROL_LEFT,
    Keys.CONTROL_RIGHT,
    Keys.SHIFT_LEFT,
    Keys.SHIFT_RIGHT,
    Keys.ALT_LEFT,
    Keys.ALT_RIGHT
  )
  private val SwipeDistance = Geometry.ScreenHeight / 32
