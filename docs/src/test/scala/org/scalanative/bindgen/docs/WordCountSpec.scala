package org.scalanative.bindgen.docs

import org.scalatest.funspec.AnyFunSpec

class WordCountSpec extends AnyFunSpec {
  describe("wordcount") {
    it("using generated bindings") {
      //#example
      import org.example.wordcount.WordCount._
      import scala.scalanative.unsafe._
      import scala.scalanative.libc._

      //#example
      val pathToFile =
        c"docs/src/test/resources/scala-native-bindings/wordcount.h"

      import scala.scalanative.posix._, unistd.access, unistd.R_OK
      assert(access(pathToFile, R_OK) == 0, "Header file does not exist")

      //#example
      Zone { implicit zone =>
        val result = struct_wordcount()
        val file   = stdio.fopen(pathToFile, c"r")
        val code   = wordcount(result, file)
        stdio.fclose(file)
        //#example
        import org.example.wordcount.WordCount.implicits._
        assert(code == 0)
        assert(result.chars == 187)
        assert(result.words == 20)
        assert(result.lines == 11)
      //#example
      }
      //#example
    }
  }
}
