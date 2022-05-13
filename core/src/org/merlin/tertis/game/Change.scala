package org.merlin.tertis.game

/** @param value -1 or -1
  * @param timestamp start time
  *
  * A keypress is still valid within a few milliseconds of creation, even if no longer
  * active.
  */
case class Change(
                   value: Int,
                   timestamp: Long,
                   auto: Boolean = false
)

case object Change {
  def down: Change = Change(-1, System.currentTimeMillis)
  def up: Change = Change(1, System.currentTimeMillis)
  def autoDown: Change = down.copy(auto = true)
  def autoUp: Change = up.copy(auto = true)
}
