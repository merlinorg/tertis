package org.merlin.tertis
package util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.utils.Disposable

class TextureWrapper(val pixmap: Pixmap) extends Disposable {

  val width: Int = pixmap.getWidth
  val height: Int = pixmap.getHeight
  val texture = new Texture(pixmap)

  override def dispose(): Unit = {
    texture.dispose()
    pixmap.dispose()
  }

}

object TextureWrapper {
  def load(path: String)(implicit garbageCan: GarbageCan): TextureWrapper = {
    val fileHandle = Gdx.files.internal(path)
    val pixmap = new Pixmap(fileHandle)
    garbageCan.add(new TextureWrapper(pixmap))
  }

  implicit def toTexture(wrapper: TextureWrapper): Texture = wrapper.texture

}
