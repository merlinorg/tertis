package org.merlin.tertis

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch

abstract class Scene {
  def init(): InputAdapter
  def update(delta: Float): Option[Scene]
  def render(batch: PolygonSpriteBatch): Unit
}
