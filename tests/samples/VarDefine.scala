package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object VarDefine {
  val a: CInt = extern
  val constInt: CInt = extern
  val constIntPointer: Ptr[CInt] = extern
  val c: CInt = extern
  val f: CFloat = extern
  @name("a")
  val A: CInt = extern
  @name("constInt")
  val CONST_INT: CInt = extern
  @name("constIntPointer")
  val CONST_INT_POINTER: Ptr[CInt] = extern
}
