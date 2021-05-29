package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object Function {
  type struct_s = CStruct1[CInt]
  type s = struct_s
  type union_u = CArray[Byte, Nat._4]
  def no_args(): CInt = extern
  def void_arg(): CFloat = extern
  def one_arg(a: CInt): CChar = extern
  def two_args(a: CFloat, b: CInt): Ptr[Byte] = extern
  def anonymous_args(p0: CFloat, p1: CInt): CDouble = extern
  def variadic_args(a: CDouble, varArgs: CString, varArgs0: CVarArg*): CDouble = extern
  def acceptsArray(p0: Ptr[CInt]): Unit = extern

  object implicits {
    implicit class struct_s_ops(val p: Ptr[struct_s]) extends AnyVal {
      def `val`: CInt = p._1
      def `val_=`(value: CInt): Unit = p._1 = value
    }

    implicit class union_u_pos(val p: Ptr[union_u]) extends AnyVal {
      def a: Ptr[CInt] = p.asInstanceOf[Ptr[CInt]]
      def a_=(value: CInt): Unit = !p.asInstanceOf[Ptr[CInt]] = value
    }
  }

  object struct_s {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_s] = alloc[struct_s]
    def apply(`val`: CInt)(implicit z: Zone): Ptr[struct_s] = {
      val ptr = alloc[struct_s]
      ptr.`val` = `val`
      ptr
    }
  }
}
