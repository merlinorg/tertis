package org.merlin.tertis.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{
  PolygonRegion,
  PolygonSprite,
  PolygonSpriteBatch,
  TextureRegion
}
import org.merlin.tertis.Tertis

object BlockRenderer {

  def render(
      batch: PolygonSpriteBatch,
      color: Color,
      x: Int,
      y: Int,
      w: Int,
      h: Int,
      v: Int, // bevel
      test: (Int, Int) => Boolean
  ): Unit = {
    batch.setColor(color)
    batch.draw(Tertis.pixture, x, y, w, h)
    Shadow.a = color.a * .1f
    Highlight.a = color.a * .3f
    Highlight
    // This could be done much more efficiently without the PolygonRegion and PolygonSprite,
    // by just computing the packed vertex information and rendering polygons directly
    def drawPoly(tint: Color, vertices: Float*): Unit = {
      assert(vertices.length == 8 || vertices.length == 6)
      val polygonRegion = new PolygonRegion(
        new TextureRegion(Tertis.pixture),
        vertices.toArray,
        if (vertices.length == 8) // counterclockwise triangles
          Array[Short](0, 1, 2, 0, 2, 3)
        else Array[Short](0, 1, 2)
      )
      val poly = new PolygonSprite(polygonRegion)
      poly.setPosition(x, y)
      poly.setColor(tint)
      poly.draw(batch)
    }
    // I just overdraw twice for the stronger shade
    if (!test(0, -1)) { // nothing below
      val l = if (test(-1, 0)) 0 else v
      val r = if (test(1, 0)) 0 else v
      drawPoly(Shadow, 0, 0, w, 0, w - r * 2, v * 2, l * 2, v * 2)
      drawPoly(Shadow, 0, 0, w, 0, w - r, v, l, v)
    } else if (test(-1, 0) && !test(-1, -1)) {
      drawPoly(Shadow, 0, 0, v * 2, v * 2, 0, v * 2)
      drawPoly(Shadow, 0, 0, v, v, 0, v)
    } else if (test(1, 0) && !test(1, -1)) {
      drawPoly(Shadow, w - v * 2, 0, w, 0, w, v * 2, w - v * 2, v * 2)
      drawPoly(Shadow, w - v, 0, w, 0, w, v, w - v, v)
    }
    if (!test(1, 0)) { // nothing to the right
      val b = if (test(0, -1)) 0 else v
      val t = if (test(0, 1)) 0 else v
      drawPoly(Shadow, w - v * 2, b * 2, w, 0, w, h, w - v * 2, h - t * 2)
      drawPoly(Shadow, w - v, b, w, 0, w, h, w - v, h - t)
    } else if (test(0, 1) && !test(1, 1)) {
      drawPoly(Shadow, w - v * 2, h - v * 2, w, h, w - v * 2, h)
      drawPoly(Shadow, w - v, h - v, w, h, w - v, h)
    }
    if (!test(0, 1)) { // nothing above
      val l = if (test(-1, 0)) 0 else v
      val r = if (test(1, 0)) 0 else v
      drawPoly(Highlight, l * 2, h - v * 2, w - r * 2, h - v * 2, w, h, 0, h)
      drawPoly(Highlight, l, h - v, w - r, h - v, w, h, 0, h)
    } else if (test(1, 0) && !test(1, 1)) {
      drawPoly(Highlight, w - v * 2, h - v * 2, w, h - v * 2, w, h)
      drawPoly(Highlight, w - v, h - v, w, h - v, w, h)
    } else if (test(-1, 0) && !test(-1, 1)) {
      drawPoly(Highlight, 0, h - v * 2, v * 2, h - v * 2, v * 2, h, 0, h)
      drawPoly(Highlight, 0, h - v, v, h - v, v, h, 0, h)
    }
    if (!test(-1, 0)) { // nothing to the left
      val b = if (test(0, -1)) 0 else v
      val t = if (test(0, 1)) 0 else v
      drawPoly(Highlight, 0, 0, v * 2, b * 2, v * 2, h - t * 2, 0, h)
      drawPoly(Highlight, 0, 0, v, b, v, h - t, 0, h)
    } else if (test(0, -1) && !test(-1, -1)) {
      drawPoly(Highlight, 0, 0, v * 2, 0, v * 2, v * 2)
      drawPoly(Highlight, 0, 0, v, 0, v, v)
    }
  }

  private val Shadow = new Color(0, 0, 0, .1f)
  private val Highlight = new Color(1, 1, 1, .3f)

}
