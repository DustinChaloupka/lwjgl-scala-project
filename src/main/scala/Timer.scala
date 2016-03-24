package org.chaloupka.lwjgl
import org.lwjgl.glfw.GLFW.glfwGetTime

case class Timer(lastLoopTime: Float, timeCount: Float) {
  lazy val interval = 1f / GameAttributes.targetUPS
  def decrementTimeCount(): Timer = this.copy(lastLoopTime, timeCount - 1)
  def addDeltaToTimeCount(delta: Float): Timer = this.copy(lastLoopTime, timeCount + delta)
  def withUpdatedLastLoopTime(time: Float): Timer = this.copy(time, timeCount)
}

object Timer {
  def initializeTimer(): Timer = {
    Timer(getTime, 0)
  }

  def getTime: Float = {
    glfwGetTime().toFloat
  }

  def getDeltaAndUpdatedTimer(timer: Timer): (Float, Timer) = {
    val currentTime = getTime
    val delta = currentTime - timer.lastLoopTime

    val updatedTimeCount = timer.addDeltaToTimeCount(delta)

    (delta, updatedTimeCount.withUpdatedLastLoopTime(currentTime))
  }

  def updateFrameRatesAndTimer(frameRates: FrameRates, timer: Timer): (FrameRates, Timer) = {
    if (timer.timeCount > 1f) {
      val updatedFrameRates = frameRates.resetPerSecondCounts()
      val updatedTimer = timer.decrementTimeCount()
      (updatedFrameRates, updatedTimer)
    } else {
      (frameRates, timer)
    }
  }
}
