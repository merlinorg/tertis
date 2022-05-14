package org.merlin.tertis.home

import com.badlogic.gdx.InputAdapter
import org.merlin.tertis.Geometry

import scala.collection.mutable

abstract class IconAdapter(icons: => List[Icon]) extends InputAdapter {

  private val down = mutable.Map.empty[Int, Icon]

  override def touchDown(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    icons.find(icon =>
      within(screenX, screenY, icon.x, icon.y, icon.size)
    ) foreach { icon =>
      icon.onPress()
      down.put(pointer, icon)
    }
    true
  }

  override def touchUp(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    down.remove(pointer) foreach { icon =>
      icon.onRelease(within(screenX, screenY, icon.x, icon.y, icon.size))
    }
    true
  }

  // I deliberately pass the full width of the icon as its radius so the touch area is bigger
  def within(
      screenX: Int,
      screenY: Int,
      x: Float,
      y: Float,
      radius: Float
  ): Boolean = {
    val dx = x - screenX.toFloat
    val dy = Geometry.ScreenHeight - screenY.toFloat - y
    dx * dx + dy * dy < radius * radius
  }

}
