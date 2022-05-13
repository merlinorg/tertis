package org.merlin.tertis.game

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter

class OverControl(over: Over) extends InputAdapter {

  override def keyDown(keycode: Int): Boolean = {
    if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
      over.home()
    }
    true
  }

  override def keyUp(keycode: Int): Boolean = {
    if (keycode == Keys.SPACE || keycode == Keys.ENTER) {
      over.home()
    }
    true
  }
  override def touchUp(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    over.home()
    true
  }
}
