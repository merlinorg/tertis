package org.merlin.tertis

import com.badlogic.gdx.backends.lwjgl3.{
  Lwjgl3Application,
  Lwjgl3ApplicationConfiguration
}
import de.damios.guacamole.gdx.StartOnFirstThreadHelper

object DesktopLauncher extends App:
  StartOnFirstThreadHelper.executeOnValidJVM: () =>
    val config = Lwjgl3ApplicationConfiguration()
    config.setForegroundFPS(60)
    config.setWindowedMode(500, 1050)
    Lwjgl3Application(Tertis(), config)
