package org.scalanative.bindgen

import java.io.{File, PrintWriter}

import org.scalatest.funspec.AnyFunSpec

class BindgenReportingSpec extends AnyFunSpec {
  describe("Bindgen") {

    val bindgen = Bindgen(new File(System.getProperty("bindgen.path")))

    def writeToFile(file: File, input: String): Unit = {
      new PrintWriter(file) {
        try {
          write(input)
        } finally {
          close()
        }
      }
    }

    def assertBindgenError(input: String, errors: Seq[String]): Unit = {
      val tempFile = File.createTempFile("scala-native-bindgen-tests", ".h")
      try {
        writeToFile(tempFile, input)

        val options = BindingOptions(tempFile)
          .name("BindgenTests")
          .link("bindgentests")
          .packageName("org.scalanative.bindgen.samples")
          .excludePrefix("__")

        bindgen.generate(options) match {
          case Right(binding) =>
            assert(binding.errors == errors)
          case Left(errors) =>
            fail(s"Non-zero exit code:\n${errors.mkString("\n")}")
        }
      } finally {
        tempFile.delete()
      }
    }

    it("Skips functions that pass struct or union by value") {
      assertBindgenError(
        """struct s { int a; };
          |void useStruct(struct s);
          |typedef struct s s;
          |s returnStruct();
          |
          |union u { int a; };
          |void useUnion(union u);
          |typedef union u u;
          |u returnUnion();
          |""".stripMargin,
        Seq(
          "Warning: Function useStruct is skipped because Scala Native does not support passing structs and arrays by value.",
          "Warning: Function returnStruct is skipped because Scala Native does not support passing structs and arrays by value.",
          "Warning: Function useUnion is skipped because Scala Native does not support passing structs and arrays by value.",
          "Warning: Function returnUnion is skipped because Scala Native does not support passing structs and arrays by value."
        )
      )
    }

    it("Skips variable with opaque type") {
      assertBindgenError(
        """struct undefinedStruct;
          |extern struct undefinedStruct removedExtern;
          |#define removedExternAlias removedExtern
          |""".stripMargin,
        Seq(
          "Error: Variable removedExtern is skipped because it has incomplete type.",
          "Error: Variable alias removedExternAlias is skipped because it has incomplete type."
        )
      )
    }

    it("Skips unused alias for opaque type") {
      assertBindgenError(
        """union undefinedUnion;
          |typedef union undefinedUnion aliasForUndefinedUnion;
          |void foo();
          |""".stripMargin,
        Seq(
          "Warning: type alias aliasForUndefinedUnion is skipped because it is an unused alias for incomplete type.")
      )
    }
  }
}
