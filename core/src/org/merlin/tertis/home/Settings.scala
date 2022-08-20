package org.merlin.tertis
package home

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Geometry.Dimension
import org.merlin.tertis.Scene
import org.merlin.tertis.common.{Frame, Starfield}

// I don't like that this fades in after home fades out, but modeling this as a separate scene
// makes life so much easier.
class Settings(home: Home) extends Scene {
  import Settings._

  var alpha: Float = 0f
  var done: Boolean = false

  private val IconSize = (Dimension * 3 / 4).floor
  private val IconTop = Geometry.ScreenHeight - IconSize * 5
  private val IconSpacing = IconSize * 3

  val icons: List[Icon] = List(
    new BasicIcon(
      Geometry.ScreenWidth - IconSize * 2,
      Geometry.ScreenHeight - IconSize * 2,
      IconSize,
      Tertis.close,
      () => {
        done = true
      }
    ),
    new CheckIcon(
      IconSize * 2,
      IconTop,
      IconSize,
      Prefs.ZenMode,
      "Zen mode",
      "Slow and steady wins the race."
    ),
    new CheckIcon(
      IconSize * 2,
      IconTop - IconSpacing,
      IconSize,
      Prefs.TiltSpeed,
      "Tilt speed",
      "Tilt your phone to change the speed.",
      !Tertis.mobile
    ),
//    new CheckIcon(
//      IconSize * 2,
//      IconTop - IconSpacing * 2,
//      IconSize,
//      Prefs.StuffHappens,
//      "Stuff happens",
//      "Stuff happens while you play."
//    ),
    new CheckIcon(
      IconSize * 2,
      IconTop - IconSpacing * 2,
      IconSize,
      Prefs.LowContrast,
      "Low contrast",
      "More bland colours."
    ),
    new CheckIcon(
      IconSize * 2,
      IconTop - IconSpacing * 3,
      IconSize,
      Prefs.WeakRandomness,
      "Weak randomness",
      "Bind the hands of fate."
    ),
  )

  override def init(): SettingsControl =
    new SettingsControl(this)

  override def update(delta: Float): Option[Scene] = {
    Starfield.update(delta)
    Frame.update(delta)
    if (!done) {
      alpha = alpha.alphaUp(delta, SettingsFadeInSeconds)
      None
    } else {
      alpha = alpha.alphaDown(delta, SettingsFadeOutSeconds)
      (alpha == 0f)
        .option(home)
    }
  }

  override def render(batch: PolygonSpriteBatch): Unit = {
    Starfield.render(batch)
    icons.foreach(_.draw(batch, alpha * alpha))
    Frame.render(batch)
  }

}

object Settings {
  val SettingsFadeInSeconds = .3f
  val SettingsFadeOutSeconds = .3f
}
