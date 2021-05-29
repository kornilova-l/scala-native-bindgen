package org.example

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("vector")
@extern
object vector {
  type struct_point = CStruct2[CFloat, CFloat]
  type struct_lineSegment = CStruct2[struct_point, struct_point]
  def cosine(v1: Ptr[struct_lineSegment], v2: Ptr[struct_lineSegment]): CFloat = extern

  object implicits {
    implicit class struct_point_ops(val p: Ptr[struct_point]) extends AnyVal {
      def x: CFloat = p._1
      def x_=(value: CFloat): Unit = p._1 = value
      def y: CFloat = p._2
      def y_=(value: CFloat): Unit = p._2 = value
    }

    implicit class struct_lineSegment_ops(val p: Ptr[struct_lineSegment]) extends AnyVal {
      def a: Ptr[struct_point] = p.at1
      def a_=(value: Ptr[struct_point]): Unit = p._1 = value
      def b: Ptr[struct_point] = p.at2
      def b_=(value: Ptr[struct_point]): Unit = p._2 = value
    }
  }

  object struct_point {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_point] = alloc[struct_point]
    def apply(x: CFloat, y: CFloat)(implicit z: Zone): Ptr[struct_point] = {
      val ptr = alloc[struct_point]
      ptr.x = x
      ptr.y = y
      ptr
    }
  }

  object struct_lineSegment {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_lineSegment] = alloc[struct_lineSegment]
    def apply(a: Ptr[struct_point], b: Ptr[struct_point])(implicit z: Zone): Ptr[struct_lineSegment] = {
      val ptr = alloc[struct_lineSegment]
      ptr.a = a
      ptr.b = b
      ptr
    }
  }
}
