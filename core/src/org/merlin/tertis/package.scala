package org.merlin.tertis

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Peripheral
import com.badlogic.gdx.graphics.Color

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

def compassAvailable: Boolean =
  input.isPeripheralAvailable(Peripheral.Compass)

val CenterAlign = 1

// Things kinda stolen from scaloi

extension (self: Any)
  /** Replace this value with [a]. */
  infix def as[A](a: A): A = a

extension (self: Float)

  /** Clamp this value between 0f and 1f inclusive. */
  def clamp: Float = clamp(1f)

  /** Clamp this value between 0f and [max] inclusive. */
  def clamp(max: Float): Float =
    if self < 0f then 0f else if self > max then max else self

  /** Increases an alpha by [delta] time interval spread over [seconds] seconds limited to 1f. */
  def alphaUp(delta: Float, seconds: Float): Float =
    (self + delta / seconds) min 1f

  /** Decreases an alpha by [delta] time interval spread over [seconds] seconds limited to 0f. */
  def alphaDown(delta: Float, seconds: Float): Float =
    (self - delta / seconds) max 0f

extension (self: Boolean)
  def option[A](a: => A): Option[A] = Option.when(self)(a)

  def fold[A](ifTrue: => A, ifFalse: => A): A = if self then ifTrue else ifFalse

extension (self: FiniteDuration)
  def toFiniteDuration(tu: TimeUnit): FiniteDuration =
    FiniteDuration(self.toUnit(tu).toLong, tu)

  protected def largestUnit: Option[TimeUnit] =
    TimeUnit.values.findLast(u => self.toUnit(u) >= 1.0)

  def toHumanString: String =
    largestUnit.fold("no time at all"): u =>
      val scaled    = toFiniteDuration(u)
      val v         = TimeUnit.values.apply(u.ordinal - 1)
      val modulus   = FiniteDuration(1, u).toUnit(v).toInt
      val remainder = self.toUnit(v).toLong % modulus
      if remainder > 0 then scaled.toString + ", " + FiniteDuration(remainder, v)
      else scaled.toString

extension [A](self: Option[A])
  def isTrue(using Booleate: Booleate[A]): Boolean =
    self.fold(false)(Booleate.value)

  def isFalse(using Booleate: Booleate[A]): Boolean =
    self.fold(false)(Booleate.unvalue)

private trait Booleate[A]:
  def value(a: A): Boolean

  final def unvalue(a: A): Boolean = !value(a)

given Booleate[Boolean] = b => b

extension (self: Color)

  /** Returns a new colour with alpha set to [alpha]. */
  def withAlpha(alpha: Float): Color =
    new Color(self.r, self.g, self.b, alpha)

  /** Returns a new colour with alpha multiplied by [alpha]. */
  def ⍺(alpha: Float): Color =
    new Color(self.r, self.g, self.b, self.a * alpha)

  /** Returns a new colour with alpha multiplied by [alpha]². */
  def ⍺⍺(alpha: Float): Color =
    new Color(self.r, self.g, self.b, self.a * alpha * alpha)
