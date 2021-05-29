package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object OpaqueTypes {
  type struct_undefinedIncludedStruct = CStruct0 // incomplete type
  type undefinedIncludedStruct = struct_undefinedIncludedStruct
  type struct_points = CStruct2[Ptr[struct_point], Ptr[struct_point]]
  type points = struct_points
  type struct_point = CStruct2[CInt, CInt]
  type union_u = CArray[Byte, Nat._4]
  type u = union_u
  type struct_undefinedStruct = CStruct0 // incomplete type
  type struct_structWithPointerToUndefinedStruct = CStruct1[Ptr[struct_undefinedStruct]]
  type union_unionWithPointerToUndefinedStruct = CArray[Byte, Nat._8]
  type union_undefinedUnion = CStruct0 // incomplete type
  type undefinedUnion = union_undefinedUnion
  type aliasToPointerOfUndefinedUnion = Ptr[undefinedUnion]
  type aliasForUndefinedStruct = struct_undefinedStruct
  type functionPointerWithPointerToOpaqueType = CFuncPtr1[Ptr[Ptr[struct_undefinedStruct]], Ptr[undefinedUnion]]
  def move(point: Ptr[struct_point], x: CInt, y: CInt): Ptr[struct_point] = extern
  def processPoints(p: Ptr[points]): Ptr[union_u] = extern
  def usePointerToUndefinedStruct(p0: Ptr[struct_undefinedStruct]): Unit = extern
  def fun(): Ptr[Ptr[undefinedUnion]] = extern
  def returnPointerToAliasOfUndefinedStruct(): Ptr[aliasForUndefinedStruct] = extern
  def usePointerToUndefinedIncludedStruct(p0: Ptr[undefinedIncludedStruct]): Unit = extern

  object implicits {
    implicit class struct_points_ops(val p: Ptr[struct_points]) extends AnyVal {
      def point1: Ptr[struct_point] = p._1
      def point1_=(value: Ptr[struct_point]): Unit = p._1 = value
      def point2: Ptr[struct_point] = p._2
      def point2_=(value: Ptr[struct_point]): Unit = p._2 = value
    }

    implicit class struct_point_ops(val p: Ptr[struct_point]) extends AnyVal {
      def x: CInt = p._1
      def x_=(value: CInt): Unit = p._1 = value
      def y: CInt = p._2
      def y_=(value: CInt): Unit = p._2 = value
    }

    implicit class struct_structWithPointerToUndefinedStruct_ops(val p: Ptr[struct_structWithPointerToUndefinedStruct]) extends AnyVal {
      def field: Ptr[struct_undefinedStruct] = p._1
      def field_=(value: Ptr[struct_undefinedStruct]): Unit = p._1 = value
    }

    implicit class union_u_pos(val p: Ptr[union_u]) extends AnyVal {
      def i: Ptr[CInt] = p.asInstanceOf[Ptr[CInt]]
      def i_=(value: CInt): Unit = !p.asInstanceOf[Ptr[CInt]] = value
      def f: Ptr[CFloat] = p.asInstanceOf[Ptr[CFloat]]
      def f_=(value: CFloat): Unit = !p.asInstanceOf[Ptr[CFloat]] = value
    }

    implicit class union_unionWithPointerToUndefinedStruct_pos(val p: Ptr[union_unionWithPointerToUndefinedStruct]) extends AnyVal {
      def field: Ptr[Ptr[struct_undefinedStruct]] = p.asInstanceOf[Ptr[Ptr[struct_undefinedStruct]]]
      def field_=(value: Ptr[struct_undefinedStruct]): Unit = !p.asInstanceOf[Ptr[Ptr[struct_undefinedStruct]]] = value
    }
  }

  object struct_points {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_points] = alloc[struct_points]
    def apply(point1: Ptr[struct_point], point2: Ptr[struct_point])(implicit z: Zone): Ptr[struct_points] = {
      val ptr = alloc[struct_points]
      ptr.point1 = point1
      ptr.point2 = point2
      ptr
    }
  }

  object struct_point {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_point] = alloc[struct_point]
    def apply(x: CInt, y: CInt)(implicit z: Zone): Ptr[struct_point] = {
      val ptr = alloc[struct_point]
      ptr.x = x
      ptr.y = y
      ptr
    }
  }

  object struct_structWithPointerToUndefinedStruct {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_structWithPointerToUndefinedStruct] = alloc[struct_structWithPointerToUndefinedStruct]
    def apply(field: Ptr[struct_undefinedStruct])(implicit z: Zone): Ptr[struct_structWithPointerToUndefinedStruct] = {
      val ptr = alloc[struct_structWithPointerToUndefinedStruct]
      ptr.field = field
      ptr
    }
  }
}
