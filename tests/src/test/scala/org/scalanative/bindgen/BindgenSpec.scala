package org.scalanative.bindgen

import java.io.File
import org.scalatest.funspec.AnyFunSpec
import scala.io.Source

class BindgenSpec extends AnyFunSpec {
  describe("Bindgen") {
    val bindgen        = Bindgen(new File(System.getProperty("bindgen.path")))
    val inputDirectory = new File("samples")

    val outputDir = new File("target/bindgen-samples")
    Option(outputDir.listFiles()).foreach(_.foreach(_.delete()))
    outputDir.mkdirs()

    it("executable should exist") {
      assert(bindgen.executable.exists, s"${bindgen.executable} does not exist")
    }

    def contentOf(file: File) =
      Source.fromFile(file).getLines.mkString("\n").trim()

    for (input <- inputDirectory.listFiles() if input.getName.endsWith(".h")) {
      it(s"should generate bindings for ${input.getName}") {
        val testName = input.getName.replace(".h", "")
        val expected = new File(inputDirectory, testName + ".scala")
        val config   = new File(inputDirectory, testName + ".json")
        var options = BindingOptions(input)
          .name(testName)
          .link("bindgentests")
          .packageName("org.scalanative.bindgen.samples")
          .excludePrefix("__")

        if (config.exists()) {
          options = options.bindingConfig(config)
        }

        bindgen.generate(options) match {
          case Right(binding) =>
            assert(binding.source.trim() == contentOf(expected))
          case Left(errors) =>
            fail("scala-native-bindgen failed: " + errors.mkString("\n"))
        }
      }
    }
  }
}
