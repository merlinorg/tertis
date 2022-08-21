package org.merlin.tertis

import com.badlogic.gdx.Net.{HttpMethods, HttpRequest, HttpResponseListener}
import com.badlogic.gdx.net.HttpParametersUtils
import com.badlogic.gdx.{Gdx, Net}
import org.merlin.tertis.game.Score

import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

object ScoreIO {
  def loadScore(): Unit =
    try {
      val get = new HttpRequest(HttpMethods.GET)
      val mobile = Tertis.mobile
      val zen = Prefs.ZenMode.isTrue
      val weak = Prefs.WeakRandomness.isTrue
      get.setUrl(s"$URL?mobile=$mobile&zen=$zen&weak=$weak")
      Gdx.net.sendHttpRequest(get, new ScoreListener)
    } catch {
      case NonFatal(e) =>
        Gdx.app.log("loadScore", s"exception $e")
    }

  def saveScore(score: Score): Unit =
    try {
      for {
        data <- scorePayload(score)
        signed = signPayload(data)
      } {
        val post = new HttpRequest(HttpMethods.POST)
        post.setUrl(URL)
        post.setHeader("Content-Type", "application/x-www-form-urlencoded")
        post.setContent(
          HttpParametersUtils.convertHttpParameters(signed.asJava)
        )
        Gdx.net.sendHttpRequest(post, new IOListener("saveScore"))
      }
    } catch {
      case NonFatal(e) =>
        Gdx.app.log("saveScore", s"exception $e")
    }

  def scorePayload(score: Score): Option[Map[String, String]] =
    for {
      uuid <- Prefs.UniqueIdentifier.stringValue
      highScore <- Prefs.HighScore.intValue
      highTime <- Prefs.HighTime.intValue
      highRows <- Prefs.HighRows.intValue
    } yield Map(
      "app" -> "Tertis",
      "version" -> Tertis.version,
      "uuid" -> uuid,
      "zen" -> Prefs.ZenMode.isTrue,
      "weak" -> Prefs.WeakRandomness.isTrue,
      "tilt" -> Prefs.TiltSpeed.isTrue,
      "mobile" -> Tertis.mobile,
      "score" -> score.score,
      "time" -> score.time,
      "rows" -> score.rows,
      "highScore" -> highScore,
      "highTime" -> highTime,
      "highRows" -> highRows,
      "timestamp" -> Instant.now.toString
    ).view.mapValues(v => v.toString).toMap
  val URL = "https://api.merlin.org/tertis/score"

  val HMAC_SHA256 = "HmacSHA256"

  def signPayload(payload: Map[String, String]): Map[String, String] = {
    val mac = Mac.getInstance(HMAC_SHA256)
    mac.init(new SecretKeySpec(Tertis.key.getBytes("UTF-8"), HMAC_SHA256))
    val plaintext =
      payload.toList.sortBy(_._1).map(t => s"${t._1}=${t._2}").mkString(",")
    val checksum = mac.doFinal(plaintext.getBytes("UTF-8"))
    payload + ("cksum" -> Base64.getEncoder.encodeToString(checksum))
  }

}

class IOListener(tag: String) extends HttpResponseListener {
  override def handleHttpResponse(
      httpResponse: Net.HttpResponse
  ): Unit =
    Gdx.app.log(tag, httpResponse.getResultAsString)

  override def failed(t: Throwable): Unit =
    Gdx.app.log(tag, s"post failed: $t")

  override def cancelled(): Unit =
    Gdx.app.log(tag, "post canceled")

}

class ScoreListener extends IOListener("loadScore") {
  override def handleHttpResponse(httpResponse: Net.HttpResponse): Unit =
    httpResponse.getResultAsString match {
      case ScoreRE(score, time) =>
        Tertis.globalHigh = score.toInt
        Tertis.globalTime = time.toInt
      case _ =>
    }

  final val ScoreRE = "^([0-9]+),([0-9]+)$".r
}
