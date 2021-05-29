package org.scalanative.bindgen.docs

import org.scalatest.funspec.AnyFunSpec

class GeometrySpec extends AnyFunSpec {
  describe("geometry") {
    it("using generated bindings") {
      //#example
      import com.example.custom.binding.Vector.Point
      import org.example.geometry.Geometry._
      import scala.scalanative.unsafe.Zone

      Zone { implicit zone =>
        val center = Point(1, 1)
        val circle = struct_circle(center, 2.0)

        val area = circle_area(circle)
        //#example
        assert(area - (4 * math.Pi) < 1e-3f)
      //#example
      }
      //#example
    }
  }
}
