package org.scalanative.bindgen.samples

import org.scalatest.funspec.AnyFunSpec
import scala.scalanative.unsafe._

class FunctionSpec extends AnyFunSpec {
  describe("function bindings") {
    it("should bind to function with no args") {
      assert(Function.no_args() == 42)
    }

    it("should bind to function with void args") {
      assert(Function.void_arg() == 1.5)
    }

    it("should bind to function with one arg") {
      assert(Function.one_arg(42) == '*')
    }

    it("should bind to function with two arg") {
      val result = Function.two_args(3.14.toFloat, 1024)
      val string = fromCString(result)
      assert(string == "3.14 1024")
    }

    it("should bind to function with anonymous args") {
      assert(Function.anonymous_args(1.5.toFloat, 42) == 43.5)
    }

    it("should bind to function with variadic args") {
      Zone { implicit z =>
        val result = Function.variadic_args(0.2, toCString("0123"), 1, 10, 100, 1000)
        assert(result == 3210.2)
      }
    }
  }
}
