package org.merlin.tertis
package game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Scene
import org.merlin.tertis.common.{Frame, Starfield}
import org.merlin.tertis.home.Home

import scala.concurrent.duration.{DurationInt, DurationLong}

class Over(board: Board, score: Score) extends Scene {
  import Over._

  var time: Float = 0f
  var alpha: Float = 0f

  var done: Boolean = false

  def home(): Unit = {
    if (time >= .5f) done = true
  }

  override def init(): OverControl = {
    board.ending = true
    Frame.targetAlpha = 0f
    new OverControl(this)
  }

  override def update(delta: Float): Option[Scene] = {
    time = time + delta
    if (done)
      alpha = alpha.alphaDown(delta, OverFadeOutSeconds)
    else if (time >= OverDelaySeconds)
      alpha = alpha.alphaUp(delta, OverFadeInSeconds)
    Frame.update(delta)
    board.update(delta)
    (done && alpha == 0f).option(new Home)
  }

  override def render(batch: PolygonSpriteBatch): Unit = {
    Starfield.render(batch)
    board.draw(batch)

    val color = Color.WHITE ⍺⍺ alpha
    Text.mediumFont.setColor(color)
    Text.smallFont.setColor(color)
    Text.tinyFont.setColor(color)

    // want springs, will pay
    val content =
      Text.mediumFont.getLineHeight + Text.smallFont.getLineHeight + 5 * Text.tinyFont.getLineHeight
    val margin =
      (Geometry.ScreenHeight - content) / 4

    val textY = Geometry.ScreenHeight - margin
    Text.draw(
      batch,
      Text.mediumFont,
      color,
      "Game Over",
      textY
    )

    val scoreY = textY - Text.mediumFont.getLineHeight - margin
    Text.draw(
      batch,
      Text.smallFont,
      color,
      f"${score.highScore.fold("High Score", "Score")}%s: ${score.score}%,d",
      scoreY
    )

    val statsY =
      scoreY - Text.smallFont.getLineHeight - Text.tinyFont.getLineHeight
    Text.draw(
      batch,
      Text.tinyFont,
      color,
      f"""${score.count}%,d blocks
         |${score.rows}%,d row${(score.rows != 1).fold("s", "")}%s
         |${score.time.toInt.seconds.toHumanString}%s
         |""".stripMargin,
      statsY
    )

    Prefs.AllTime.longValue foreach { allTime =>
      Text.draw(
        batch,
        Text.tinyFont,
        color,
        s"All Time: ${allTime.seconds.toHumanString}",
        margin + Text.tinyFont.getLineHeight
      )
    }

    Frame.render(batch)

  }
}

object Over {
  val OverDelaySeconds = 0.2f
  val OverFadeInSeconds = 0.5f
  val OverFadeOutSeconds = 0.3f
}
