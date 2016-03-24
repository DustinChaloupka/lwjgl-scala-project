package org.chaloupka.lwjgl

object Run {
  def main(args: Array[String]): Unit = {
    val (window, timer, renderer) = Game.init()
    Game.play(window, timer, renderer)
    Game.dispose(window, renderer)
  }
}
