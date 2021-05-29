package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object AnonymousTypes {
  type struct_anonymous_0 = CStruct1[CChar]
  type union_anonymous_0 = CArray[Byte, Nat._8]
  type struct_anonymous_1 = CStruct1[Ptr[union_anonymous_0]]
  type struct_StructWithAnonymousStruct = CStruct2[Ptr[struct_anonymous_1], CUnsignedInt]
  type struct_anonymous_2 = CStruct1[CInt]
  def foo(s: Ptr[struct_anonymous_0]): Unit = extern
  def bar(): Ptr[struct_anonymous_2] = extern

  object implicits {
    implicit class struct_anonymous_0_ops(val p: Ptr[struct_anonymous_0]) extends AnyVal {
      def a: CChar = p._1
      def a_=(value: CChar): Unit = p._1 = value
    }

    implicit class struct_anonymous_1_ops(val p: Ptr[struct_anonymous_1]) extends AnyVal {
      def innerUnion: Ptr[union_anonymous_0] = p._1
      def innerUnion_=(value: Ptr[union_anonymous_0]): Unit = p._1 = value
    }

    implicit class struct_StructWithAnonymousStruct_ops(val p: Ptr[struct_StructWithAnonymousStruct]) extends AnyVal {
      def innerStruct: Ptr[struct_anonymous_1] = p._1
      def innerStruct_=(value: Ptr[struct_anonymous_1]): Unit = p._1 = value
      def innerEnum: CUnsignedInt = p._2
      def innerEnum_=(value: CUnsignedInt): Unit = p._2 = value
    }

    implicit class struct_anonymous_2_ops(val p: Ptr[struct_anonymous_2]) extends AnyVal {
      def result: CInt = p._1
      def result_=(value: CInt): Unit = p._1 = value
    }

    implicit class union_anonymous_0_pos(val p: Ptr[union_anonymous_0]) extends AnyVal {
      def a: Ptr[CLong] = p.asInstanceOf[Ptr[CLong]]
      def a_=(value: CLong): Unit = !p.asInstanceOf[Ptr[CLong]] = value
    }
  }

  object struct_anonymous_0 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_anonymous_0] = alloc[struct_anonymous_0]
    def apply(a: CChar)(implicit z: Zone): Ptr[struct_anonymous_0] = {
      val ptr = alloc[struct_anonymous_0]
      ptr.a = a
      ptr
    }
  }

  object struct_anonymous_1 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_anonymous_1] = alloc[struct_anonymous_1]
    def apply(innerUnion: Ptr[union_anonymous_0])(implicit z: Zone): Ptr[struct_anonymous_1] = {
      val ptr = alloc[struct_anonymous_1]
      ptr.innerUnion = innerUnion
      ptr
    }
  }

  object struct_StructWithAnonymousStruct {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_StructWithAnonymousStruct] = alloc[struct_StructWithAnonymousStruct]
    def apply(innerStruct: Ptr[struct_anonymous_1], innerEnum: CUnsignedInt)(implicit z: Zone): Ptr[struct_StructWithAnonymousStruct] = {
      val ptr = alloc[struct_StructWithAnonymousStruct]
      ptr.innerStruct = innerStruct
      ptr.innerEnum = innerEnum
      ptr
    }
  }

  object struct_anonymous_2 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_anonymous_2] = alloc[struct_anonymous_2]
    def apply(result: CInt)(implicit z: Zone): Ptr[struct_anonymous_2] = {
      val ptr = alloc[struct_anonymous_2]
      ptr.result = result
      ptr
    }
  }
}
