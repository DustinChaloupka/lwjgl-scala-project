package org.chaloupka.lwjgl
import java.nio.FloatBuffer
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.BufferUtils

case class Renderer(shaderProgram: ShaderProgram,
                    vertexShader: Shader,
                    fragmentShader: Shader,
                    vertexArrayObject: VertexArrayObject,
                    vertexArrayBuffer: VertexArrayBuffer,
                    vertices: FloatBuffer,
                    uniformModel: UniformModel) {
  def dispose(): Unit = {
    shaderProgram.delete()
    vertexShader.delete()
    fragmentShader.delete()
    vertexArrayObject.delete()
    vertexArrayBuffer.delete()
  }

  def updateUniformModel(uniformModel: UniformModel): Renderer = {
    this.copy(uniformModel = uniformModel)
  }
}

object Renderer {
  def initializeRenderer(): Renderer = {
    val vertexArrayObject = new VertexArrayObject()
    vertexArrayObject.bind()

    val vertexArrayBuffer = new VertexArrayBuffer()
    vertexArrayBuffer.bind(GL_ARRAY_BUFFER)

    val vertices = BufferUtils.createFloatBuffer(3 * 6)
    vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f)
    vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f)
    vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f)
    vertices.flip()

    vertexArrayBuffer.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

    val vertexShader = Shader.loadShaderFromFile(GL_VERTEX_SHADER, "src/main/resources/default.vert")
    vertexShader.init()
    val fragmentShader = Shader.loadShaderFromFile(GL_FRAGMENT_SHADER, "src/main/resources/default.frag")
    fragmentShader.init()

    val shaderProgram = ShaderProgram()
    shaderProgram.attachShader(vertexShader)
    shaderProgram.attachShader(fragmentShader)
    shaderProgram.bindFragDataLocation(0, "fragColor")
    shaderProgram.link()
    shaderProgram.use()

    specifyVertexAttributes(shaderProgram)

    val uniformModelLocation = shaderProgram.getUniformLocation("model")
    val triangleUniformModel = TriangleUniformModel(uniformModelLocation)
    val model = Matrix4.identity()
    shaderProgram.setUniformMatrix4(uniformModelLocation, model)

    val uniformViewLocation = shaderProgram.getUniformLocation("view")
    val view = Matrix4.identity()
    shaderProgram.setUniformMatrix4(uniformViewLocation, view)

    val uniformProjectionLocation = shaderProgram.getUniformLocation("projection")
    val ratio = 640f / 480f
    val projection = Matrix4.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f)
    shaderProgram.setUniformMatrix4(uniformProjectionLocation, projection)

    Renderer(shaderProgram, vertexShader, fragmentShader, vertexArrayObject, vertexArrayBuffer, vertices, triangleUniformModel)
  }

  def render(frameRates: FrameRates, fixedTimeStep: FixedTimeStep, renderer: Renderer): FrameRates = {
    glClear(GL_COLOR_BUFFER_BIT)

    renderer.uniformModel match {
      case TriangleUniformModel(id, previousAngle, angle) =>
        val triangleLerpAngle = (1f - fixedTimeStep.alpha) * previousAngle + fixedTimeStep.alpha * angle
        val rotateModel = Matrix4.rotate(triangleLerpAngle, 0f, 0f, 1f)
        renderer.shaderProgram.setUniformMatrix4(id, rotateModel)
    }

    glDrawArrays(GL_TRIANGLES, 0, 3)

    // Last thing probably
    val updatedFrameRates = frameRates.incrementFPSCount()
    updatedFrameRates
  }

  private[this] def specifyVertexAttributes(shaderProgram: ShaderProgram): Unit = {
    val floatSize = 4

    val positionAttribute = shaderProgram.getAttributeLocation("position")
    glEnableVertexAttribArray(positionAttribute)
    glVertexAttribPointer(positionAttribute, 3, GL_FLOAT, false, 6 * floatSize, 0)

    val columnAttribute = shaderProgram.getAttributeLocation("color")
    glEnableVertexAttribArray(columnAttribute)
    glVertexAttribPointer(columnAttribute, 3, GL_FLOAT, false, 6 * floatSize, 3 * floatSize)
  }
}
