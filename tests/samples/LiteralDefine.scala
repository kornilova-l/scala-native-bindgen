package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

object LiteralDefine {

  object defines {
    val STRING: CString = c"Hello, World!"
    val LONG: CLong = 1000000000000L
    val LONG_WITHOUT_ENDING: CLong = 1000000000000L
    val LONG_LONG: CLongLong = 1000000000000L
    val MAXIMUM_SIGNED_LONG: CLong = 9223372036854775807L
    val MINIMUM_SIGNED_LONG: CLong = -9223372036854775808L
    val FLOAT: CDouble = 5.6
    val INT: CInt = 42
    val MAXIMUM_INT: CInt = 2147483647
    val NEW_INT: CInt = 42
    val NEG_INT: CInt = -42
    val SHOULD_BE_DEFINED: CString = c"Because INT is not equal to 0"
    val OCTAL: CInt = 139
    val HEXADECIMAL: CInt = 75
    val EXPONENT: CDouble = 1e-10
    val DOT_EXPONENT: CDouble = 0.01
    val HEXADECIMAL_WITHOUT_RADIX: CDouble = 523264
    val HEXADECIMAL_WITH_RADIX: CDouble = 7.5
    val HEXADECIMAL_FRACTIONAL_WITH_RADIX: CDouble = 0.0355225
  }
}
