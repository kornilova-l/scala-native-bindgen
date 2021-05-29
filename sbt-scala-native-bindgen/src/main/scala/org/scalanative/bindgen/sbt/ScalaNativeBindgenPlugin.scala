package org.scalanative.bindgen.sbt

import sbt._
import sbt.Keys._

import java.nio.file.Files
import java.nio.file.attribute.{PosixFileAttributeView, PosixFilePermission}

import org.scalanative.bindgen.{Bindgen, BindingOptions}

/**
 * Generate Scala bindings from C headers.
 *
 * == Usage ==
 *
 * This plugin must be explicitly enabled. To enable it add the following line
 * to your `.sbt` file:
 * {{{
 * enablePlugins(ScalaNativeBindgenPlugin)
 * }}}
 *
 * By default, the plugin reads the configured header file and generates
 * a single Scala source file in the managed source directory.
 *
 * See the [[https://github.com/scala-native/scala-native-bindgen/tree/master/sbt-scala-native-bindgen/src/sbt-test/bindgen/generate example project]].
 *
 * == Configuration ==
 *
 * Keys are defined in [[ScalaNativeBindgenPlugin.autoImport]].
 *
 *  - `nativeBindgenPath`: Path to the `scala-native-bindgen` executable.
 *  - `nativeBindings`: Settings for each binding to generate.
 *  - `nativeBindgen / target`: Output folder of the generated code.
 *  - `nativeBindgen / version`: Version of the `scala-native-bindgen`
 *    to use when automatically downloading the executable.
 *  - `nativeBindgen`: Generate Scala Native bindings.
 *
 * @example
 * {{{
 * nativeBindings += {
 *   NativeBinding(file("/usr/include/uv.h"))
 *     .name("uv")
 *     .packageName("org.example.uv")
 *     .link("uv"),
 *     .excludePrefix("__")
 * }
 * }}}
 */
object ScalaNativeBindgenPlugin extends AutoPlugin {

  object autoImport {
    type NativeBinding = BindingOptions
    val NativeBinding      = BindingOptions
    val ScalaNativeBindgen = config("scala-native-bindgen").hide
    val nativeBindgenPath =
      settingKey[Option[File]]("Path to the scala-native-bindgen executable")
    val nativeBindgenResolvedPath =
      taskKey[File]("Resolved path to the scala-native-bindgen executable")
    val nativeBindings =
      settingKey[Seq[NativeBinding]]("Configuration for each bindings")
    val nativeBindgen = taskKey[Seq[File]]("Generate Scala Native bindings")
  }
  import autoImport._

  override def requires = plugins.JvmPlugin

  override def projectSettings: Seq[Setting[_]] =
    inConfig(ScalaNativeBindgen)(Defaults.configSettings) ++
      nativeBindgenScopedSettings(Compile) ++
      nativeBindgenScopedSettings(Test) ++
      Def.settings(
        ivyConfigurations += ScalaNativeBindgen,
        nativeBindgen / version := BuildInfo.version,
        nativeBindgenPath := None,
        libraryDependencies ++= {
          (nativeBindgenPath.value, artifactName) match {
            case (None, Some(name)) =>
              val bindgenVersion = (nativeBindgen / version).value
              val url =
                s"${BuildInfo.projectUrl}/releases/download/v$bindgenVersion/$name"

              Seq(
                BuildInfo.organization % name % bindgenVersion % ScalaNativeBindgen from (url))
            case _ =>
              Seq.empty
          }
        },
        nativeBindgenResolvedPath := Def.taskDyn {
          nativeBindgenPath.value match {
            case Some(path) => Def.task { path }
            case None =>
              Def.task {
                val scalaNativeBindgenUpdate =
                  (ScalaNativeBindgen / update).value

                val artifactFile = artifactName match {
                  case None =>
                    sys.error(
                      "No downloadable binaries available for your OS, " +
                        "please provide path via `nativeBindgenPath`")
                  case Some(name) =>
                    scalaNativeBindgenUpdate
                      .select(artifact = artifactFilter(name = name))
                      .head
                }

                // Set the executable bit on the expected path to fail if it doesn't exist
                for (view <- Option(Files.getFileAttributeView(
                       artifactFile.toPath,
                       classOf[PosixFileAttributeView]))) {
                  val permissions = view.readAttributes.permissions
                  if (permissions.add(PosixFilePermission.OWNER_EXECUTE))
                    view.setPermissions(permissions)
                }

                artifactFile
              }
          }
        }.value
      )

  private val artifactName =
    Option(System.getProperty("os.name")).collect {
      case "Mac OS X" => "scala-native-bindgen-darwin"
      case "Linux"    => "scala-native-bindgen-linux"
    }

  def nativeBindgenScopedSettings(conf: Configuration): Seq[Setting[_]] =
    inConfig(conf)(
      Def.settings(
        nativeBindings := Seq.empty,
        nativeBindgen / target := sourceManaged.value / "sbt-scala-native-bindgen",
        sourceGenerators += Def.taskDyn {
          val nativeBindgenTarget = (nativeBindgen / target).value.toPath
          val managedSource       = sourceManaged.value.toPath

          if (nativeBindgenTarget.startsWith(managedSource)) {
            Def.task { nativeBindgen.value }
          } else {
            Def.task { Seq.empty[File] }
          }
        },
        nativeBindgen := {
          val bindgen         = Bindgen(nativeBindgenResolvedPath.value)
          val optionsList     = nativeBindings.value
          val outputDirectory = (nativeBindgen / target).value
          val logger          = streams.value.log

          // FIXME: Check uniqueness of names.

          optionsList.map {
            options =>
              bindgen.generate(options) match {
                case Right(bindings) =>
                  val output = outputDirectory / s"${bindings.name}.scala"
                  bindings.writeToFile(output)
                  bindings.errors.foreach(error => logger.error(error))
                  output
                case Left(errors) =>
                  errors.foreach(error => logger.error(error))
                  sys.error(
                    "scala-native-bindgen failed with non-zero exit code")
              }
          }
        }
      ))
}
