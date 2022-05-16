package org.merlin.tertis
package game

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.merlin.tertis.Geometry._

class NextUp(game: Game) {
  private var previous = Option.empty[Block]
  private var previousAlpha = 0f

  private var bag = Iterator.empty[Block]
  private var next: Block = nextBlock
  private var nextAlpha = 0f

  def shift(): Block = {
    val current = next
    previous = Some(next)
    previousAlpha = nextAlpha
    next = nextBlock
    nextAlpha = 0f
    current
  }

  private def nextBlock: Block =
    if (!game.weakRandomness) {
      Block.random
    } else {
      if (!bag.hasNext) bag = Block.bag
      bag.next()
    }

  def update(delta: Float): Unit = {
    previousAlpha = (previousAlpha - delta / FadeOutSeconds) max 0f
    nextAlpha = (nextAlpha + delta / FadeInSeconds) min 1f
  }

  def render(batch: PolygonSpriteBatch): Unit = {
    previous foreach { prev =>
      draw(batch, prev, previousAlpha, -.5f)
    }
    draw(batch, next, nextAlpha, 1f)
  }

  private def draw(
      batch: PolygonSpriteBatch,
      block: Block,
      alpha: Float,
      y: Float
  ): Unit = {
    val Small = (Dimension / 2).floor
    // It is known that the shapes have width 2..4 and height 1..2
    val nextX =
      (OffsetX + Columns * Dimension - (4 + block.size) * Small / 2).floor
    val nextY =
      (OffsetY + Rows * Dimension + (1 - block.vOffset) * Small + (y * (1f - alpha) * Dimension)).floor
    val nextColor = block.getColor ⍺⍺ alpha ⍺ 0.5f
    block.eachSquare(
      0,
      (i, j, test) =>
        BlockRenderer.render(
          batch,
          nextColor,
          nextX + i * Small,
          nextY + j * Small,
          Small,
          Small,
          Bevel,
          test
        )
    )
  }

  val FadeOutSeconds = 0.5f
  val FadeInSeconds = 0.5f
}
