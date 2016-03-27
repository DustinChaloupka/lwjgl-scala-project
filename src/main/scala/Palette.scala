package org.chaloupka.lwjgl

object Palette {
  sealed trait Color {
    def red: Int
    def blue: Int
    def green: Int
    def alpha: Float

    val minColorValue = 0
    val maxColorValue = 255
    def inRangeInt(value: Int): Int = {
      Math.max(Math.min(maxColorValue, value), minColorValue)
    }

    def inRangeFloat(value: Int): Float = {
      inRangeInt(value).toFloat / maxColorValue.toFloat
    }

    def floatValues: (Float, Float, Float, Float) = {
      (inRangeFloat(red), inRangeFloat(blue), inRangeFloat(green), alpha)
    }
  }

  case class White() extends Color {
    lazy val red = maxColorValue
    lazy val blue = maxColorValue
    lazy val green = maxColorValue
    lazy val alpha = 1f
  }

  case class Black() extends Color {
    lazy val red = minColorValue
    lazy val blue = minColorValue
    lazy val green = minColorValue
    lazy val alpha = 1f
  }

  case class GrayA(alpha: Float) extends Color {
    lazy val red = minColorValue
    lazy val blue = minColorValue
    lazy val green = minColorValue
  }

  case class RedBlueGreenA(red: Int, blue: Int, green: Int, alpha: Float) extends Color
  case class RedBlueA(red: Int, blue: Int, alpha: Float) extends Color {
    lazy val green = minColorValue
  }

  case class RedGreenA(red: Int, green: Int, alpha: Float) extends Color {
    lazy val blue = minColorValue
  }

  case class RedA(red: Int, alpha: Float) extends Color {
    lazy val blue = minColorValue
    lazy val green = minColorValue
  }

  case class BlueGreenA(blue: Int, green: Int, alpha: Float) extends Color {
    lazy val red = minColorValue
  }

  case class BlueA(blue: Int, alpha: Float) extends Color {
    lazy val red = minColorValue
    lazy val green = minColorValue
  }

  case class GreenA(green: Int, alpha: Float) extends Color {
    lazy val red = minColorValue
    lazy val blue = minColorValue
  }

  case class RedBlueGreen(red: Int, blue: Int, green: Int) extends Color {
    lazy val alpha = 1f
  }

  case class RedBlue(red: Int, blue: Int) extends Color {
    lazy val green = minColorValue
    lazy val alpha = 1f
  }

  case class RedGreen(red: Int, green: Int) extends Color {
    lazy val blue = minColorValue
    lazy val alpha = 1f
  }

  case class Red(red: Int) extends Color {
    lazy val blue = minColorValue
    lazy val green = minColorValue
    lazy val alpha = 1f
  }

  case class BlueGreen(blue: Int, green: Int) extends Color {
    lazy val red = minColorValue
    lazy val alpha = 1f
  }

  case class Blue(blue: Int) extends Color {
    lazy val red = minColorValue
    lazy val green = minColorValue
    lazy val alpha = 1f
  }

  case class Green(green: Int) extends Color {
    lazy val red = minColorValue
    lazy val blue = minColorValue
    lazy val alpha = 1f
  }
}
