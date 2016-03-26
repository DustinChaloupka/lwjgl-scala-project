package org.chaloupka.lwjgl
import org.chaloupka.lwjgl.PrimitiveUtils.FloatSize
import java.nio.Buffer
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.BufferUtils

case class Renderer(shaderProgram: ShaderProgram,
                    vertexArrayObject: VertexArrayObject,
                    bufferObjects: List[BufferObject],
                    vertices: List[Buffer],
                    uniformModel: UniformModel) {
  def dispose(): Unit = {
    shaderProgram.delete()
    vertexArrayObject.delete()
    bufferObjects.foreach(_.delete())
  }

  def updateUniformModel(uniformModel: UniformModel): Renderer = {
    this.copy(uniformModel = uniformModel)
  }
}

object Renderer {
  def initializeRenderer(): Renderer = {
    val vertexArrayObject = new VertexArrayObject()
    vertexArrayObject.bind()

    val vertexBufferObject = new VertexBufferObject()
    val modelVertices = BufferUtils.createFloatBuffer(8 * 7)
    modelVertices.put(-0.5f).put(0.5f).put(0f).put(1f).put(0f).put(0f).put(1f)
    modelVertices.put(0.5f).put(0.5f).put(0f).put(0f).put(1f).put(0f).put(1f)
    modelVertices.put(0.5f).put(-0.5f).put(0f).put(0f).put(0f).put(1f).put(1f)
    modelVertices.put(-0.5f).put(-0.5f).put(0f).put(0.5f).put(0.5f).put(0f).put(1f)
    modelVertices.put(-0.5f).put(0.5f).put(-0.5f).put(0f).put(0.5f).put(0.5f).put(1f)
    modelVertices.put(0.5f).put(0.5f).put(-0.5f).put(0.25f).put(0.25f).put(0.25f).put(1f)
    modelVertices.put(0.5f).put(-0.5f).put(-0.5f).put(0.5f).put(0f).put(0.5f).put(1f)
    modelVertices.put(-0.5f).put(-0.5f).put(-0.5f).put(0.75f).put(0.75f).put(0.75f).put(1f)
    modelVertices.flip()

    vertexBufferObject.uploadData(modelVertices, GL_STATIC_DRAW)

    val elementBufferObject = new ElementBufferObject()
    val elementVertices = BufferUtils.createIntBuffer(12 * 3)
    elementVertices.put(0).put(1).put(2)
    elementVertices.put(2).put(3).put(0)
    elementVertices.put(0).put(4).put(1)
    elementVertices.put(1).put(4).put(5)
    elementVertices.put(5).put(1).put(2)
    elementVertices.put(2).put(6).put(5)
    elementVertices.put(5).put(6).put(4)
    elementVertices.put(4).put(6).put(7)
    elementVertices.put(7).put(6).put(2)
    elementVertices.put(2).put(3).put(7)
    elementVertices.put(7).put(4).put(0)
    elementVertices.put(0).put(3).put(7)
    elementVertices.flip()

    elementBufferObject.uploadData(elementVertices, GL_STATIC_DRAW)

    val vertexShader = Shader.loadShaderFromFile(GL_VERTEX_SHADER, "src/main/resources/default.vert")
    val vertexShaderCompiledSuccessfully = vertexShader.init()
    if (!vertexShaderCompiledSuccessfully) {
      println(s"Vertex shader did not compile correctly!")
    }

    val fragmentShader = Shader.loadShaderFromFile(GL_FRAGMENT_SHADER, "src/main/resources/default.frag")
    val fragmentShaderCompiledSuccessfully = fragmentShader.init()
    if (!fragmentShaderCompiledSuccessfully) {
      println(s"Fragment shader did not compile correctly!")
    }

    val shaderProgram = ShaderProgram(List(vertexShader, fragmentShader))
    shaderProgram.attachShaders()
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

    Renderer(shaderProgram, vertexArrayObject, List(vertexBufferObject, elementBufferObject), List(modelVertices, elementVertices), triangleUniformModel)
  }

  def render(frameRates: FrameRates, fixedTimeStep: FixedTimeStep, renderer: Renderer): FrameRates = {
    glClear(GL_COLOR_BUFFER_BIT)

    renderer.uniformModel match {
      case TriangleUniformModel(id, previousAngle, angle) =>
        val triangleLerpAngle = (1f - fixedTimeStep.alpha) * previousAngle + fixedTimeStep.alpha * angle
        val rotateModel = Matrix4.rotate(triangleLerpAngle, 0f, 0.25f, 0.25f)
        renderer.shaderProgram.setUniformMatrix4(id, rotateModel)
    }

    glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0)

    // Last thing probably
    val updatedFrameRates = frameRates.incrementFPSCount()
    updatedFrameRates
  }

  private[this] def specifyVertexAttributes(shaderProgram: ShaderProgram): Unit = {
    val positionAttribute = shaderProgram.getAttributeLocation("position")
    glEnableVertexAttribArray(positionAttribute)
    glVertexAttribPointer(positionAttribute, 3, GL_FLOAT, false, 7 * FloatSize, 0)

    val colorAttribute = shaderProgram.getAttributeLocation("color")
    glEnableVertexAttribArray(colorAttribute)
    glVertexAttribPointer(colorAttribute, 4, GL_FLOAT, false, 7 * FloatSize, 3 * FloatSize)
  }
}
