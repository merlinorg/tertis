package org.merlin.tertis
package home

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Geometry._
import org.merlin.tertis.Scene
import org.merlin.tertis.common.{Frame, Starfield}
import org.merlin.tertis.game.Game
import org.merlin.tertis.util.TextureWrapper

// I don't like that this fades in after home fades out, but modeling this as a separate scene
// makes life so much easier.
class Help(home: Home, game: Option[Game] = None) extends Scene {

  import Help._

  var state: State = HelpState
  var alpha: Float = 0f
  var instructed: Float = 0f

  private val IconSize = (Dimension * 3 / 4).floor

  val closeIcon = List(
    new BasicIcon(
      Geometry.ScreenWidth - IconSize * 2,
      Geometry.ScreenHeight - IconSize * 2,
      IconSize,
      Tertis.close,
      () => {
        state = ExitState
      }
    )
  )

  def icons: List[Icon] = game.isDefined.fold(Nil, closeIcon)

  override def init(): HelpControl =
    new HelpControl(this)

  override def update(delta: Float): Option[Scene] = {
    Starfield.update(delta)
    Frame.update(delta)
    if (state == HelpState) {
      alpha = alpha.alphaUp(delta, InstructionsFadeInSeconds)
      if (game.isDefined) {
        instructed = instructed + delta
        if (instructed >= AutoInstructionsSeconds) continue()
      }
      None
    } else {
      alpha = alpha.alphaDown(delta, InstructionsFadeOutSeconds)
      val awaitFrame = (state == ContinueState) && game.isDefined
      (alpha == 0f && (!awaitFrame || Frame.alpha == 1f))
        .option(game.filter(_ => state == ContinueState).getOrElse(home))
    }
  }

  override def render(batch: PolygonSpriteBatch): Unit = {
    Starfield.render(batch)

    if (Tertis.mobile) {
      mobileHelp(batch)
    } else {
      desktopIcons.foreach(_.draw(batch, alpha * alpha))
    }
    icons.foreach(_.draw(batch, alpha * alpha))

    Frame.render(batch)
  }

  private val DesktopIconLeft = IconSize * 2
  private val DesktopIconInterval = IconSize * 3
  private val DesktopIconsTop = Geometry.ScreenHeight - IconSize * 5

  val desktopIcons: List[Icon] = List(
    new KeyIcon(
      DesktopIconLeft,
      DesktopIconsTop,
      IconSize,
      Tertis.arrowKey,
      0f,
      "Right",
      "Right arrow key"
    ),
    new KeyIcon(
      DesktopIconLeft,
      DesktopIconsTop - DesktopIconInterval,
      IconSize,
      Tertis.arrowKey,
      180f,
      "Left",
      "Left arrow key"
    ),
    new KeyIcon(
      DesktopIconLeft,
      DesktopIconsTop - DesktopIconInterval * 2,
      IconSize,
      Tertis.arrowKey,
      90f,
      "Rotate",
      "Up arrow key"
    ),
    new KeyIcon(
      DesktopIconLeft,
      DesktopIconsTop - DesktopIconInterval * 3,
      IconSize,
      Tertis.arrowKey,
      270f,
      "Drop",
      "Down arrow key"
    ),
    new KeyIcon(
      DesktopIconLeft,
      DesktopIconsTop - DesktopIconInterval * 4,
      IconSize,
      Tertis.metaKey,
      0f,
      "Velocitator",
      "Shift/control/alt/option key"
    )
  )

  private val mobileHelps = List(
    MobileHelp(Tertis.tap, "Left", "Tap", 0, 1),
    MobileHelp(Tertis.swipeUpDown, "Rotate", "Swipe up/down", 0, 3),
    MobileHelp(Tertis.tap, "Right", "Tap", 2, 1),
    MobileHelp(Tertis.swipeUpDown, "Rotate", "Swipe up/down", 2, 3),
    MobileHelp(Tertis.swipeLeft, "Slide left", "Swipe left", 1, 0),
    MobileHelp(Tertis.swipeRight, "Slide right", "Swipe right", 1, 2),
    MobileHelp(Tertis.swipeDown, "Drop", "Swipe down", 1, 4)
  )

  private def mobileHelp(batch: PolygonSpriteBatch): Unit = {
    val IconSize = Dimension
    val color = Icon.White ⍺⍺ alpha
    val grey = Icon.Grey ⍺⍺ alpha
    val columnSpacing = Dimension / 2
    val columnWidth = (Geometry.ScreenWidth - columnSpacing * 6) / 3
    val scale = IconSize / 512
    val helpEntryHeight =
      Text.smallFont.getLineHeight + Text.tinyFont.getLineHeight + Dimension * 5 / 4
    val totalHeight = helpEntryHeight * 3 + 2 * Dimension * 2
    val initialY =
      Geometry.ScreenHeight - (Geometry.ScreenHeight - totalHeight) / 2
    batch.setColor(color)
    Text.smallFont.setColor(color)
    mobileHelps.foreach { help =>
      val w = (help.icon.width * scale).floor
      val h = (help.icon.height * scale).floor
      val x = (columnSpacing + (columnWidth + columnSpacing * 2) * help.x).floor
      val y = (initialY - 2 * Dimension * help.y).floor
      Text.draw(batch, Text.smallFont, color, help.label, y, x, columnWidth)
      batch.draw(
        help.icon,
        (x + (columnWidth - w) / 2).floor,
        (y - Text.smallFont.getLineHeight - (h + IconSize) / 2).floor,
        w,
        h
      )
      Text.draw(
        batch,
        Text.tinyFont,
        grey,
        help.desc,
        (y - Text.smallFont.getLineHeight - Dimension * 5 / 4).floor,
        x,
        columnWidth
      )
    }
    batch.draw(
      Tertis.separator,
      (columnWidth + columnSpacing * 2 - Dimension / 32).floor,
      initialY - totalHeight,
      (Dimension / 16).ceil,
      totalHeight
    )
    batch.draw(
      Tertis.separator,
      (columnWidth * 2 + columnSpacing * 4 - Dimension / 32).floor,
      initialY - totalHeight,
      (Dimension / 16).ceil,
      totalHeight
    )

  }

  def exit(): Unit = {
    state = ExitState
  }

  def continue(): Unit = {
    if (game.isDefined) Frame.targetAlpha = 1f
    state = ContinueState
  }
}

object Help {
  val InstructionsFadeInSeconds = .3f
  val InstructionsFadeOutSeconds = .3f
  val AutoInstructionsSeconds = 5f

  val Red = new Color(.855f, .075f, .102f, 1f)
  val Yellow = new Color(1f, .937f, 0f, 1f)
  val White = new Color(.7f, .7f, .7f, 1f)

  sealed trait State
  case object HelpState extends State
  case object ExitState extends State
  case object ContinueState extends State

  private final case class MobileHelp(
      icon: TextureWrapper,
      label: String,
      desc: String,
      x: Int,
      y: Int
  )
}
