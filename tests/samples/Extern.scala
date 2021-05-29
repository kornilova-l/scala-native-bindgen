package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object Extern {
  type enum_mode = CUnsignedInt
  object enum_mode {
    final val SYSTEM: enum_mode = 0.toUInt
    final val USER: enum_mode = 1.toUInt
  }

  type struct_version = CStruct3[CInt, CInt, CInt]
  val forty_two: CInt = extern
  val version: CString = extern
  val mode: enum_mode = extern
  val semver: Ptr[struct_version] = extern

  object implicits {
    implicit class struct_version_ops(val p: Ptr[struct_version]) extends AnyVal {
      def major: CInt = p._1
      def major_=(value: CInt): Unit = p._1 = value
      def minor: CInt = p._2
      def minor_=(value: CInt): Unit = p._2 = value
      def patch: CInt = p._3
      def patch_=(value: CInt): Unit = p._3 = value
    }
  }

  object struct_version {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_version] = alloc[struct_version]
    def apply(major: CInt, minor: CInt, patch: CInt)(implicit z: Zone): Ptr[struct_version] = {
      val ptr = alloc[struct_version]
      ptr.major = major
      ptr.minor = minor
      ptr.patch = patch
      ptr
    }
  }
}
