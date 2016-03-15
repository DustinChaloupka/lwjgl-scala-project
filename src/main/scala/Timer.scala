package games.fantasy.lwjgl
import org.lwjgl.glfw.GLFW.glfwGetTime

case class Times(lastLoopTime: Double, timeCount: Double) {
  lazy val interval = 1.0 / GameAttributes.targetUPS
  def decrementTimeCount(): Times = this.copy(lastLoopTime, timeCount - 1)
  def addDeltaToTimeCount(delta: Double): Times = this.copy(lastLoopTime, timeCount + delta)
  def withUpdatedLastLoopTime(time: Double): Times = this.copy(time, timeCount)
}

trait Timer {
  def getTime: Double = {
    glfwGetTime()
  }

  def getDeltaAndUpdatedTimes(times: Times): (Double, Times) = {
    val currentTime = getTime
    val delta = currentTime - times.lastLoopTime

    val updatedTimeCount = times.addDeltaToTimeCount(delta)

    (delta, updatedTimeCount.withUpdatedLastLoopTime(currentTime))
  }

  def updateFrameRatesAndTimes(frameRates: FrameRates, times: Times): (FrameRates, Times) = {
    if (times.timeCount > 1.0) {
      val updatedFrameRates = frameRates.resetPerSecondCounts()
      val updatedTimes = times.decrementTimeCount()
      (updatedFrameRates, updatedTimes)
    } else {
      (frameRates, times)
    }
  }
}