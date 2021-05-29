package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object ReservedWords {
  type `match` = CInt
  type `var` = CArray[`match`, Nat._5]
  type struct_object = CStruct2[`match`, CInt]
  type `object` = struct_object
  type `type` = struct_object
  type struct_anonymous_0 = CStruct2[CChar, Ptr[`type`]]
  type union_lazy = CArray[Byte, Nat._8]
  type `lazy` = union_lazy
  type `def` = `match`
  type struct_finally = CStruct2[`def`, `lazy`]
  type `finally` = struct_finally
  def `with`(`sealed`: `match`, `implicit`: Ptr[`match`], `forSome`: Ptr[`lazy`]): Ptr[`type`] = extern
  def `implicit`(`type`: Ptr[`finally`]): `match` = extern
  def _1(): Unit = extern

  object implicits {
    implicit class struct_object_ops(val p: Ptr[struct_object]) extends AnyVal {
      def `yield`: `match` = p._1
      def `yield_=`(value: `match`): Unit = p._1 = value
      def `val`: CInt = p._2
      def `val_=`(value: CInt): Unit = p._2 = value
    }

    implicit class struct_anonymous_0_ops(val p: Ptr[struct_anonymous_0]) extends AnyVal {
      def `def`: CChar = p._1
      def `def_=`(value: CChar): Unit = p._1 = value
      def `super`: Ptr[`type`] = p._2
      def `super_=`(value: Ptr[`type`]): Unit = p._2 = value
    }

    implicit class struct_finally_ops(val p: Ptr[struct_finally]) extends AnyVal {
      def `val`: `def` = p._1
      def `val_=`(value: `def`): Unit = p._1 = value
      def `finally`: Ptr[`lazy`] = p._2.asInstanceOf[Ptr[`lazy`]]
      def `finally_=`(value: Ptr[`lazy`]): Unit = p._2 = !value
    }

    implicit class union_lazy_ops(val p: Ptr[union_lazy]) extends AnyVal {
      def instance: Ptr[Ptr[`object`]] = p.asInstanceOf[Ptr[Ptr[`object`]]]
      def instance_=(value: Ptr[`object`]): Unit = !p.asInstanceOf[Ptr[Ptr[`object`]]] = value
      def `forSome`: Ptr[`match`] = p.asInstanceOf[Ptr[`match`]]
      def `forSome_=`(value: `match`): Unit = !p.asInstanceOf[Ptr[`match`]] = value
      def `implicit`: Ptr[Ptr[struct_anonymous_0]] = p.asInstanceOf[Ptr[Ptr[struct_anonymous_0]]]
      def `implicit_=`(value: Ptr[struct_anonymous_0]): Unit = !p.asInstanceOf[Ptr[Ptr[struct_anonymous_0]]] = value
    }
  }

  object struct_object {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_object] = alloc[struct_object]
    def apply(`yield`: `match`, `val`: CInt)(implicit z: Zone): Ptr[struct_object] = {
      val ptr = alloc[struct_object]
      ptr.`yield` = `yield`
      ptr.`val` = `val`
      ptr
    }
  }

  object struct_anonymous_0 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_anonymous_0] = alloc[struct_anonymous_0]
    def apply(`def`: CChar, `super`: Ptr[`type`])(implicit z: Zone): Ptr[struct_anonymous_0] = {
      val ptr = alloc[struct_anonymous_0]
      ptr.`def` = `def`
      ptr.`super` = `super`
      ptr
    }
  }

  object struct_finally {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_finally] = alloc[struct_finally]
    def apply(`val`: `def`, `finally`: Ptr[`lazy`])(implicit z: Zone): Ptr[struct_finally] = {
      val ptr = alloc[struct_finally]
      ptr.`val` = `val`
      ptr.`finally` = `finally`
      ptr
    }
  }
}
