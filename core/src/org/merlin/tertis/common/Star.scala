package org.merlin.tertis.common

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.{MathUtils, Quaternion, Vector3}
import org.merlin.tertis.{Geometry, Tertis}

case class Star(
    location: Vector3
) {
  import Star._
  var x, y, z: Float = 0

  def update(translation: Vector3, rotation: Quaternion): Boolean = {
    location.add(translation)
    val rotated = rotation.transform(location.cpy)
    z = rotated.z
    x = (rotated.x * ViewerDistance / z + w2).floor
    y = (rotated.y * ViewerDistance / z + h2).floor
    (z > ViewerDistance) && (x >= 0) && (y >= 0) && (x < Geometry.ScreenWidth) && (y < Geometry.ScreenHeight)
  }

  def draw(
      batch: PolygonSpriteBatch,
      alpha: Float,
      pred: (Float, Float) => Boolean
  ): Unit = {
    if (pred(x, y)) {
      val starAlpha = (FarDistance - z) / FarDistance * alpha * alpha
      if (size <= 1) {
        batch.setColor(.7f, .7f, .7f, starAlpha)
        batch.draw(Tertis.pixture, x, y, 1, 1)
      } else {
        batch.setColor(.7f, .7f, .7f, starAlpha)
        batch.draw(Tertis.pixture, x - 1, y, 3, 1)
        batch.draw(Tertis.pixture, x, y - 1, 1, 3)
      }
    }
  }
}

object Star {
  private val size = (Geometry.ScreenWidth / 500f).floor
  private val w2 = Geometry.ScreenWidth * .5f
  private val h2 = Geometry.ScreenHeight * .5f
  private val ViewerDistance = h2 * 2
  val FarDistance = 10 * ViewerDistance

  val qIdentity = new Quaternion()

  // new initial star on any z index
  def newStar: Star = newStar(qIdentity, ViewerDistance, FarDistance)

  // replacement star at far distance
  def newStar(
      rotation: Quaternion,
      z0: Float = FarDistance,
      z1: Float = FarDistance
  ): Star = {
    // So .. this is inadequate. If you are rotating and stars fall off one side, we replace them uniformly
    // across the viewport which means the other side will have a deficit of stars. Really we want to replace
    // any stars that were rotated off screen with new stars created within the full depth of the view frustum
    // that is now visible, so near stars will rotate on screen. But I'm not doing that now.

    val z = MathUtils.random(z0, z1)
    val x = w2 * z / ViewerDistance
    val y = h2 * z / ViewerDistance
    val loc = new Vector3(MathUtils.random(-x, x), MathUtils.random(-y, y), z)
    rotation.transform(loc)
    new Star(loc)
  }
}
