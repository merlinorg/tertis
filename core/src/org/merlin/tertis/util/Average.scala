package org.merlin.tertis.util

class Average {
  private var average = 0f
  private var count = 0

  def +=(f: Float): Unit = {
    average += f
    count += 1
  }

  def value: Float = average / (count max 1) // heh

  def reset(): Unit = {
    average = 0f
    count = 0
  }
}
