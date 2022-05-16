package org.merlin.tertis
package home

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Geometry.Dimension
import org.merlin.tertis.home.Icon._
import org.merlin.tertis.util.TextureWrapper

trait Icon {
  import Icon._

  def draw(batch: PolygonSpriteBatch, alpha: Float): Unit
  def x: Float
  def y: Float
  def size: Float
  def onPress(): Unit = ()
  def onRelease(inside: Boolean): Unit = ()

  protected def draw(
      batch: PolygonSpriteBatch,
      alpha: Float,
      texture: TextureWrapper,
      color: Color = White
  ): Unit = {
    batch.setColor(color ⍺ alpha)
    batch.draw(texture, x - size / 2, y - size / 2, size, size)
  }
}

object Icon {
  val White = new Color(1f, 1f, 1f, 1f)
  val Grey = new Color(.4f, .4f, .4f, 1f)
}

abstract class BaseIcon(disabled: Boolean = false) extends Icon {
  var pressed = false

  override def onPress(): Unit = {
    if (!disabled) {
      if (!Prefs.MuteAudio.isTrue)
        Tertis.click.play(.125f)
      pressed = true
    }
  }

  override def onRelease(inside: Boolean): Unit = {
    if (!disabled) {
      pressed = false
      if (inside) clicked()
    }
  }

  protected def clicked(): Unit
}

class PrefIcon(
    val x: Float,
    val y: Float,
    val size: Float,
    pref: Pref,
    ifTrue: TextureWrapper,
    ifFalse: TextureWrapper
) extends BaseIcon {

  override def draw(batch: PolygonSpriteBatch, alpha: Float): Unit =
    draw(batch, alpha * pressed.fold(.5f, 1f), pref.fold(ifTrue, ifFalse))

  override def clicked(): Unit = {
    pref.set(!pref.booleanValue.isTrue)
  }
}

class BasicIcon(
    val x: Float,
    val y: Float,
    val size: Float,
    texture: TextureWrapper,
    callback: () => Unit,
    color: Color = White
) extends BaseIcon {

  override def draw(batch: PolygonSpriteBatch, alpha: Float): Unit =
    draw(batch, alpha * pressed.fold(.5f, 1f), texture, color)

  override def clicked(): Unit = {
    callback()
  }
}

class CheckIcon(
    val x: Float,
    val y: Float,
    val size: Float,
    pref: Pref,
    label: String,
    description: String,
    disabled: Boolean = false
) extends BaseIcon(disabled) {

  override def draw(batch: PolygonSpriteBatch, alpha: Float): Unit = {
    val color = disabled.fold(Icon.Grey, Icon.White)
    draw(
      batch,
      alpha * pressed.fold(.5f, 1f),
      pref.fold(Tertis.checkOn, Tertis.checkOff),
      color
    )
    Text.smallFont.setColor(color ⍺ alpha)
    val textY =
      y + (Text.smallFont.getLineHeight + Text.tinyFont.getAscent - Text.tinyFont.getDescent) / 2
    Text.smallFont.draw(batch, label, x + size * 1.25f, textY)
    Text.tinyFont.setColor(color ⍺ alpha)
    Text.tinyFont.draw(
      batch,
      description,
      x + size * 1.25f,
      textY - Text.smallFont.getLineHeight
    )

  }

  override def clicked(): Unit = {
    pref.set(!pref.booleanValue.isTrue)
  }
}

class KeyIcon(
    val x: Float,
    val y: Float,
    val size: Float,
    icon: TextureWrapper,
    rotation: Float,
    label: String,
    description: String
) extends BaseIcon {

  override def draw(batch: PolygonSpriteBatch, alpha: Float): Unit = {
    val color = White ⍺ alpha
    batch.setColor(color)
    batch.draw(
      icon,
      x - size / 2,
      y - size / 2,
      size / 2,
      size / 2,
      size,
      size,
      1f,
      1f,
      rotation,
      0,
      0,
      icon.width,
      icon.height,
      false,
      false
    )
    Text.smallFont.setColor(color)
    val textY =
      y + (Text.smallFont.getLineHeight + Text.tinyFont.getAscent - Text.tinyFont.getDescent) / 2
    Text.smallFont.draw(batch, label, x + size * 1.25f, textY)
    val grey = Grey ⍺ alpha
    Text.tinyFont.setColor(grey)
    Text.tinyFont.draw(
      batch,
      description,
      x + size * 1.25f,
      textY - Text.smallFont.getLineHeight
    )
  }

  override def clicked(): Unit = ()
}

class PlayIcon(val x: Float, val y: Float, val size: Float, home: Home)
    extends BaseIcon {
  override def draw(batch: PolygonSpriteBatch, alpha: Float): Unit = {
    val playScale = alpha * alpha * (if (pressed) .95f else 1f)
    val playWidth = playScale * Geometry.ScreenWidth / 6
    val playHeight = Tertis.play.height * playWidth / Tertis.play.width
    batch.setColor(1, 1, 1, alpha * alpha)
    val (dX, dY) = if (compassAvailable) compassShift else (0f, 0f)
    batch.draw(
      Tertis.play,
      x - playWidth / 3 + dX,
      y - playHeight / 2 + dY,
      playWidth,
      playHeight
    )
  }

  // TODO: temporally smooth this?
  private def compassShift: (Float, Float) = {
    val roll = Gdx.input.getRoll // -180 to 180
    val pitch = Gdx.input.getPitch // -90 to 90
    val scale = Dimension / 4f / 90f
    // as pitch approaches 90, roll becomes indeterminate so ramp to 0 from 75 to 85
    val pitchLimit =
      if (pitch.abs > 85f) 0f
      else if (pitch.abs < 75f) 1f
      else (85f - pitch.abs) / 10f
    (
      (roll max -90f min 90f) * scale * pitchLimit * pitchLimit,
      (pitch + 45) * scale
    )
  }

  override def clicked(): Unit = home.play()
}
