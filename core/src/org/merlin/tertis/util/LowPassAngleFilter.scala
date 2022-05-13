package org.merlin.tertis.util

import com.badlogic.gdx.math.MathUtils

// https://stackoverflow.com/a/18911252
class LowPassAngleFilter {
  import LowPassAngleFilter._

  private var index = 0
  private val values = Array.fill(N)(0f)
  private var sumSin = 0f
  private var sumCos = 0f
  var value = 0f

  def add(angle: Float): Unit = {
    sumSin += MathUtils.sinDeg(angle)
    sumCos += MathUtils.cosDeg(angle)
    values.update(index % N, angle)
    index = index + 1
    if (index > N) {
      val old = values(index % N)
      sumSin -= MathUtils.sinDeg(old)
      sumCos -= MathUtils.cosDeg(old)
    }
    val size = index min N
    value = MathUtils.radDeg * MathUtils.atan2(sumSin / size, sumCos / size)
  }
}

object LowPassAngleFilter {
  private final val N = 20
}
