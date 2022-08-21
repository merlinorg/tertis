package org.merlin.tertis
package home

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Geometry._
import org.merlin.tertis.common.{Frame, Starfield}
import org.merlin.tertis.game.Game
import org.merlin.tertis.{Scene, Tertis}

import scala.concurrent.duration.DurationInt

class Home(paused: Option[Game] = None) extends Scene {

  import Home._

  var state: State = HomeState
  var logoAlpha = 0f
  var playAlpha = 0f
  var discard = false

  // spring layout would make this easy

  private val IconSize = Dimension * 3 / 4
  private val IconCount = 3
  private val IconMargin =
    ((Geometry.ScreenWidth - IconCount * IconSize) / (IconCount + 1)).floor
  private val IconOffsetX = (IconMargin + IconSize / 2).floor
  private val IconSpacing = IconMargin + IconSize

  private val HighScoreSize =
    Text.smallFont.getLineHeight * 2 + Text.tinyFont.getLineHeight
  private val LogoWidth = (Geometry.ScreenWidth * 2 / 3).floor

  private val FooterMargin =
    ((Geometry.ScreenHeight - LogoWidth) / 4).floor
  private val IconOffsetY =
    (Geometry.ScreenHeight - (Geometry.ScreenHeight - LogoWidth) / 4).floor

  private val baseIcons: List[Icon] = List(
    new PlayIcon(
      Geometry.ScreenWidth / 2,
      Geometry.ScreenHeight / 2,
      LogoWidth / 2,
      this
    ),
    new PrefIcon(
      IconOffsetX,
      IconOffsetY,
      IconSize,
      Prefs.MuteAudio,
      Tertis.soundOff,
      Tertis.soundOn
    ),
//    new PrefIcon(
//      IconOffsetX + IconSpacing,
//      IconOffsetY,
//      IconSize,
//      Prefs.MuteMusic,
//      Tertis.musicOff,
//      Tertis.musicOn
//    ),
    new BasicIcon(
      IconOffsetX + IconSpacing,
      IconOffsetY,
      IconSize,
      Tertis.settings,
      () => {
        state = SettingsState
      }
    ),
    new BasicIcon(
      IconOffsetX + IconSpacing * 2,
      IconOffsetY,
      IconSize,
      Tertis.help,
      () => {
        state = HelpState
      }
    )
  )

  private val iconsWithDiscard = new BasicIcon(
    Geometry.ScreenWidth / 2 - Dimension * 9 / 4, // failure to get real dimensions
    FooterMargin + HighScoreSize - Text.tinyFont.getLineHeight / 2 - Text.smallFont.getAscent,
    IconSize / 2,
    Tertis.trash,
    () => {
      discard = true
    },
    HighScoreColor
  ) :: baseIcons

  def icons: List[Icon] =
    (paused.isDefined && !discard).fold(iconsWithDiscard, baseIcons)

  override def init(): HomeControl = {
    state = HomeState
    Frame.targetAlpha = 0f
    ScoreIO.loadScore()
    new HomeControl(this)
  }

  override def update(delta: Float): Option[Scene] = {
    Starfield.update(delta)
    Frame.update(delta)
    if (state == HomeState) {
      logoAlpha = logoAlpha.alphaUp(delta, LogoFadeInSeconds)
      if (logoAlpha > PlayDelaySeconds)
        playAlpha = playAlpha.alphaUp(delta, PlayFadeInSeconds)
      None
    } else {
      logoAlpha = logoAlpha.alphaDown(delta, LogoFadeOutSeconds)
      playAlpha = playAlpha.alphaDown(delta, PlayFadeOutSeconds)
      if (state == SettingsState) {
        (logoAlpha + playAlpha == 0f)
          .option(new Settings(this))
      } else if (state == PlayState) {
        (logoAlpha + playAlpha == 0f && Frame.alpha == 1f)
          .option(nextGame)
      } else {
        (logoAlpha + playAlpha == 0f).option(
          new Help(this, (state == HelpPlayState).option(nextGame))
        )
      }
    }
  }

  private def nextGame: Game =
    paused.filterNot(_ => discard).getOrElse(new Game)

  override def render(batch: PolygonSpriteBatch): Unit = {
    Starfield.render(batch)
    drawLogo(batch)
    icons.foreach(_.draw(batch, playAlpha * playAlpha))
    if (paused.isDefined && !discard) {
      drawPaused(batch)
    } else {
      for {
        score <- Prefs.HighScore.intValue
        time <- Prefs.HighTime.intValue
      } drawHighScore(batch, score, time)
    }
    Text.draw(
      batch,
      Text.tinyFont,
      Color.DARK_GRAY ⍺ (.25f * playAlpha),
      s"v${Tertis.version}",
      l =>
        (Geometry.ScreenWidth - l.width - Geometry.Dimension / 4) -> (l.height + Geometry.Dimension / 4)
    )

    Frame.render(batch)
  }

  private def drawLogo(batch: PolygonSpriteBatch): Unit = {
    val logoOffset = (Geometry.ScreenWidth - LogoWidth) / 2
    batch.setColor(1, 1, 1, logoAlpha * logoAlpha)
    batch.draw(
      Tertis.logo,
      logoOffset,
      Geometry.ScreenHeight / 2 - LogoWidth / 2,
      LogoWidth,
      LogoWidth
    )
  }

  private def drawPaused(
      batch: PolygonSpriteBatch
  ): Unit = {
    val color = HighScoreColor ⍺ (logoAlpha * logoAlpha)
    Text.draw(
      batch,
      Text.smallFont,
      color,
      "Game Paused",
      FooterMargin + HighScoreSize - Text.tinyFont.getLineHeight / 2
    )
  }

  private def drawHighScore(
      batch: PolygonSpriteBatch,
      score: Int,
      time: Int
  ): Unit = {
    val color = HighScoreColor ⍺ (logoAlpha * logoAlpha)
    Text.draw(
      batch,
      Text.smallFont,
      color,
      f"High Score: $score%,d",
      FooterMargin + HighScoreSize
    )
    Text.draw(
      batch,
      Text.tinyFont,
      color,
      time.seconds.toHumanString,
      FooterMargin + HighScoreSize - Text.smallFont.getLineHeight
    )
    if (Tertis.globalHigh > 0) {
      val (allScore, allTime) =
        if (score > Tertis.globalHigh) (score -> time)
        else (Tertis.globalHigh -> Tertis.globalTime)
      Text.draw(
        batch,
        Text.tinyFont,
        color,
        f"All Time: $allScore%,d in " + allTime.seconds.toHumanString,
        (FooterMargin + HighScoreSize - Text.smallFont.getLineHeight + Text.tinyFont.getLineHeight) / 2
      )
    }
  }

  def play(): Unit = {
    if (Prefs.Instructed.booleanValue.contains(true)) {
      state = PlayState
      Frame.targetAlpha = 1f
    } else {
      state = HelpPlayState
      Prefs.Instructed.set(true)
    }
  }
}

object Home {
  def apply(game: Game): Home = new Home(Some(game))

  val LogoFadeInSeconds = 1f
  val PlayDelaySeconds = 0.3f
  val PlayFadeInSeconds = .3f

  val LogoFadeOutSeconds = .5f
  val PlayFadeOutSeconds = .3f

  val Title = "Тэятис"

  val HighScoreColor = new Color(.7f, .7f, .7f, 1f)

  sealed trait State

  case object HomeState extends State
  case object HelpState extends State
  case object HelpPlayState extends State
  case object SettingsState extends State
  case object PlayState extends State
}
