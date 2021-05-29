package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object CustomNames {
  type enum_enumWithTypedef = CUnsignedInt
  object enum_enumWithTypedef {
    final val CONST: enum_enumWithTypedef = 0.toUInt
  }

  type EnumWithTypedef = enum_enumWithTypedef
  type page = CStruct2[CString, Ptr[Byte]]
  type book = CStruct1[Ptr[page]]
  type weight = CArray[Byte, Nat._4]
  type MY_INT = CInt
}
