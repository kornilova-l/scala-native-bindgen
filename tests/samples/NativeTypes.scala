package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

object NativeTypes {
  type size_t = CUnsignedInt
  type ptrdiff_t = CUnsignedInt
  type char16_t = CUnsignedShort
  type char32_t = CUnsignedInt
  type void_type = Unit
  type char_type = CChar
  type signed_char_type = CSignedChar
  type unsigned_char_type = CUnsignedChar
  type short_type = CShort
  type unsigned_short_type = CUnsignedShort
  type int_type = CInt
  type unsigned_int_type = CUnsignedInt
  type long_type = CLong
  type long_int_type = CLong
  type unsigned_long_type = CUnsignedLong
  type unsigned_long_int_type = CUnsignedLong
  type long_long_type = CLongLong
  type unsigned_long_long_type = CUnsignedLongLong
  type float_type = CFloat
  type double_type = CDouble
  type ptr_byte_type = Ptr[Byte]
  type ptr_int_type = Ptr[CInt]
  type cstring_type = CString
  type size_t_type = CSize
  type ptrdiff_t_type = CPtrDiff
  type char16_t_type = CChar16
  type char32_t_type = CChar32
}
