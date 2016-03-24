package org.chaloupka.lwjgl
import org.lwjgl.glfw.GLFW.glfwGetTime

case class Timer(lastLoopTime: Float = glfwGetTime().toFloat, timeCount: Float = 0f, delta: Float = 0f) {
  def update(oneSecondHasPassed: Boolean): Timer = {
    val currentTime = glfwGetTime().toFloat
    val updatedDelta = currentTime - lastLoopTime

    val timeCountDecrementer = if (oneSecondHasPassed) 1 else 0
    val updatedTimeCount = timeCount + delta - timeCountDecrementer

    Timer(currentTime, updatedTimeCount, updatedDelta)
  }
}
