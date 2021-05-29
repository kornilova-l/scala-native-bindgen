package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object PrivateMembers {
  type enum___privateEnum = CUnsignedInt
  object enum___privateEnum {
    final val A: enum___privateEnum = 0.toUInt
    final val B: enum___privateEnum = 1.toUInt
  }

  type enum_enumWithPrivateMembers = CUnsignedInt
  object enum_enumWithPrivateMembers {
    final val __C: enum_enumWithPrivateMembers = 0.toUInt
    final val D: enum_enumWithPrivateMembers = 1.toUInt
  }

  type enum_anonymous_0 = CUnsignedInt
  object enum_anonymous_0 {
    final val __E: enum_anonymous_0 = 0.toUInt
    final val F: enum_anonymous_0 = 1.toUInt
  }

  type pid_t = CInt
  type __private_type = CInt
  type struct_structWithPrivateType = CStruct2[CInt, __private_type]
  type union___unionWithPrivateName = CArray[Byte, Nat._4]
  type struct_structWithPrivateStruct = CStruct1[Ptr[struct_structWithPrivateType]]
  type struct_normalStruct = CStruct1[CInt]
  type struct_privateStructWithTypedef = CStruct1[Ptr[__private_type]]
  type privateStructWithTypedef = struct_privateStructWithTypedef
  type privateStructWithTypedefPtr = Ptr[struct_privateStructWithTypedef]
  def getTypeThatUsesPrivateTypes(): pid_t = extern
  def getPrivateType(): Ptr[__private_type] = extern
  def usesPrivateUnion(p0: Ptr[union___unionWithPrivateName]): Unit = extern
  def usesPrivateStruct(p0: Ptr[struct_structWithPrivateType], p1: Ptr[struct_normalStruct]): Unit = extern
  def usesPrivateEnum(p0: Ptr[enum___privateEnum]): Unit = extern

  object implicits {
    implicit class struct_structWithPrivateType_ops(val p: Ptr[struct_structWithPrivateType]) extends AnyVal {
      def field1: CInt = p._1
      def field1_=(value: CInt): Unit = p._1 = value
      def field2: __private_type = p._2
      def field2_=(value: __private_type): Unit = p._2 = value
    }

    implicit class struct_structWithPrivateStruct_ops(val p: Ptr[struct_structWithPrivateStruct]) extends AnyVal {
      def s: Ptr[struct_structWithPrivateType] = p._1
      def s_=(value: Ptr[struct_structWithPrivateType]): Unit = p._1 = value
    }

    implicit class struct_normalStruct_ops(val p: Ptr[struct_normalStruct]) extends AnyVal {
      def a: CInt = p._1
      def a_=(value: CInt): Unit = p._1 = value
    }

    implicit class struct_privateStructWithTypedef_ops(val p: Ptr[struct_privateStructWithTypedef]) extends AnyVal {
      def a: Ptr[__private_type] = p._1
      def a_=(value: Ptr[__private_type]): Unit = p._1 = value
    }

    implicit class union___unionWithPrivateName_pos(val p: Ptr[union___unionWithPrivateName]) extends AnyVal {
      def a: Ptr[CInt] = p.asInstanceOf[Ptr[CInt]]
      def a_=(value: CInt): Unit = !p.asInstanceOf[Ptr[CInt]] = value
    }
  }

  object struct_structWithPrivateType {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_structWithPrivateType] = alloc[struct_structWithPrivateType]
    def apply(field1: CInt, field2: __private_type)(implicit z: Zone): Ptr[struct_structWithPrivateType] = {
      val ptr = alloc[struct_structWithPrivateType]
      ptr.field1 = field1
      ptr.field2 = field2
      ptr
    }
  }

  object struct_structWithPrivateStruct {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_structWithPrivateStruct] = alloc[struct_structWithPrivateStruct]
    def apply(s: Ptr[struct_structWithPrivateType])(implicit z: Zone): Ptr[struct_structWithPrivateStruct] = {
      val ptr = alloc[struct_structWithPrivateStruct]
      ptr.s = s
      ptr
    }
  }

  object struct_normalStruct {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_normalStruct] = alloc[struct_normalStruct]
    def apply(a: CInt)(implicit z: Zone): Ptr[struct_normalStruct] = {
      val ptr = alloc[struct_normalStruct]
      ptr.a = a
      ptr
    }
  }

  object struct_privateStructWithTypedef {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_privateStructWithTypedef] = alloc[struct_privateStructWithTypedef]
    def apply(a: Ptr[__private_type])(implicit z: Zone): Ptr[struct_privateStructWithTypedef] = {
      val ptr = alloc[struct_privateStructWithTypedef]
      ptr.a = a
      ptr
    }
  }
}
