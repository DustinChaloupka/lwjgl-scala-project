package org.chaloupka.lwjgl
import org.chaloupka.lwjgl.GameAttributes.targetUPS

case class FixedTimeStep(accumulator: Float = 0f, alpha: Float = 0f) {
  lazy val interval = 1f / targetUPS
  def addDeltaToAccumulator(delta: Float): FixedTimeStep = this.copy(accumulator + delta, alpha)
  def subtractIntervalFromAccumulator(): FixedTimeStep = this.copy(accumulator - interval, alpha)
  def updateAlpha(): FixedTimeStep = this.copy(accumulator, accumulator / interval)
  def canUpdate: Boolean = accumulator >= interval
}
