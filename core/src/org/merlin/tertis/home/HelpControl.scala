package org.merlin.tertis.home

import com.badlogic.gdx.Input.Keys

class HelpControl(help: Help) extends IconAdapter(help.icons) {
  override def touchUp(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    help.continue()
    true
  }

  override def keyDown(keycode: Int): Boolean = {
    if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
      help.exit()
    }
    true
  }

  override def keyUp(keycode: Int): Boolean = {
    if (keycode == Keys.SPACE || keycode == Keys.ENTER) {
      help.continue()
    }
    true
  }
}
