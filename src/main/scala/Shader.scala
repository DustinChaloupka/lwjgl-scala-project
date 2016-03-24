package org.chaloupka.lwjgl
import java.io.{FileInputStream, BufferedReader, InputStreamReader, IOException}
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11.GL_TRUE

case class Shader(shaderType: Int, source: CharSequence) {
  val id = glCreateShader(shaderType)

  def init(): Boolean = {
    glShaderSource(id, source)
    glCompileShader(id)

    compiledSuccessfully
  }

  lazy val compiledSuccessfully = {
    val status = glGetShaderi(id, GL_COMPILE_STATUS)
    status == GL_TRUE
  }

  def getShaderLog(): String = {
    glGetShaderInfoLog(id)
  }

  def delete(): Unit = {
    glDeleteShader(id)
  }
}

object Shader {
  def loadShaderFromFile(shaderType: Int, filePath: String): Shader = {
    val builder = new StringBuilder()
    val inputStream = new FileInputStream(filePath)
    val reader = new BufferedReader(new InputStreamReader(inputStream))
    try {
      var line = reader.readLine()
      while (line != null) {
        builder.append(line).append("\n")
        line = reader.readLine()
      }
    } catch {
      case e: IOException =>
        throw new RuntimeException(s"Failed to load a shader file: ${e.getMessage}")
    }

    val source = builder.toString()
    Shader(shaderType, source)
  }
}
