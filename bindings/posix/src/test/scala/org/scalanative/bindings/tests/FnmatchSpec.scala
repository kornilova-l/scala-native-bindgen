package org.scalanative.bindings.tests

import org.scalatest.funspec.AnyFunSpec

class FnmatchSpec extends AnyFunSpec {
  describe("fnmatch") {
    it("should match patterns") {
      //#usage-example
      import scala.scalanative.unsafe._
      import org.scalanative.bindings.posix.fnmatch._

      assert(fnmatch(c"*.md", c"README.md", 0) == 0)
      assert(fnmatch(c"*.[ch]", c"main.h", 0) == 0)
      assert(fnmatch(c"*.[ch]", c"main.c", 0) == 0)
      assert(fnmatch(c"*.md", c"README_md", 0) == defines.FNM_NOMATCH)
      //#usage-example
    }
  }
}
