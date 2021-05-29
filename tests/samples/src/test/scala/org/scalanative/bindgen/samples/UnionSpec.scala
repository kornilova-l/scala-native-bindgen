package org.scalanative.bindgen.samples

import org.scalatest.funspec.AnyFunSpec
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

import org.scalanative.bindgen.samples.Union.implicits._

class UnionSpec extends AnyFunSpec {
  describe("union bindings") {
    it("should match size of C memory layout") {
      assert(Union.union_get_sizeof().toULong == sizeof[Union.union_values])
    }

    it("should provide field getters") {
      Zone { implicit zone =>
        val unionPtr = alloc[Union.union_values]

        for (value <- Seq(Int.MinValue, -1, 0, 1, Int.MaxValue)) {
          Union.union_test_int(unionPtr, Union.enum_union_op.UNION_SET, value)
          assert(!unionPtr.i == value)
        }

        for (value <- Seq(Long.MinValue, -1, 0, 1, Long.MaxValue)) {
          Union.union_test_long(unionPtr, Union.enum_union_op.UNION_SET, value)
          assert(!unionPtr.l == value)
        }

        val llValues =
          Seq[CLongLong](Long.MinValue - 100, -1, 0, 1, Long.MaxValue + 100)
        for (value <- llValues) {
          Union.union_test_long_long(unionPtr,
                                     Union.enum_union_op.UNION_SET,
                                     value)
          assert(!unionPtr.ll == value)
        }

        for (value <- Seq(Double.MinValue, -1, 0, 1, Double.MaxValue)) {
          Union.union_test_double(unionPtr,
                                  Union.enum_union_op.UNION_SET,
                                  value)
          assert(!unionPtr.d == value)
        }

        for (value <- Seq("", "bindgen")) {
          val s = toCString(value)
          Union.union_test_string(unionPtr, Union.enum_union_op.UNION_SET, s)
          assert(fromCString(!unionPtr.s) == value)
        }

        Union.union_test_string(unionPtr, Union.enum_union_op.UNION_SET, null)
        assert(!unionPtr.s == null)

        val struct = alloc[Union.struct_s]
        struct.a = 10
        Union.union_test_struct(unionPtr, Union.enum_union_op.UNION_SET, struct)
        assert(unionPtr.structInUnion.a == 10)
      }
    }

    it("should provide field setters") {
      Zone { implicit zone =>
        val unionPtr = alloc[Union.union_values]

        for (value <- Seq(Int.MinValue, -1, 0, 1, Int.MaxValue)) {
          !unionPtr.i = value
          assert(
            Union.union_test_int(unionPtr,
                                 Union.enum_union_op.UNION_TEST,
                                 value) == 1)
        }

        for (value <- Seq(Long.MinValue, -1, 0, 1, Long.MaxValue)) {
          !unionPtr.l = value
          assert(
            Union.union_test_long(unionPtr,
                                  Union.enum_union_op.UNION_TEST,
                                  value) == 1)
        }

        val llValues =
          Seq[CLongLong](Long.MinValue - 100, -1, 0, 1, Long.MaxValue + 100)
        for (value <- llValues) {
          !unionPtr.ll = value
          assert(
            Union.union_test_long_long(unionPtr,
                                       Union.enum_union_op.UNION_TEST,
                                       value) == 1)
        }

        for (value <- Seq(Double.MinValue, -1, 0, 1, Double.MaxValue)) {
          !unionPtr.d = value
          assert(
            Union.union_test_double(unionPtr,
                                    Union.enum_union_op.UNION_TEST,
                                    value) == 1)
        }

        for (value <- Seq("", "bindgen")) {
          val s = toCString(value)
          !unionPtr.s = s
          val expected = toCString(value)
          assert(
            Union.union_test_string(unionPtr,
                                    Union.enum_union_op.UNION_TEST,
                                    expected) == 1)
        }

        !unionPtr.s = null
        assert(
          Union.union_test_string(unionPtr,
                                  Union.enum_union_op.UNION_TEST,
                                  null) == 1)

        val struct = alloc[Union.struct_s]
        struct.a = 10
        unionPtr.structInUnion = struct
        assert(
          Union.union_test_struct(unionPtr,
                                  Union.enum_union_op.UNION_TEST,
                                  struct) == 1)
      }
    }
  }
}
