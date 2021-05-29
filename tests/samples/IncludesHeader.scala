package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object IncludesHeader {
  type enum_semester = CUnsignedInt
  object enum_semester {
    final val AUTUMN: enum_semester = 0.toUInt
    final val SPRING: enum_semester = 1.toUInt
  }

  type size = CInt
  type struct_metadata = CStruct2[CUnsignedInt, CString]
  type metadata = struct_metadata
  type struct_document = CStruct1[metadata]
  type struct_courseInfo = CStruct2[CString, enum_semester]
  def getSize(d: Ptr[struct_document]): size = extern

  object implicits {
    implicit class struct_metadata_ops(val p: Ptr[struct_metadata]) extends AnyVal {
      def year: CUnsignedInt = p._1
      def year_=(value: CUnsignedInt): Unit = p._1 = value
      def publisher: CString = p._2
      def publisher_=(value: CString): Unit = p._2 = value
    }

    implicit class struct_document_ops(val p: Ptr[struct_document]) extends AnyVal {
      def m: Ptr[metadata] = p._1.asInstanceOf[Ptr[metadata]]
      def m_=(value: Ptr[metadata]): Unit = p._1 = value
    }

    implicit class struct_courseInfo_ops(val p: Ptr[struct_courseInfo]) extends AnyVal {
      def name: CString = p._1
      def name_=(value: CString): Unit = p._1 = value
      def s: enum_semester = p._2
      def s_=(value: enum_semester): Unit = p._2 = value
    }
  }

  object struct_metadata {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_metadata] = alloc[struct_metadata]
    def apply(year: CUnsignedInt, publisher: CString)(implicit z: Zone): Ptr[struct_metadata] = {
      val ptr = alloc[struct_metadata]
      ptr.year = year
      ptr.publisher = publisher
      ptr
    }
  }

  object struct_document {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_document] = alloc[struct_document]
    def apply(m: Ptr[metadata])(implicit z: Zone): Ptr[struct_document] = {
      val ptr = alloc[struct_document]
      ptr.m = m
      ptr
    }
  }

  object struct_courseInfo {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_courseInfo] = alloc[struct_courseInfo]
    def apply(name: CString, s: enum_semester)(implicit z: Zone): Ptr[struct_courseInfo] = {
      val ptr = alloc[struct_courseInfo]
      ptr.name = name
      ptr.s = s
      ptr
    }
  }
}
