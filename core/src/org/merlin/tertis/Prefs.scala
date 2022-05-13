package org.merlin.tertis

import com.badlogic.gdx.{Gdx, Preferences}

class Pref(key: String) {
  import Prefs.preferences

  def intValue: Option[Int] =
    preferences.contains(key).option(preferences.getInteger(key))
  def longValue: Option[Long] =
    preferences.contains(key).option(preferences.getLong(key))
  def booleanValue: Option[Boolean] =
    preferences.contains(key).option(preferences.getBoolean(key))
  def set(value: Int): Unit = {
    preferences.putInteger(key, value)
    preferences.flush()
  }
  def set(value: Long): Unit = {
    preferences.putLong(key, value)
    preferences.flush()
  }
  def set(value: Boolean): Unit = {
    preferences.putBoolean(key, value)
    preferences.flush()
  }
  def fold[A](ifTrue: => A, ifFalse: => A): A =
    if (booleanValue.isTrue) ifTrue else ifFalse

  def isTrue: Boolean = booleanValue.isTrue
}

object Prefs {
  var preferences: Preferences = _

  def loadPreferences(): Unit = {
    preferences = Gdx.app.getPreferences("tertis")
    // preferences.clear()
  }

  final val HighScore = new Pref("highScore")
  final val HighTime = new Pref("highTime")
  final val HighRows = new Pref("highRows")
  final val AllTime = new Pref("allTime")
  final val Instructed = new Pref("instructed")
  final val MuteAudio = new Pref("muteAudio")
  final val MuteMusic = new Pref("muteMusic")
  final val ZenMode = new Pref("zenMode")
  final val HighContrast = new Pref("highContrast")
  final val TiltSpeed = new Pref("tiltSpeed")
  final val StuffHappens = new Pref("stuffHappens")
}
