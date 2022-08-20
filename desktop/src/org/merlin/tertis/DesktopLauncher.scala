package org.merlin.tertis

import com.badlogic.gdx.backends.lwjgl3.{
  Lwjgl3Application,
  Lwjgl3ApplicationConfiguration
}
import de.damios.guacamole.gdx.StartOnFirstThreadHelper

object DesktopLauncher extends App {
  StartOnFirstThreadHelper.executeIfJVMValid(() => {
    val config = new Lwjgl3ApplicationConfiguration
    config.setForegroundFPS(60)
    config.setWindowedMode(500, 1050)
    new Lwjgl3Application(new Tertis, config)
  })
}
