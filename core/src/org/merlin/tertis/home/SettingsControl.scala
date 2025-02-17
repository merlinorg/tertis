package org.merlin.tertis.home

import com.badlogic.gdx.Input.Keys

class SettingsControl(settings: Settings) extends IconAdapter(settings.icons):
  override def keyDown(keycode: Int): Boolean =
    if keycode == Keys.ESCAPE || keycode == Keys.BACK then settings.done = true
    true
