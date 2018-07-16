package org.scalanative.bindgen

import java.io.File
import org.scalatest.FunSpec
import scala.io.Source
import scala.sys.process.{Process, ProcessLogger}

class BindgenSpec extends FunSpec {
  describe("Bindgen") {
    val bindgenPath    = System.getProperty("bindgen.path")
    val inputDirectory = new File("samples")

    val outputDir = new File("target/bindgen-samples")
    Option(outputDir.listFiles()).foreach(_.foreach(_.delete()))
    outputDir.mkdirs()

    it("should exist") {
      assert(new File(bindgenPath).exists, s"$bindgenPath does not exist")
    }

    def bindgen(inputFile: File, name: String, outputFile: File): Unit = {
      Bindgen()
        .bindgenExecutable(new File(bindgenPath))
        .header(inputFile)
        .name(name)
        .link("bindgentests")
        .packageName("org.scalanative.bindgen.samples")
        .excludePrefix("__")
        .generate()
        .writeToFile(outputFile)
    }

    def contentOf(file: File) =
      Source.fromFile(file).getLines.mkString("\n").trim()

    /**
     * @return valgrind exit code
     */
    def checkMemoryErrors(inputFile: File): Int = {
      val cmd = Seq(
        "valgrind",
        "--leak-check=full",
        "--error-exitcode=1",
        "--suppressions=",
        new File("bindgen/valgrind-suppressions.txt").getAbsolutePath,
        "--show-leak-kinds=definite",
        bindgenPath,
        inputFile.getAbsolutePath,
        "--name",
        "lib",
        "--"
      )
      Process(cmd).run(ProcessLogger(_ => ())).exitValue()
    }

    for (input <- inputDirectory.listFiles() if input.getName.endsWith(".h")) {
      it(s"should generate correct bindings for ${input.getName}") {
        val testName = input.getName.replace(".h", "")
        val expected = new File(inputDirectory, testName + ".scala")
        val output   = new File(outputDir, testName + ".scala")

        bindgen(input, testName, output)

        assert(output.exists())
        assert(contentOf(output) == contentOf(expected))
      }

      it(s"should generate bindings for ${input.getName} without memory errors") {
        assert(0 == checkMemoryErrors(input))
      }
    }
  }
}
