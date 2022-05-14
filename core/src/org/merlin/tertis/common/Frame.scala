package org.merlin.tertis.common

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Rectangle
import org.merlin.tertis.Geometry
import org.merlin.tertis.Geometry._
import org.merlin.tertis.Tertis.pixture

object Frame {
  // Not really alpha, but the 0f..1f size of the frame
  var alpha: Float = 0f
  var targetAlpha: Float = 0f
  var frame: Rectangle = new Rectangle()

  def update(delta: Float): Unit = {
    if (alpha < targetAlpha) {
      alpha = (alpha + delta / FadeSpeedSeconds) min 1f
    } else if (alpha > targetAlpha) {
      alpha = (alpha - delta / FadeSpeedSeconds) max 0f
    }

    val offsetY = OffsetY * alpha
    val offsetX = OffsetX * alpha
    val width =
      Geometry.ScreenWidth * (1f - alpha) + Columns * Dimension * alpha
    val height =
      Geometry.ScreenHeight * (1f - alpha) + Rows * Dimension * alpha
    frame.set(offsetX, offsetY, width, height)
  }

  def render(batch: PolygonSpriteBatch): Unit = {
    batch.setColor(BlackColour)
    batch.draw(
      pixture,
      0,
      0,
      Geometry.ScreenWidth,
      frame.y
    )
    batch.draw(
      pixture,
      0,
      frame.y + frame.height,
      Geometry.ScreenWidth,
      Geometry.ScreenHeight - frame.y - frame.height
    )
    batch.draw(
      pixture,
      0,
      0,
      frame.x,
      Geometry.ScreenHeight
    )
    batch.draw(
      pixture,
      frame.x + frame.width,
      0,
      Geometry.ScreenWidth - frame.x - frame.width,
      Geometry.ScreenHeight
    )
    Starfield.renderOnFrame(batch)
    GreyColour.a = alpha
    batch.setColor(GreyColour)
    batch.draw(
      pixture,
      frame.x,
      frame.y - 1,
      frame.width + 1,
      1
    )
    batch.draw(
      pixture,
      frame.x + frame.width,
      frame.y,
      1,
      frame.height + 1
    )
    batch.draw(
      pixture,
      frame.x - 1,
      frame.y + frame.height,
      frame.width + 1,
      1
    )
    batch.draw(
      pixture,
      frame.x - 1,
      frame.y - 1,
      1,
      frame.height + 1
    )

  }

  private val BlackColour = new Color(0, 0, 0, 1)
  private val GreyColour = new Color(.5f, .5f, .5f, 1)

  val FadeSpeedSeconds = .5f
}
