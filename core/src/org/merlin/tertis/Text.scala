package org.merlin.tertis

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, PolygonSpriteBatch}
import org.merlin.tertis.Geometry.Dimension
import org.merlin.tertis.home.Home
import org.merlin.tertis.util.GarbageCan

import scala.compiletime.uninitialized

object Text:
  def loadFonts()(implicit garbage: GarbageCan): Unit =
    val generator = new FreeTypeFontGenerator(
      Gdx.files.internal("OpenSans-Regular.ttf")
    )
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter
    parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + CharExtras
    parameter.size = (Dimension * 3 / 4).toInt
    mediumFont = garbage.add(generator.generateFont(parameter))
    parameter.size = (Dimension * 9 / 16).toInt
    smallFont = garbage.add(generator.generateFont(parameter))
    parameter.size = (Dimension * 3 / 8).toInt
    tinyFont = garbage.add(generator.generateFont(parameter))
    generator.dispose()

  private val CharExtras = Home.Title

  var mediumFont: BitmapFont = uninitialized
  var smallFont: BitmapFont  = uninitialized
  var tinyFont: BitmapFont   = uninitialized

  def draw(
    batch: PolygonSpriteBatch,
    font: BitmapFont,
    color: Color,
    text: String,
    y: Float,
    x: Float = 0f,
    width: Float = Geometry.ScreenWidth
  ): Unit =
    font.setColor(color)
    font.draw(batch, text, x, y, width, CenterAlign, false)

  def draw(
    batch: PolygonSpriteBatch,
    font: BitmapFont,
    color: Color,
    text: String,
    position: GlyphLayout => (Float, Float)
  ): Unit =
    font.setColor(color)
    val layout = new GlyphLayout(font, text)
    val (x, y) = position(layout)
    font.draw(batch, layout, x, y)
