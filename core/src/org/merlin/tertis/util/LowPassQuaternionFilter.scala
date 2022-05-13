package org.merlin.tertis.util

import com.badlogic.gdx.math.{Matrix3, Quaternion}

class LowPassQuaternionFilter(n: Int) {
  private var index = 0
  private val values = Array.fill(n)(new Quaternion())
  val value = new Quaternion()
  val conjugate = new Quaternion()

  def add(matrix: Matrix3): Unit = {
    values(index).setFromMatrix(matrix)
//    values(index).x = -values(index).x
//    values(index).y = -values(index).y
    index = (index + 1) % n
    value.slerp(values) // yeah yeah, first few values will be off
    conjugate.set(value).conjugate()
  }
}
