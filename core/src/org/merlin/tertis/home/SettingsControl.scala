package org.merlin.tertis.home

import com.badlogic.gdx.Input.Keys

class SettingsControl(settings: Settings) extends IconAdapter(settings.icons) {
  override def keyDown(keycode: Int): Boolean = {
    if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
      settings.done = true
    }
    true
  }
}
