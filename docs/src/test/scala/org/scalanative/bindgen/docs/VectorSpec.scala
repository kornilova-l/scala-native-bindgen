package org.scalanative.bindgen.docs

import org.scalatest.funspec.AnyFunSpec

class VectorSpec extends AnyFunSpec {
  describe("vector") {
    it("using generated bindings") {
      //#step-1
      import org.example.vector._
      import scala.scalanative.unsafe.Zone

      Zone { implicit zone =>
        val p1 = struct_point(1, 1)
        val p2 = struct_point(7, 4)

        val lineSegment1 = struct_lineSegment(p1, p2)
        //#step-1
        //#step-2
        import org.example.vector.implicits._

        val lineSegment2 = struct_lineSegment()

        lineSegment2.a.x = 3
        lineSegment2.a.y = 4
        lineSegment2.b = struct_point(5, 0)
        //#step-2
        //#step-3
        val angle = cosine(lineSegment1, lineSegment2)
        //#step-3
        import org.scalactic.TolerantNumerics
        val epsilon = 1e-3f
        assert(
          (0.8.toFloat === angle)(
            TolerantNumerics.tolerantFloatEquality(epsilon)))
      //#step-1
      }
      //#step-1
    }
  }
}
