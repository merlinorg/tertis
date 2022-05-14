package org.merlin.tertis

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, PolygonSpriteBatch}
import org.merlin.tertis.Geometry.Dimension
import org.merlin.tertis.home.Home

object Text {
  def loadFonts(): Unit = {
    val generator = new FreeTypeFontGenerator(
      Gdx.files.internal("OpenSans-Regular.ttf")
    )
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter
    parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + CharExtras
    parameter.size = Dimension.toInt
    bigFont = generator.generateFont(parameter)
    parameter.size = (Dimension * 3 / 4).toInt
    mediumFont = generator.generateFont(parameter)
    parameter.size = (Dimension * 9 / 16).toInt
    smallFont = generator.generateFont(parameter)
    parameter.size = (Dimension * 3 / 8).toInt
    tinyFont = generator.generateFont(parameter)
    generator.dispose()
  }

  private val CharExtras = Home.Title

  var bigFont: BitmapFont = _
  var mediumFont: BitmapFont = _
  var smallFont: BitmapFont = _
  var tinyFont: BitmapFont = _

  def draw(
      batch: PolygonSpriteBatch,
      font: BitmapFont,
      color: Color,
      text: String,
      y: Float,
      x: Float = 0f,
      width: Float = Geometry.ScreenWidth
  ): Unit = {
    font.setColor(color)
    font.draw(batch, text, x, y, width, CenterAlign, false)
  }

  def draw(
      batch: PolygonSpriteBatch,
      font: BitmapFont,
      color: Color,
      text: String,
      position: GlyphLayout => (Float, Float)
  ): Unit = {
    font.setColor(color)
    val layout = new GlyphLayout(font, text)
    val (x, y) = position(layout)
    font.draw(batch, layout, x, y)
  }

}
