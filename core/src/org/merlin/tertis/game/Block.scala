package org.merlin.tertis
package game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

import scala.collection.mutable
import scala.util.Random

trait Test extends ((Int, Int) => Boolean)

/** The symmetric flag causes ---- and _|- to alternate between two positions
  * instead of truly rotating about a center. Subjective, but I find the true
  * rotation to be displeasing.
  */
final class Block(
    color: Color,
    hcColor: Color,
    bits: Array[String],
    symmetric: Boolean = false
) {
  import Block.Solid

  def getColor: Color = Prefs.LowContrast.fold(color, hcColor)

  assert(bits.forall(_.length == bits.length), bits.mkString(","))

  val size: Int = bits.length

  val vOffset: Int = bits.reverse.takeWhile(!_.contains(Solid)).length
  val vWidth: Int = bits.count(_.contains(Solid))

  def forall(rotation: Int, f: (Int, Int) => Boolean): Boolean = {
    var result = true
    for (i <- 0 until size if result) {
      for (j <- 0 until size if result) {
        val (x, y) = translate(rotation, i, j)
        result = bits(x)(y) != Solid || f(i, j)
      }
    }
    result
  }

  def columnOccupied(rotation: Int, i: Int): Boolean =
    (0 until size).exists(j => test(rotation, i, j))

  def firstColumn(rotation: Int): Int =
    (0 until size).find(columnOccupied(rotation, _)).get

  def lastColumn(rotation: Int): Int =
    (0 until size).findLast(columnOccupied(rotation, _)).get

  def exists(rotation: Int, f: (Int, Int) => Boolean): Boolean =
    !forall(rotation, (x, y) => !f(x, y))

  def foreach(rotation: Int, f: (Int, Int) => Unit): Unit =
    forall(rotation, (i, j) => f(i, j) as true)

  def eachSquare[A](
      rotation: Int,
      f: (Int, Int, (Int, Int) => Boolean) => A
  ): Unit = {
    for (i <- 0 until size) {
      for (j <- 0 until size) {
        val (x, y) = translate(rotation, i, j)
        if (bits(x)(y) == Solid) {
          f(i, j, (di, dj) => test(rotation, i + di, j + dj))
        }
      }
    }

  }

  def test(rotation: Int, i: Int, j: Int): Boolean = {
    val (x, y) = translate(rotation, i, j)
    (x >= 0) && (x < size) && (y >= 0) && (y < size) && bits(x)(y) == Solid
  }

  private def translate(rotation: Int, i: Int, j: Int): (Int, Int) =
    (rotation % (symmetric.fold(2, 4))) match {
      case 0 => (size - 1 - j, i)
      case 1 => (size - 1 - i, size - 1 - j)
      case 2 => (j, size - 1 - i)
      case 3 => (i, j)
      case _ => throw new IllegalArgumentException(s"Rotation $rotation")
    }
}

// TODO: support textures for colour blind accessibility...
object Block {
  final val Solid = '#'

  def random: Block = blocks(randomNumber(blocks.length))

  def bag: Iterator[Block] =
    Random.shuffle(blocks).iterator

  // I question the randomness of MathUtils.random for 7
  private def randomNumber(n: Int): Int = {
    if (n == 7) {
      var rnd: Int = n
      while rnd >= n do
        rnd = MathUtils.random.nextInt >>> 29
      rnd
    } else {
      MathUtils.random(n - 1)
    }
  }

  // https://observablehq.com/@shan/oklab-color-wheel
  // oklab colour space for perceptual calmness
  // 6/2/4.51/0.1/255
  // hc: 6/2/3.83/0.14/255
  // hc: 6/2/3.83/0.29/255
  val blocks: List[Block] = List(
    new Block(
      rgb(246, 246, 246),
      rgb(246, 246, 246),
      """
        |##
        |##
        |""".toBlock
    ),
    new Block(
      rgb(235, 245, 255),
      rgb(221, 223, 255),
      """
        |....
        |####
        |....
        |....
        |""".toBlock,
      true
    ),
    new Block(
      rgb(127, 255, 255),
      rgb(0, 255, 255),
      """
          |...
          |..#
          |###
          |""".toBlock
    ),
    new Block(
      rgb(165, 255, 221),
      rgb(0, 255, 181),
      """
        |###
        |..#
        |...
        |""".toBlock
    ),
    new Block(
      rgb(255, 255, 139),
      rgb(255, 253, 0),
      """
        |...
        |##.
        |.##
        |""".toBlock,
      true
    ),
    new Block(
      rgb(255, 222, 183),
      rgb(255, 166, 60),
      """
        |...
        |.##
        |##.
        |""".toBlock,
      true
    ),
    new Block(
      rgb(255, 218, 255),
      rgb(255, 160, 255),
      """
        |...
        |###
        |.#.
        |""".toBlock
    )
  )

  private def rgb(r: Int, g: Int, b: Int) =
    new Color(r / 255f, g / 255f, b / 255f, 1f)

  implicit class StringOps(val s: String) extends AnyVal {
    def toBlock: Array[String] = s.stripMargin.trim.split('\n')
  }
}
