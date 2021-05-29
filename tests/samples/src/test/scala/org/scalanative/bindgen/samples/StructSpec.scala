package org.scalanative.bindgen.samples

import org.scalatest.funspec.AnyFunSpec
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

import org.scalanative.bindgen.samples.Struct.implicits._

class StructSpec extends AnyFunSpec {
  describe("struct bindings") {
    it("should provide field getters and setters") {
      val point = Struct.createPoint()
      assert(point.x == 10)
      assert(point.y == 20)

      point.x = 11
      assert(point.x == 11)
    }

    it("should provide constructors") {
      Zone { implicit Zone =>
        val pointNoValue = Struct.struct_point()
        assert(pointNoValue.x == 0)
        assert(pointNoValue.y == 0)

        val point = Struct.struct_point(1, 2)
        assert(point.x == 1)
        assert(point.y == 2)
      }
    }

    it("should provided field getters for inner structs") {
      Zone { implicit zone =>
        val points = Struct.struct_points()
        Struct.setPoints(points, 1, 2, 3, 4)
        assert(1 == points.p1.x)
        assert(2 == points.p1.y)
        assert(3 == points.p2.x)
        assert(4 == points.p2.y)
      }
    }

    it("should provided field setters for inner structs") {
      Zone { implicit zone =>
        val points = Struct.struct_points()
        points.p1.x = 1
        points.p1.y = 2
        points.p2.x = 3
        points.p2.y = 4
        assert(1 == Struct.getPoint(points, Struct.enum_pointIndex.X1))
        assert(2 == Struct.getPoint(points, Struct.enum_pointIndex.Y1))
        assert(3 == Struct.getPoint(points, Struct.enum_pointIndex.X2))
        assert(4 == Struct.getPoint(points, Struct.enum_pointIndex.Y2))
      }
    }

    it("should support anonymous structs") {
      type struct_anonymousStruct = CStruct2[CChar, CInt]
      Zone { implicit zone =>
        val anonymousStruct: Ptr[struct_anonymousStruct] =
          alloc[struct_anonymousStruct]
        anonymousStruct._1 = 'a'
        anonymousStruct._2 = 42

        val structWithAnonymousStruct =
          Struct.struct_structWithAnonymousStruct()
        structWithAnonymousStruct.anonymousStruct = anonymousStruct

        assert(
          'a' == Struct.getCharFromAnonymousStruct(structWithAnonymousStruct))
        assert(
          42 == Struct.getIntFromAnonymousStruct(structWithAnonymousStruct))
      }
    }

    it("should match size of C memory layout for big structs") {
      assert(Struct.getBigStructSize().toULong == sizeof[Struct.struct_bigStruct])
    }

    it("should provide field getters for big structs") {
      Zone { implicit zone: Zone =>
        val structPtr = alloc[Struct.struct_bigStruct]
        for (value <- Seq(Long.MinValue, -1, 0, 1, Long.MaxValue)) {
          Struct.struct_test_long(structPtr,
                                  Struct.enum_struct_op.STRUCT_SET,
                                  value)
          assert(structPtr.one == value)
        }

        for (value <- Seq(Double.MinValue, -1, 0, 1, Double.MaxValue)) {
          Struct.struct_test_double(structPtr,
                                    Struct.enum_struct_op.STRUCT_SET,
                                    value)
          assert(structPtr.five == value)
        }

        val pointPtr = alloc[Struct.point]
        pointPtr.x = 5
        pointPtr.y = 10

        Struct.struct_test_point(structPtr,
                                 Struct.enum_struct_op.STRUCT_SET,
                                 pointPtr)
        assert(structPtr.six.x == pointPtr.x)
        assert(structPtr.six.y == pointPtr.y)
      }
    }

    it("should provide field setters for big structs") {
      Zone { implicit zone: Zone =>
        val structPtr = alloc[Struct.struct_bigStruct]
        for (value <- Seq(Long.MinValue, -1, 0, 1, Long.MaxValue)) {
          structPtr.one = value
          assert(
            Struct.struct_test_long(structPtr,
                                    Struct.enum_struct_op.STRUCT_TEST,
                                    value) == 1)
        }

        for (value <- Seq(Double.MinValue, -1, 0, 1, Double.MaxValue)) {
          structPtr.five = value
          assert(
            Struct.struct_test_double(structPtr,
                                      Struct.enum_struct_op.STRUCT_TEST,
                                      value) == 1)
        }

        val pointPtr = alloc[Struct.point]
        pointPtr.x = 5
        pointPtr.y = 10
        structPtr.six = pointPtr
        assert(
          Struct.struct_test_point(structPtr,
                                   Struct.enum_struct_op.STRUCT_TEST,
                                   pointPtr) == 1)
      }
    }
  }
}
