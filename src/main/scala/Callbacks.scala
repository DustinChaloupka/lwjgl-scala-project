package org.chaloupka.lwjgl

import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.{GLFWKeyCallback, GLFWErrorCallback}

object Callbacks {
  lazy val errorCallback = GLFWErrorCallback.createPrint(System.err)
  lazy val keyCallback = new GLFWKeyCallback() {
    override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
        glfwSetWindowShouldClose(window, GLFW_TRUE)
      }
    }
  }
}
