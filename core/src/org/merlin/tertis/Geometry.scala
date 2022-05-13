package org.merlin.tertis

import com.badlogic.gdx.Gdx

object Geometry {
  val Columns = 10
  val Rows = 20
  // dimension of one block
  val Dimension: Int =
    (Gdx.graphics.getWidth * 2 / (Columns * 2 + 1)) min (Gdx.graphics.getHeight * 2 / (Rows * 2 + 5))
  val OffsetX: Int = (Gdx.graphics.getWidth - Dimension * Columns) / 2
  val OffsetY: Int =
    (Gdx.graphics.getHeight - Dimension * (Rows + 2) + Dimension / 2) / 2
  val Bevel: Int = (Dimension / 50) max 1

}
