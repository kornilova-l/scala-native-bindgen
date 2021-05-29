package org.scalanative.bindgen.samples

import org.scalatest.funspec.AnyFunSpec
import scala.scalanative.unsafe._

class VarDefineSpec extends AnyFunSpec {
  describe("variable #define bindings") {
    it("should have the value of the referenced variable") {
      assert(VarDefine.A == 23)
      assert(VarDefine.CONST_INT == 10)
    }
  }
}
