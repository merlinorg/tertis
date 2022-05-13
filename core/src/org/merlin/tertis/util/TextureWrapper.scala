package org.merlin.tertis.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Pixmap, Texture}

class TextureWrapper(val pixmap: Pixmap) {

  val width = pixmap.getWidth
  val height = pixmap.getHeight
  val texture = new Texture(pixmap)

  def dispose(): Unit = {
    texture.dispose()
    pixmap.dispose()
  }

}

object TextureWrapper {
  def load(path: String): TextureWrapper = {
    val fileHandle = Gdx.files.internal(path)
    val pixmap = new Pixmap(fileHandle)
    new TextureWrapper(pixmap)
  }

  implicit def toTexture(wrapper: TextureWrapper): Texture = wrapper.texture

}
