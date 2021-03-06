package org.scalanative.bindgen.samples

import scala.scalanative._
import scala.scalanative.native._

@native.link("bindgentests")
@native.extern
object Extern {
  type enum_mode = native.CUnsignedInt
  object enum_mode {
    final val SYSTEM: enum_mode = 0.toUInt
    final val USER: enum_mode = 1.toUInt
  }

  type struct_version = native.CStruct3[native.CInt, native.CInt, native.CInt]
  val forty_two: native.CInt = native.extern
  val version: native.CString = native.extern
  val mode: enum_mode = native.extern
  val semver: native.Ptr[struct_version] = native.extern

  object implicits {
    implicit class struct_version_ops(val p: native.Ptr[struct_version]) extends AnyVal {
      def major: native.CInt = !p._1
      def major_=(value: native.CInt): Unit = !p._1 = value
      def minor: native.CInt = !p._2
      def minor_=(value: native.CInt): Unit = !p._2 = value
      def patch: native.CInt = !p._3
      def patch_=(value: native.CInt): Unit = !p._3 = value
    }
  }

  object struct_version {
    import implicits._
    def apply()(implicit z: native.Zone): native.Ptr[struct_version] = native.alloc[struct_version]
    def apply(major: native.CInt, minor: native.CInt, patch: native.CInt)(implicit z: native.Zone): native.Ptr[struct_version] = {
      val ptr = native.alloc[struct_version]
      ptr.major = major
      ptr.minor = minor
      ptr.patch = patch
      ptr
    }
  }
}
