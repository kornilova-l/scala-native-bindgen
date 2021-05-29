import scala.sys.process._
import scala.util.Try
import org.scalanative.bindgen.sbt.ScalaNativeBindgenPlugin
import BindingHelpers._

addCommandAlias("verify", "; test ; ^scripted ; docs/makeSite")

val scala211  = "2.11.12"
val scala212  = "2.12.13"
val sbt1      = "1.3.13"
val scalaTest = "3.2.4-M1"


inThisBuild(
  Def.settings(
    organization := "org.scala-native.bindgen",
    licenses := Seq(
      "BSD 3-Clause" -> url("https://www.scala-lang.org/license/")),
    homepage := Some(
      url("https://scala-native.github.io/scala-native-bindgen")),
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-encoding",
      "utf8"
    ),
    Global / parallelExecution := false,
    scmInfo := Some(
      ScmInfo(url("https://github.com/scala-native/scala-native-bindgen"),
              "scm:git:git@github.com:scala-native/scala-native-bindgen.git")),
    developers := List(
      Developer(
        id = "kornilova-l",
        name = "Liudmila Kornilova",
        email = "kornilova-l@users.noreply.github.com",
        url = url("https://github.com/kornilova-l")
      ),
      Developer(
        id = "jonas",
        name = "Jonas Fonseca",
        email = "jonas@users.noreply.github.com",
        url = url("https://github.com/jonas")
      )
    )
  ))

val root = project("scala-native-bindgen")
  .in(file("."))
  .aggregate(
    tests,
    samples,
    tools,
    sbtPlugin,
    bindings,
    docs
  )
  .enablePlugins(ReleasePlugin)
  .settings(
    publish / skip := true,
    releaseCrossBuild := false,
    releaseVersionFile := target.value / "unused-version.sbt",
    releaseProcess := {
      import ReleaseTransformations._

      val setReleaseVersions: String => State => State =
        v => _.put(ReleaseKeys.versions, (v, v))

      Seq[ReleaseStep](
        checkSnapshotDependencies,
        setReleaseVersions(version.value),
        runClean,
        releaseStepCommandAndRemaining("verify"),
        setReleaseVersion,
        tagRelease,
        releaseStepCommandAndRemaining("+bindings/publish"),
        releaseStepCommandAndRemaining("+tools/publish"),
        releaseStepCommandAndRemaining("^sbt-scala-native-bindgen/publish"),
        pushChanges,
        releaseStepTask(docs / ghpagesPushSite)
      )
    }
  )

lazy val tests = project("tests")
  .dependsOn(tools)
  .settings(
    publish / skip := true,
    Test / fork := true,
    Test / javaOptions += {
      val rootDir = (ThisBuild / baseDirectory).value
      s"-Dbindgen.path=$rootDir/bindgen/target/scala-native-bindgen"
    },
    watchSources += WatchSource(
      baseDirectory.value / "tests" / "samples",
      "*.h" || "*.scala",
      NothingFilter
    ),
    libraryDependencies += "org.scalatest" %% "scalatest" % scalaTest % Test
  )
  .aggregate(samples)

lazy val samples = nativeProject("samples")
  .in(file("tests/samples"))
  .settings(
    publish / skip := true,
    libraryDependencies += "org.scalatest" %%% "scalatest" % scalaTest % Test,
    compileTask("bindgentests", baseDirectory)
  )

lazy val tools = project("tools")
  .settings(
    crossScalaVersions := List(scala211,
                               scala212)
  )

lazy val sbtPlugin = project("sbt-scala-native-bindgen")
  .dependsOn(tools)
  .enablePlugins(SbtPlugin, BuildInfoPlugin)
  .settings(
    Keys.sbtPlugin := true,
    scriptedLaunchOpts += s"-Dplugin.version=${version.value}",
    scriptedLaunchOpts += {
      val rootDir = (ThisBuild / baseDirectory).value
      s"-Dbindgen.path=$rootDir/bindgen/target/scala-native-bindgen"
    },
    buildInfoPackage := "org.scalanative.bindgen.sbt",
    buildInfoKeys := Seq[BuildInfoKey](
      version,
      organization,
      BuildInfoKey.map(scmInfo) {
        case (k, v) => "projectUrl" -> v.get.browseUrl
      }
    ),
    publishLocal := publishLocal.dependsOn(tools / publishLocal).value
  )

lazy val docsUsingBindingsDirectory       = settingKey[File]("vector source")
lazy val docs3rdPartyBindingsDirectory    = settingKey[File]("geometry source")
lazy val docsScalaNativeBindingsDirectory = settingKey[File]("wordcount source")

lazy val docs = nativeProject("docs")
  .enablePlugins(GhpagesPlugin, ParadoxSitePlugin, ParadoxMaterialThemePlugin)
  .enablePlugins(ScalaNativeBindgenPlugin)
  .settings(
    org.scalanative.bindgen.build.ParadoxSupport.paradoxWithCustomDirectives)
  .settings(
    publish / skip := true,
    docsUsingBindingsDirectory := (Test / resourceDirectory).value / "using-bindings",
    docs3rdPartyBindingsDirectory := (Test / resourceDirectory).value / "3rd-party-bindings",
    docsScalaNativeBindingsDirectory := (Test / resourceDirectory).value / "scala-native-bindings",
    Test / nativeBindings ++= Seq(
      NativeBinding(docsUsingBindingsDirectory.value / "vector.h")
        .name("vector")
        .link("vector")
        .packageName("org.example"), {
        val pathToHeader = docs3rdPartyBindingsDirectory.value / "geometry.h"
        val pathToConfig = docs3rdPartyBindingsDirectory.value / "config.json"
        //#sbt-binding-config
        NativeBinding(pathToHeader)
          .bindingConfig(pathToConfig)
          //#sbt-binding-config
          .name("Geometry")
          .link("geometry")
          .packageName("org.example.geometry")
      }, {
        val pathToHeader = docsScalaNativeBindingsDirectory.value / "wordcount.h"
        //#sbt-exclude-prefix
        NativeBinding(pathToHeader)
          .excludePrefix("__")
          //#sbt-exclude-prefix
          .name("WordCount")
          .link("wordcount")
          .packageName("org.example.wordcount")
          .bindingConfig(docsScalaNativeBindingsDirectory.value / "config.json")
      }
    ),
    nativeBindgenPath := {
      Some(
        (ThisBuild / baseDirectory).value / "bindgen/target/scala-native-bindgen")
    },
    Test / nativeBindgen / target := (Test / scalaSource).value / "org/example",
    compileTask("vector", docsUsingBindingsDirectory),
    compileTask("geometry", docs3rdPartyBindingsDirectory),
    compileTask("wordcount", docsScalaNativeBindingsDirectory),
    libraryDependencies += "org.scalatest" %%% "scalatest" % scalaTest % Test,
    Paradox / paradoxProperties ++= Map(
      "github.base_url" -> scmInfo.value.get.browseUrl.toString
    ),
    ParadoxMaterialThemePlugin.paradoxMaterialThemeSettings(Paradox),
    Paradox / paradoxMaterialTheme := {
      val licenseUrl = scmInfo.value.get.browseUrl.toString + "blob/master/LICENSE.txt"
      (Paradox / paradoxMaterialTheme).value
        .withRepository(scmInfo.value.get.browseUrl.toURI)
        .withColor("indigo", "indigo")
        .withCopyright(
          s"Copyright © Liudmila Kornilova, distributed under the <a href='$licenseUrl'>Scala license</a>.")
    }
  )

lazy val bindings = project("bindings")
  .settings(
    publish / skip := true,
    scalaVersion := scala211
  )
  .aggregate(
    libiconv,
    libposix,
    libutf8proc
  )

lazy val libiconv = bindingProject("iconv")("iconv.h")
  .settings(
    //#sbt-iconv-linking-options
    Compile / nativeLinkingOptions ++= {
      Option(System.getProperty("os.name")) match {
        case Some("Mac OS X") => Seq("-liconv")
        case _                => Seq.empty
      }
    }
    //#sbt-iconv-linking-options
    ,
    Test / nativeLinkingOptions ++= {
      (Compile / nativeLinkingOptions).value
    }
  )

//#sbt-binding-project-multi-header
lazy val libposix = bindingProject("posix")(
  "fnmatch.h",
  "regex.h"
)
//#sbt-binding-project-multi-header

//#sbt-binding-project
lazy val libutf8proc =
  bindingProject("utf8proc", Some("utf8proc"))("utf8proc.h")
//#sbt-binding-project

def project(name: String) = {
  Project(id = name, base = file(name))
    .enablePlugins(GitPlugin, GitVersioning)
    .settings(
      versionWithGit,
      git.useGitDescribe := true,
      git.remoteRepo := scmInfo.value.get.connection.replace("scm:git:", ""),
      crossSbtVersions := List(sbt1),
      scalaVersion := {
        (pluginCrossBuild / sbtBinaryVersion).value match {
          case _      => scala212
        }
      },
      bintrayOrganization := Some("scala-native-bindgen"),
      bintrayRepository := {
        if (Keys.sbtPlugin.value) "sbt-plugins"
        else "maven"
      },
      publishMavenStyle := Keys.sbtPlugin.value == false,
      Test / publishArtifact := false
    )
}

def nativeProject(name: String) = {
  project(name)
    .enablePlugins(ScalaNativePlugin)
    .settings(
      scalaVersion := scala211,
      nativeLinkStubs := true
    )
}

def compileTask(libname: String, srcDirTask: SettingKey[File]) = Def.settings(
  Test / nativeLinkingOptions += {
    Seq("-L", (Test / target).value.getAbsoluteFile / "bindgen").mkString
  },
  Test / compile := {
    val log            = streams.value.log
    val cwd            = (Test / target).value / "bindgen"
    val compileOptions = (Test / nativeCompileOptions).value
    val cpaths         = (srcDirTask.value.getAbsoluteFile * "*.c").get
    val clangPath      = nativeClang.value.toPath.toAbsolutePath.toString

    cwd.mkdirs()

    def abs(path: File): String =
      path.getAbsolutePath

    def run(command: Seq[String]): Int = {
      log.info("Running " + command.mkString(" "))
      Process(command, cwd) ! log
    }

    val opaths = cpaths.map {
      cpath =>
        val opath = cwd / s"${cpath.getName}.o"
        val command = Seq(clangPath) ++ compileOptions ++ Seq("-c",
                                                              abs(cpath),
                                                              "-o",
                                                              abs(opath))
        val doCompile =
          !opath.exists() || cpath.lastModified() >= opath.lastModified()

        if (doCompile && run(command) != 0) {
          sys.error(s"Failed to compile $cpath")
        }
        opath
    }

    val archivePath = cwd / s"lib$libname.a"
    val archive     = Seq("ar", "cr", abs(archivePath)) ++ opaths.map(abs)
    val doArchive =
      opaths.map(_.lastModified).max >= archivePath.lastModified()

    if (doArchive && run(archive) != 0) {
      sys.error(s"Failed to create archive $archivePath")
    }

    (Test / compile).value
  }
)

// Add Clang's directory include dir with platform specific headers, like stddef.h.
lazy val bindingsExtraArgs = Try {
  val version = "llvm-config --version".!!.trim
  val libDir  = "llvm-config --libdir".!!.trim
  s"-I$libDir/clang/$version/include"
}.toOption

def bindingProject(name: String, link: Option[String] = None)(
    headers: String*) = {
  val packagePath = Seq("org", "scalanative", "bindings") ++
    (if (headers.length > 1) Seq(name) else Seq.empty)

  nativeProject(s"lib$name")
    .enablePlugins(ScalaNativeBindgenPlugin)
    .in(file(s"bindings/$name"))
    .settings(
      Keys.name := name,
      libraryDependencies += "org.scalatest" %%% "scalatest" % scalaTest % Test,
      Compile / nativeBindgen / target := {
        packagePath.foldLeft((Compile / scalaSource).value)(_ / _)
      }
    )
    .settings(
      headers.flatMap(binding(name, packagePath.mkString("."), link)): _*)
}

def binding(name: String, packageName: String, link: Option[String])(
    header: String): Seq[Setting[_]] = {
  val includeDirs = Seq("/usr/include", "/usr/local/include")
  val headerFiles = includeDirs.map(dir => file(dir) / header).filter(_.exists)

  headerFiles.headOption match {
    case Some(headerFile) =>
      Def.settings(
        organization := "org.scala-native.binding",
        Compile / nativeBindings += {
          NativeBinding(headerFile)
            .name(link.map(_ => name).getOrElse(header.replace(".h", "")))
            .packageName(packageName)
            .maybe(link, _.link)
            .excludePrefix("__")
            .extraArgs(bindingsExtraArgs.toSeq: _*)
            .extraArgs("-D_POSIX_C_SOURCE")
        }
      )

    case None =>
      Def.settings(
        test := {
          streams.value.log
            .warn(s"Skipping $name tests due to missing header file <$header>")
        }
      )
  }
}
