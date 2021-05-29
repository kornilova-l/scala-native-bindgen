package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object ReuseBindings {
  type aliasForBigStruct = org.scalanative.bindgen.samples.Struct.struct_bigStruct
  type struct_usesImportedEnum = CStruct2[org.scalanative.bindgen.samples.Struct.enum_pointIndex, org.scalanative.bindgen.samples.CustomNames.weight]
  def useStruct(p0: Ptr[org.scalanative.bindgen.samples.Struct.struct_point]): Unit = extern
  def returnTypedef_point_s(): Ptr[org.scalanative.bindgen.samples.Struct.struct_point] = extern
  def returnTypedef_point(): Ptr[org.scalanative.bindgen.samples.Struct.point] = extern
  def readBook(book: Ptr[org.scalanative.bindgen.samples.CustomNames.book]): Unit = extern
  def getWeight(weight: Ptr[org.scalanative.bindgen.samples.CustomNames.weight]): Unit = extern
  def getMyInt(): org.scalanative.bindgen.samples.CustomNames.MY_INT = extern
  def getEnum(): org.scalanative.bindgen.samples.CustomNames.EnumWithTypedef = extern

  object implicits {
    implicit class struct_usesImportedEnum_ops(val p: Ptr[struct_usesImportedEnum]) extends AnyVal {
      def index: org.scalanative.bindgen.samples.Struct.enum_pointIndex = p._1
      def index_=(value: org.scalanative.bindgen.samples.Struct.enum_pointIndex): Unit = p._1 = value
      def weight: Ptr[org.scalanative.bindgen.samples.CustomNames.weight] = p._2.asInstanceOf[Ptr[org.scalanative.bindgen.samples.CustomNames.weight]]
      def weight_=(value: Ptr[org.scalanative.bindgen.samples.CustomNames.weight]): Unit = p._2 = value
    }
  }

  object struct_usesImportedEnum {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_usesImportedEnum] = alloc[struct_usesImportedEnum]
    def apply(index: org.scalanative.bindgen.samples.Struct.enum_pointIndex, weight: Ptr[org.scalanative.bindgen.samples.CustomNames.weight])(implicit z: Zone): Ptr[struct_usesImportedEnum] = {
      val ptr = alloc[struct_usesImportedEnum]
      ptr.index = index
      ptr.weight = weight
      ptr
    }
  }
}
