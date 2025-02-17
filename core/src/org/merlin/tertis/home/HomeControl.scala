package org.merlin.tertis.home

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys

class HomeControl(home: Home) extends IconAdapter(home.icons):

  override def keyDown(keycode: Int): Boolean =
    if keycode == Keys.ESCAPE || keycode == Keys.BACK then Gdx.app.exit()
    true

  override def keyUp(keycode: Int): Boolean =
    if keycode == Keys.SPACE || keycode == Keys.ENTER then home.play()
    else if keycode == Keys.SLASH then home.state = Home.State.HelpState
    true
