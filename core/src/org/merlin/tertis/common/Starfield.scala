package org.merlin.tertis
package common

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.{Quaternion, Vector3}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Starfield {

  val NumStars = 256
  val FadeInSeconds = 5f

  var alpha = 0f

  val stars: ListBuffer[Star] = mutable.ListBuffer
    .fill(NumStars)(Star.newStar)
    .sortBy(star => -star.location.z)

  private val rotation = new Quaternion()
  private val translation = new Vector3()

  var r = 0f
  def update(delta: Float): Unit = {
    r = r + delta

    if (Tertis.mobile) {
      // So... I want this to match the device orientation, but I just can't.
      // Even with the LPQF the display is super jittery, and then the axes are
      // wrong; the rotation matrix isn't how I expect it to be and so most
      // phone rotations cause the starfield to rotate unexpectedly...
//      private val rawRotation = new Matrix3()
//      private val lowPassFilter = new LowPassQuaternionFilter(60)
//      Gdx.input.getRotationMatrix(rawRotation.getValues)
//      rawRotation.transpose()
//      lowPassFilter.add(rawRotation)
//      rotation.set(lowPassFilter.value)
      rotation.set(Vector3.X, r / 3)
    } else {
      rotation.set(Vector3.X, r / 3)
    }
    val inverse = new Quaternion(rotation).conjugate()

    alpha = (alpha + delta / FadeInSeconds) min 1f

    translation.set(0, 0, -delta * 300)
    rotation.transform(translation)
    stars.filterInPlace(
      _.update(translation, inverse)
    ) // filterInPlace

    // I don't maintain the sort order of the list but it should remain relatively ordered
    while (stars.size < NumStars)
      stars.prepend(Star.newStar(rotation))
  }

  // This is all a bit wonky but I switched from stars only within the frame to stars only outside the
  // frame to a bit of both...

  def render(batch: PolygonSpriteBatch): Unit = {
    renderImpl(batch, within = true)
  }

  def renderOnFrame(batch: PolygonSpriteBatch): Unit = {
    renderImpl(batch, within = false)
  }

  def renderImpl(batch: PolygonSpriteBatch, within: Boolean): Unit = {
    val a = within.fold(1f - Frame.alpha / 2, 1f) * alpha
    if (a != 0f) {
      stars.foreach(
        _.draw(batch, a, (x, y) => Frame.frame.contains(x, y) == within)
      )
    }
  }

}
