package org.chaloupka.lwjgl
import java.io.{FileInputStream, BufferedReader, InputStreamReader, IOException}
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11.GL_TRUE

trait Shader {
  def shaderType: Int
  def source: String
  lazy val id = glCreateShader(shaderType)

  lazy val compiledSuccessfully = {
    val status = glGetShaderi(id, GL_COMPILE_STATUS)
    status == GL_TRUE
  }

  def init(): Boolean = {
    glShaderSource(id, source)
    glCompileShader(id)

    if (!compiledSuccessfully) {
      println(s"${this.getClass.getSimpleName} did not compile correctly!")
    }

    compiledSuccessfully
  }

  def getShaderLog(): String = {
    glGetShaderInfoLog(id)
  }

  def delete(): Unit = {
    glDeleteShader(id)
  }
}
