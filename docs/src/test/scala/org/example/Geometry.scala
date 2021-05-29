package org.example.geometry

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("geometry")
@extern
object Geometry {
  //#using-struct-point
  type struct_circle = CStruct2[com.example.custom.binding.Vector.Point, CDouble]
  //#using-struct-point
  def circle_area(circle: Ptr[struct_circle]): CFloat = extern

  object implicits {
    implicit class struct_circle_ops(val p: Ptr[struct_circle]) extends AnyVal {
      def point: Ptr[com.example.custom.binding.Vector.Point] = p.at1
      def point_=(value: Ptr[com.example.custom.binding.Vector.Point]): Unit = p._1 = value
      def radius: CDouble = p._2
      def radius_=(value: CDouble): Unit = p._2 = value
    }
  }

  object struct_circle {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_circle] = alloc[struct_circle]
    def apply(point: Ptr[com.example.custom.binding.Vector.Point], radius: CDouble)(implicit z: Zone): Ptr[struct_circle] = {
      val ptr = alloc[struct_circle]
      ptr.point = point
      ptr.radius = radius
      ptr
    }
  }
}
