package org.merlin.tertis
package game

import com.badlogic.gdx.graphics.g2d.{GlyphLayout, PolygonSpriteBatch}
import org.merlin.tertis.Geometry._
import org.merlin.tertis.Prefs

// on phone fwiw 12 minutes for 120 rows
class Score {
  var alpha: Float = 0f
  var time: Float = 0f
  var score: Int = 0
  var count: Int = 0
  var rows: Int = 0
  var speedRun: Int = 0 // sequential piece dropped at >= SpeedRunSpeed
  var highScore: Boolean = false

  // speed is multiple of slow speed that was used
  def drop(speed: Float): Unit = {
    count = count + 1
    speedRun = (speed >= SpeedRunSpeed).fold(1 + speedRun, 0)
    score = score + speedRun
  }

  // an epic drop would be worth 4 * 4 * 184 = 2944 points before speed multiplier
  def cleared(rows: Int, mass: Int): Unit = {
    this.rows = this.rows + rows
    val speedX =
      speedRun / 5 // integer arithmetic so no bonus until after at least 5
    score = score + rows * rows * mass * (1 + speedX)
  }

  def recordHighScore(): Unit = {
    if (score > 0 && Prefs.HighScore.intValue.forall(_ < score)) {
      highScore = true
      Prefs.HighScore.set(score)
      Prefs.HighTime.set(time.intValue)
      Prefs.HighRows.set(rows.intValue)
    }
    Prefs.AllTime.set(
      Prefs.AllTime.longValue.fold(time.longValue)(_ + time.longValue)
    )
  }

  def update(delta: Float): Unit = {
    alpha = (alpha + delta / FadeInSeconds) min 1f
    time = time + delta
  }

  def draw(batch: PolygonSpriteBatch): Unit = {
    Text.smallFont.setColor(1, 1, 1, alpha * alpha)
    Text.mediumFont.setColor(1, 1, 1, alpha * alpha)
    val scoreLabel = new GlyphLayout(Text.smallFont, f"SCORE:")
    val scoreValue =
      new GlyphLayout(Text.mediumFont, f" $score%,d")
    val xOffset = OffsetX + Dimension / 4
    val baseline = OffsetY + Dimension * Rows + Dimension / 2
    Text.smallFont.draw(
      batch,
      scoreLabel,
      xOffset,
      baseline + Text.smallFont.getCapHeight
    )
    Text.mediumFont.draw(
      batch,
      scoreValue,
      xOffset + scoreLabel.width,
      baseline + Text.mediumFont.getCapHeight
    )
  }

  val FadeInSeconds = 1f
  val SpeedRunSpeed = 4f // a piece is speedy if dropped at >= 4x slow speed
}
