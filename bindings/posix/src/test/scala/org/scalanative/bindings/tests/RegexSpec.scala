package org.scalanative.bindings.tests

import org.scalatest.funspec.AnyFunSpec

class RegexSpec extends AnyFunSpec {
  describe("regex") {
    it("should match regular expressions") {
      //#usage-example
      import scala.scalanative.unsafe._
      import scala.scalanative.unsigned._
      import org.scalanative.bindings.posix.regex._

      val reg = stackalloc[regex_t]

      val compResult =
        regcomp(reg, c"Scala \(J\(S\|VM\)\|Native\)", defines.REG_EXTENDED)
      assert(compResult == 0)

      assert(regexec(reg, c"Scala JVM", 0.toULong, null, 0) == 0)
      assert(regexec(reg, c"Scala JS", 0.toULong, null, 0) == 0)
      assert(regexec(reg, c"Scala Native", 0.toULong, null, 0) == 0)
      assert(regexec(reg, c"Scala .NET", 0.toULong, null, 0) != 0)

      regfree(reg)
      //#usage-example
    }
  }
}
