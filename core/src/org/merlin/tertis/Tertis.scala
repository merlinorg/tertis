package org.merlin.tertis

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input}
import org.merlin.tertis.common.Starfield
import org.merlin.tertis.home.Home
import org.merlin.tertis.util.TextureWrapper

class Tertis extends ApplicationAdapter {

  private var batch: PolygonSpriteBatch = _
  private var scene: Scene = _

  override def create(): Unit = {
    Gdx.input.setCatchKey(Input.Keys.BACK, true)

    Prefs.loadPreferences()

    batch = new PolygonSpriteBatch()

    Tertis.logo = TextureWrapper.load("logo.png")
    Tertis.play = TextureWrapper.load("play.png")

    Tertis.separator = TextureWrapper.load("separator.png")
    Tertis.tap = TextureWrapper.load("tap.png")
    Tertis.swipeDown = TextureWrapper.load("swipe-down.png")
    Tertis.swipeLeft = TextureWrapper.load("swipe-left.png")
    Tertis.swipeRight = TextureWrapper.load("swipe-right.png")
    Tertis.swipeUpDown = TextureWrapper.load("swipe-up-down.png")

    Tertis.soundOff = TextureWrapper.load("sound-off.png")
    Tertis.soundOn = TextureWrapper.load("sound-on.png")
    Tertis.musicOff = TextureWrapper.load("music-off.png")
    Tertis.musicOn = TextureWrapper.load("music-on.png")
    Tertis.settings = TextureWrapper.load("settings.png")
    Tertis.help = TextureWrapper.load("help.png")
    Tertis.close = TextureWrapper.load("close.png")
    Tertis.checkOn = TextureWrapper.load("check-on.png")
    Tertis.checkOff = TextureWrapper.load("check-off.png")
    Tertis.trash = TextureWrapper.load("trash.png")
    Tertis.arrowKey = TextureWrapper.load("arrow-key.png")
    Tertis.metaKey =
      TextureWrapper.load("meta-key.png") // linear filter doesn't help

    Tertis.click = Gdx.audio.newSound(Gdx.files.internal("click.mp3"))
    Tertis.drop = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
    Tertis.crash = Gdx.audio.newSound(Gdx.files.internal("crash.mp3"))
    Tertis.end = Gdx.audio.newSound(Gdx.files.internal("triangle.mp3"))

    // TODO: dispose of everything

    Text.loadFonts()

    setScene(new Home)
  }

  override def render(): Unit = {
    val delta = Gdx.graphics.getDeltaTime
    Starfield.update(delta)
    scene.update(delta) foreach setScene
    ScreenUtils.clear(0, 0, 0, 1)
    batch.begin()
    Starfield.render(batch)
    scene.render(batch)
    batch.end()
  }

  override def dispose(): Unit = {
    batch.dispose()
  }

  private def setScene(newScene: Scene): Unit = {
    scene = newScene
    Gdx.input.setInputProcessor(scene.init())
  }

}

object Tertis {
  var logo: TextureWrapper = _
  var play: TextureWrapper = _

  var separator: TextureWrapper = _
  var tap: TextureWrapper = _
  var swipeLeft: TextureWrapper = _
  var swipeRight: TextureWrapper = _
  var swipeDown: TextureWrapper = _
  var swipeUpDown: TextureWrapper = _

  var soundOff: TextureWrapper = _
  var soundOn: TextureWrapper = _
  var musicOff: TextureWrapper = _
  var musicOn: TextureWrapper = _
  var help: TextureWrapper = _
  var settings: TextureWrapper = _
  var close: TextureWrapper = _
  var checkOn: TextureWrapper = _
  var checkOff: TextureWrapper = _
  var trash: TextureWrapper = _
  var arrowKey: TextureWrapper = _
  var metaKey: TextureWrapper = _

  var click: Sound = _
  var drop: Sound = _
  var crash: Sound = _
  var end: Sound = _

  def mobile: Boolean = isMobile(Gdx.app.getType)

  private def isMobile(tpe: ApplicationType) =
    tpe == ApplicationType.Android || tpe == ApplicationType.iOS

  val pixture = solidTexture(1f, 1f, 1f, 1f)

  def solidTexture(r: Float, g: Float, b: Float, a: Float): Texture = {
    val pixel = new Pixmap(1, 1, Format.RGBA8888)
    pixel.setColor(r, g, b, a)
    pixel.fill()
    new Texture(pixel)
  }
}