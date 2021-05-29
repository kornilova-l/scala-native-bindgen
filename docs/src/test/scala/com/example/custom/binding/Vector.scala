//#example
package com.example.custom.binding

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("vector")
@extern
object Vector {
  type Point       = CStruct2[CFloat, CFloat]
  type LineSegment = CStruct2[Point, Point]
  // ...
  //#example
  def cosine(v1: Ptr[LineSegment],
             v2: Ptr[LineSegment]): CFloat = extern

  object implicits {
    implicit class PointOps(val p: Ptr[Point]) extends AnyVal {
      def x: CFloat                = p._1
      def x_=(value: CFloat): Unit = p._1 = value
      def y: CFloat                = p._2
      def y_=(value: CFloat): Unit = p._2 = value
    }

    implicit class LineSegmentOps(val p: Ptr[LineSegment])
        extends AnyVal {
      def a: Ptr[Point]                = p._1.asInstanceOf[Ptr[Point]]
      def a_=(value: Ptr[Point]): Unit = p._1 = value
      def b: Ptr[Point]                = p._2.asInstanceOf[Ptr[Point]]
      def b_=(value: Ptr[Point]): Unit = p._2 = value
    }
  }

  object Point {
    import implicits._
    def apply()(implicit z: Zone): Ptr[Point] =
      alloc[Point]
    def apply(x: CFloat, y: CFloat)(
        implicit z: Zone): Ptr[Point] = {
      val ptr = alloc[Point]
      ptr.x = x
      ptr.y = y
      ptr
    }
  }

  object LineSegment {
    import implicits._
    def apply()(implicit z: Zone): Ptr[LineSegment] =
      alloc[LineSegment]
    def apply(a: Ptr[Point], b: Ptr[Point])(
        implicit z: Zone): Ptr[LineSegment] = {
      val ptr = alloc[LineSegment]
      ptr.a = a
      ptr.b = b
      ptr
    }
  }
  //#example
}
//#example
