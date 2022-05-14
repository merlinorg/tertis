package org.merlin.tertis

import com.badlogic.gdx.Gdx

object Geometry {
  val Columns = 10
  val Rows = 20

  val ScreenWidth: Float = Gdx.graphics.getWidth.toFloat
  val ScreenHeight: Float = Gdx.graphics.getHeight.toFloat
  // dimension of one block
  val Dimension: Float =
    ((ScreenWidth * 2 / (Columns * 2 + 1)) min (ScreenHeight * 2 / (Rows * 2 + 5))).floor
  val OffsetX: Float = ((ScreenWidth - Dimension * Columns) / 2).floor
  val OffsetY: Float =
    ((ScreenHeight - Dimension * (Rows + 2) + Dimension / 2) / 2).floor
  val Bevel: Float = (Dimension / 48).floor max 1

}
