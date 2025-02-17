package org.merlin.tertis

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.PixmapTextureData
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input}
import org.merlin.tertis.common.Starfield
import org.merlin.tertis.home.Home
import org.merlin.tertis.util.{GarbageCan, TextureWrapper}

import java.util.Properties
import scala.compiletime.uninitialized

class Tertis extends ApplicationAdapter:
  import Tertis.garbage

  private var batch: PolygonSpriteBatch = uninitialized
  private var scene: Scene              = uninitialized

  override def create(): Unit =

    Gdx.input.setCatchKey(Input.Keys.BACK, true)

    Prefs.loadPreferences()

    batch = garbage.add(new PolygonSpriteBatch())

    val properties = new Properties
    val is         = Tertis.getClass.getResourceAsStream("/app.properties")
    if is ne null then
      properties.load(is)
      is.close()

    Tertis.version = properties.getProperty("version", "Unknown")
    Tertis.key = properties.getProperty("key", "unset")

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
    Tertis.metaKey = TextureWrapper.load("meta-key.png") // linear filter doesn't help

    Tertis.click = Tertis.loadSound("click.mp3")
    Tertis.drop = Tertis.loadSound("drop.mp3")
    Tertis.crash = Tertis.loadSound("crash.mp3")
    Tertis.end = Tertis.loadSound("triangle.mp3")

    Tertis.pixture = Tertis.solidTexture(1f, 1f, 1f, 1f)

    Text.loadFonts()

    setScene(new Home)

  override def render(): Unit =
    val delta = Gdx.graphics.getDeltaTime
    Starfield.update(delta)
    scene.update(delta) foreach setScene
    ScreenUtils.clear(0, 0, 0, 1)
    batch.begin()
    Starfield.render(batch)
    scene.render(batch)
    batch.end()

  override def dispose(): Unit =
    garbage.dispose()

  private def setScene(newScene: Scene): Unit =
    scene = newScene
    Gdx.input.setInputProcessor(scene.init())

object Tertis:
  implicit val garbage: GarbageCan = new GarbageCan

  var version: String = uninitialized
  var key: String     = uninitialized

  var logo: TextureWrapper = uninitialized
  var play: TextureWrapper = uninitialized

  var separator: TextureWrapper   = uninitialized
  var tap: TextureWrapper         = uninitialized
  var swipeLeft: TextureWrapper   = uninitialized
  var swipeRight: TextureWrapper  = uninitialized
  var swipeDown: TextureWrapper   = uninitialized
  var swipeUpDown: TextureWrapper = uninitialized

  var soundOff: TextureWrapper = uninitialized
  var soundOn: TextureWrapper  = uninitialized
  var musicOff: TextureWrapper = uninitialized
  var musicOn: TextureWrapper  = uninitialized
  var help: TextureWrapper     = uninitialized
  var settings: TextureWrapper = uninitialized
  var close: TextureWrapper    = uninitialized
  var checkOn: TextureWrapper  = uninitialized
  var checkOff: TextureWrapper = uninitialized
  var trash: TextureWrapper    = uninitialized
  var arrowKey: TextureWrapper = uninitialized
  var metaKey: TextureWrapper  = uninitialized

  var pixture: Texture = uninitialized

  var click: Sound = uninitialized
  var drop: Sound  = uninitialized
  var crash: Sound = uninitialized
  var end: Sound   = uninitialized

  var globalHigh: Int = uninitialized
  var globalTime: Int = uninitialized

  def mobile: Boolean = isMobile(Gdx.app.getType)

  private def isMobile(tpe: ApplicationType) =
    tpe == ApplicationType.Android || tpe == ApplicationType.iOS

  private def loadSound(path: String)(implicit garbage: GarbageCan): Sound =
    garbage.add(Gdx.audio.newSound(Gdx.files.internal(path)))

  private def solidTexture(r: Float, g: Float, b: Float, a: Float)(implicit
    garbage: GarbageCan
  ): Texture =
    val pixel = new Pixmap(1, 1, Format.RGBA8888)
    pixel.setColor(r, g, b, a)
    pixel.fill()
    val td    = new PixmapTextureData(pixel, null, false, true)
    garbage.add(new Texture(td))
