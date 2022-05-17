package org.merlin.tertis.util

import com.badlogic.gdx.utils.Disposable

import scala.collection.mutable

class GarbageCan extends Disposable {
  private val trash = mutable.ListBuffer.empty[Disposable]
  def add[A <: Disposable](a: A): A = {
    trash.append(a)
    a
  }

  override def dispose(): Unit = {
    trash.foreach(_.dispose())
    trash.clear()
  }

}
