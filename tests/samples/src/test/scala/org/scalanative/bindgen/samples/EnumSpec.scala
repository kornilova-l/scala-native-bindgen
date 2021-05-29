package org.scalanative.bindgen.samples

import org.scalatest.funspec.AnyFunSpec
import scala.scalanative.unsafe._

class EnumSpec extends AnyFunSpec {
  describe("enum bindings") {
    it("should bind to C enum values") {
      assert(Enum.get_WEDNESDAY() == Enum.enum_days.WEDNESDAY)
    }

    it("should handle large negative values") {
      assert(
        Enum.check_BIG_NEG_A(Enum.enum_bigNegativeValues.BIG_NEG_A) == c"OK")
    }
  }
}
