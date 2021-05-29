package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object Union {
  type enum_union_op = CUnsignedInt
  object enum_union_op {
    final val UNION_SET: enum_union_op = 0.toUInt
    final val UNION_TEST: enum_union_op = 1.toUInt
  }

  type struct_s = CStruct1[CInt]
  type union_values = CArray[Byte, Nat._8]
  def union_get_sizeof(): CInt = extern
  def union_test_int(v: Ptr[union_values], op: enum_union_op, value: CInt): CInt = extern
  def union_test_long(v: Ptr[union_values], op: enum_union_op, value: CLong): CInt = extern
  def union_test_long_long(v: Ptr[union_values], op: enum_union_op, value: CLongLong): CInt = extern
  def union_test_double(v: Ptr[union_values], op: enum_union_op, value: CDouble): CInt = extern
  def union_test_string(v: Ptr[union_values], op: enum_union_op, value: CString): CInt = extern
  def union_test_struct(v: Ptr[union_values], op: enum_union_op, value: Ptr[struct_s]): CInt = extern

  object implicits {
    implicit class struct_s_ops(val p: Ptr[struct_s]) extends AnyVal {
      def a: CInt = p._1
      def a_=(value: CInt): Unit = p._1 = value
    }

    implicit class union_values_pos(val p: Ptr[union_values]) extends AnyVal {
      def l: Ptr[CLong] = p.asInstanceOf[Ptr[CLong]]
      def l_=(value: CLong): Unit = !p.asInstanceOf[Ptr[CLong]] = value
      def i: Ptr[CInt] = p.asInstanceOf[Ptr[CInt]]
      def i_=(value: CInt): Unit = !p.asInstanceOf[Ptr[CInt]] = value
      def ll: Ptr[CLongLong] = p.asInstanceOf[Ptr[CLongLong]]
      def ll_=(value: CLongLong): Unit = !p.asInstanceOf[Ptr[CLongLong]] = value
      def d: Ptr[CDouble] = p.asInstanceOf[Ptr[CDouble]]
      def d_=(value: CDouble): Unit = !p.asInstanceOf[Ptr[CDouble]] = value
      def s: Ptr[CString] = p.asInstanceOf[Ptr[CString]]
      def s_=(value: CString): Unit = !p.asInstanceOf[Ptr[CString]] = value
      def structInUnion: Ptr[struct_s] = p.asInstanceOf[Ptr[struct_s]]
      def structInUnion_=(value: Ptr[struct_s]): Unit = !p.asInstanceOf[Ptr[struct_s]] = value
    }
  }

  object struct_s {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_s] = alloc[struct_s]
    def apply(a: CInt)(implicit z: Zone): Ptr[struct_s] = {
      val ptr = alloc[struct_s]
      ptr.a = a
      ptr
    }
  }
}
