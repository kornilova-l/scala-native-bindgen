name := "test"
organization := "org.scala-native.bindgen.sbt.test"
//#example
enablePlugins(ScalaNativeBindgenPlugin)

//#example
scalaVersion := "2.11.12"
nativeBindgenPath := Some(file(System.getProperty("bindgen.path")))

//#example
inConfig(Compile)(
  Def.settings(
    nativeBindings += {
      NativeBinding(resourceDirectory.value / "stdlib.h")
        .name("stdlib")
        .packageName("org.example.app.stdlib")
        .excludePrefix("__")
    }
  ))
//#example

val nativeBindgenCustomTarget = SettingKey[File]("nativeBindgenCustomTarget")
nativeBindgenCustomTarget := baseDirectory.value / "src/main/scala/org/example"

val nativeBindgenCoreBinding =
  SettingKey[NativeBinding]("nativeBindgenCoreBinding")
nativeBindgenCoreBinding := {
  NativeBinding((Compile / resourceDirectory).value / "core.h")
    .packageName("org.example.app.core")
    .link("core")
}
