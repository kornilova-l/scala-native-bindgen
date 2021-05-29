package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

object Cycles {
  type struct_node = CStruct2[CInt, Ptr[Byte]]
  type struct_b = CStruct1[Ptr[Ptr[Byte]]]
  type struct_a = CStruct1[Ptr[struct_b]]
  type struct_c = CStruct1[struct_a]
  type struct_FuncPointerCycle2 = CStruct1[CFuncPtr0[Ptr[Byte]]]
  type struct_FuncPointerCycle1 = CStruct1[Ptr[struct_FuncPointerCycle2]]
  type struct_TypeWithTypedef1 = CStruct1[Ptr[struct_TypeWithTypedef2]]
  type TypeWithTypedef1 = struct_TypeWithTypedef1
  type struct_TypeWithTypedef2 = CStruct1[Ptr[Byte]]
  type struct_TwoTypesReplaced1 = CStruct1[Ptr[struct_TwoTypesReplaced2]]
  type struct_TwoTypesReplaced2 = CStruct1[Ptr[struct_TwoTypesReplaced3]]
  type struct_TwoTypesReplaced3 = CStruct1[CFuncPtr1[Ptr[Byte], Ptr[Byte]]]
  type union_cycleWithUnionU = CArray[Byte, Nat._8]
  type struct_cycleWithUnionS = CStruct1[Ptr[union_cycleWithUnionU]]
  type struct_FuncPointerWithValueType2 = CStruct1[CFuncPtr0[CStruct0]]
  type struct_FuncPointerWithValueType1 = CStruct1[Ptr[struct_FuncPointerWithValueType2]]

  object implicits {
    implicit class struct_node_ops(val p: Ptr[struct_node]) extends AnyVal {
      def value: CInt = p._1
      def value_=(value: CInt): Unit = p._1 = value
      def next: Ptr[struct_node] = p._2.asInstanceOf[Ptr[struct_node]]
      def next_=(value: Ptr[struct_node]): Unit = p._2 = value.asInstanceOf[Ptr[Byte]]
    }

    implicit class struct_a_ops(val p: Ptr[struct_a]) extends AnyVal {
      def bb: Ptr[struct_b] = p._1
      def bb_=(value: Ptr[struct_b]): Unit = p._1 = value
    }

    implicit class struct_b_ops(val p: Ptr[struct_b]) extends AnyVal {
      def cc: Ptr[Ptr[struct_c]] = p._1.asInstanceOf[Ptr[Ptr[struct_c]]]
      def cc_=(value: Ptr[Ptr[struct_c]]): Unit = p._1 = value.asInstanceOf[Ptr[Ptr[Byte]]]
    }

    implicit class struct_c_ops(val p: Ptr[struct_c]) extends AnyVal {
      def aa: Ptr[struct_a] = p._1.asInstanceOf[Ptr[struct_a]]
      def aa_=(value: Ptr[struct_a]): Unit = p._1 = value
    }

    implicit class struct_FuncPointerCycle1_ops(val p: Ptr[struct_FuncPointerCycle1]) extends AnyVal {
      def s: Ptr[struct_FuncPointerCycle2] = p._1
      def s_=(value: Ptr[struct_FuncPointerCycle2]): Unit = p._1 = value
    }

    implicit class struct_FuncPointerCycle2_ops(val p: Ptr[struct_FuncPointerCycle2]) extends AnyVal {
      def memberFunction: CFuncPtr0[Ptr[struct_FuncPointerCycle1]] = p._1.asInstanceOf[CFuncPtr0[Ptr[struct_FuncPointerCycle1]]]
      def memberFunction_=(value: CFuncPtr0[Ptr[struct_FuncPointerCycle1]]): Unit = p._1 = value.asInstanceOf[CFuncPtr0[Ptr[Byte]]]
    }

    implicit class struct_TypeWithTypedef2_ops(val p: Ptr[struct_TypeWithTypedef2]) extends AnyVal {
      def s: Ptr[TypeWithTypedef1] = p._1.asInstanceOf[Ptr[TypeWithTypedef1]]
      def s_=(value: Ptr[TypeWithTypedef1]): Unit = p._1 = value.asInstanceOf[Ptr[Byte]]
    }

    implicit class struct_TypeWithTypedef1_ops(val p: Ptr[struct_TypeWithTypedef1]) extends AnyVal {
      def s: Ptr[struct_TypeWithTypedef2] = p._1
      def s_=(value: Ptr[struct_TypeWithTypedef2]): Unit = p._1 = value
    }

    implicit class struct_TwoTypesReplaced3_ops(val p: Ptr[struct_TwoTypesReplaced3]) extends AnyVal {
      def memberFunction: CFuncPtr1[Ptr[struct_TwoTypesReplaced2], Ptr[struct_TwoTypesReplaced1]] = p._1.asInstanceOf[CFuncPtr1[Ptr[struct_TwoTypesReplaced2], Ptr[struct_TwoTypesReplaced1]]]
      def memberFunction_=(value: CFuncPtr1[Ptr[struct_TwoTypesReplaced2], Ptr[struct_TwoTypesReplaced1]]): Unit = p._1 = value.asInstanceOf[CFuncPtr1[Ptr[Byte], Ptr[Byte]]]
    }

    implicit class struct_TwoTypesReplaced1_ops(val p: Ptr[struct_TwoTypesReplaced1]) extends AnyVal {
      def s: Ptr[struct_TwoTypesReplaced2] = p._1
      def s_=(value: Ptr[struct_TwoTypesReplaced2]): Unit = p._1 = value
    }

    implicit class struct_TwoTypesReplaced2_ops(val p: Ptr[struct_TwoTypesReplaced2]) extends AnyVal {
      def s: Ptr[struct_TwoTypesReplaced3] = p._1
      def s_=(value: Ptr[struct_TwoTypesReplaced3]): Unit = p._1 = value
    }

    implicit class struct_cycleWithUnionS_ops(val p: Ptr[struct_cycleWithUnionS]) extends AnyVal {
      def u: Ptr[union_cycleWithUnionU] = p._1
      def u_=(value: Ptr[union_cycleWithUnionU]): Unit = p._1 = value
    }

    implicit class struct_FuncPointerWithValueType1_ops(val p: Ptr[struct_FuncPointerWithValueType1]) extends AnyVal {
      def s: Ptr[struct_FuncPointerWithValueType2] = p._1
      def s_=(value: Ptr[struct_FuncPointerWithValueType2]): Unit = p._1 = value
    }

    implicit class struct_FuncPointerWithValueType2_ops(val p: Ptr[struct_FuncPointerWithValueType2]) extends AnyVal {
      def memberFunction: CFuncPtr0[struct_FuncPointerWithValueType1] = p._1.asInstanceOf[CFuncPtr0[struct_FuncPointerWithValueType1]]
      def memberFunction_=(value: CFuncPtr0[struct_FuncPointerWithValueType1]): Unit = p._1 = value.asInstanceOf[CFuncPtr0[CStruct0]]
    }

    implicit class union_cycleWithUnionU_pos(val p: Ptr[union_cycleWithUnionU]) extends AnyVal {
      def s: Ptr[Ptr[struct_cycleWithUnionS]] = p.asInstanceOf[Ptr[Ptr[struct_cycleWithUnionS]]]
      def s_=(value: Ptr[struct_cycleWithUnionS]): Unit = !p.asInstanceOf[Ptr[Ptr[struct_cycleWithUnionS]]] = value
    }
  }

  object struct_node {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_node] = alloc[struct_node]
    def apply(value: CInt, next: Ptr[struct_node])(implicit z: Zone): Ptr[struct_node] = {
      val ptr = alloc[struct_node]
      ptr.value = value
      ptr.next = next
      ptr
    }
  }

  object struct_a {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_a] = alloc[struct_a]
    def apply(bb: Ptr[struct_b])(implicit z: Zone): Ptr[struct_a] = {
      val ptr = alloc[struct_a]
      ptr.bb = bb
      ptr
    }
  }

  object struct_b {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_b] = alloc[struct_b]
    def apply(cc: Ptr[Ptr[struct_c]])(implicit z: Zone): Ptr[struct_b] = {
      val ptr = alloc[struct_b]
      ptr.cc = cc
      ptr
    }
  }

  object struct_c {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_c] = alloc[struct_c]
    def apply(aa: Ptr[struct_a])(implicit z: Zone): Ptr[struct_c] = {
      val ptr = alloc[struct_c]
      ptr.aa = aa
      ptr
    }
  }

  object struct_FuncPointerCycle1 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_FuncPointerCycle1] = alloc[struct_FuncPointerCycle1]
    def apply(s: Ptr[struct_FuncPointerCycle2])(implicit z: Zone): Ptr[struct_FuncPointerCycle1] = {
      val ptr = alloc[struct_FuncPointerCycle1]
      ptr.s = s
      ptr
    }
  }

  object struct_FuncPointerCycle2 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_FuncPointerCycle2] = alloc[struct_FuncPointerCycle2]
    def apply(memberFunction: CFuncPtr0[Ptr[struct_FuncPointerCycle1]])(implicit z: Zone): Ptr[struct_FuncPointerCycle2] = {
      val ptr = alloc[struct_FuncPointerCycle2]
      ptr.memberFunction = memberFunction
      ptr
    }
  }

  object struct_TypeWithTypedef2 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_TypeWithTypedef2] = alloc[struct_TypeWithTypedef2]
    def apply(s: Ptr[TypeWithTypedef1])(implicit z: Zone): Ptr[struct_TypeWithTypedef2] = {
      val ptr = alloc[struct_TypeWithTypedef2]
      ptr.s = s
      ptr
    }
  }

  object struct_TypeWithTypedef1 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_TypeWithTypedef1] = alloc[struct_TypeWithTypedef1]
    def apply(s: Ptr[struct_TypeWithTypedef2])(implicit z: Zone): Ptr[struct_TypeWithTypedef1] = {
      val ptr = alloc[struct_TypeWithTypedef1]
      ptr.s = s
      ptr
    }
  }

  object struct_TwoTypesReplaced3 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_TwoTypesReplaced3] = alloc[struct_TwoTypesReplaced3]
    def apply(memberFunction: CFuncPtr1[Ptr[struct_TwoTypesReplaced2], Ptr[struct_TwoTypesReplaced1]])(implicit z: Zone): Ptr[struct_TwoTypesReplaced3] = {
      val ptr = alloc[struct_TwoTypesReplaced3]
      ptr.memberFunction = memberFunction
      ptr
    }
  }

  object struct_TwoTypesReplaced1 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_TwoTypesReplaced1] = alloc[struct_TwoTypesReplaced1]
    def apply(s: Ptr[struct_TwoTypesReplaced2])(implicit z: Zone): Ptr[struct_TwoTypesReplaced1] = {
      val ptr = alloc[struct_TwoTypesReplaced1]
      ptr.s = s
      ptr
    }
  }

  object struct_TwoTypesReplaced2 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_TwoTypesReplaced2] = alloc[struct_TwoTypesReplaced2]
    def apply(s: Ptr[struct_TwoTypesReplaced3])(implicit z: Zone): Ptr[struct_TwoTypesReplaced2] = {
      val ptr = alloc[struct_TwoTypesReplaced2]
      ptr.s = s
      ptr
    }
  }

  object struct_cycleWithUnionS {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_cycleWithUnionS] = alloc[struct_cycleWithUnionS]
    def apply(u: Ptr[union_cycleWithUnionU])(implicit z: Zone): Ptr[struct_cycleWithUnionS] = {
      val ptr = alloc[struct_cycleWithUnionS]
      ptr.u = u
      ptr
    }
  }

  object struct_FuncPointerWithValueType1 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_FuncPointerWithValueType1] = alloc[struct_FuncPointerWithValueType1]
    def apply(s: Ptr[struct_FuncPointerWithValueType2])(implicit z: Zone): Ptr[struct_FuncPointerWithValueType1] = {
      val ptr = alloc[struct_FuncPointerWithValueType1]
      ptr.s = s
      ptr
    }
  }

  object struct_FuncPointerWithValueType2 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_FuncPointerWithValueType2] = alloc[struct_FuncPointerWithValueType2]
    def apply(memberFunction: CFuncPtr0[struct_FuncPointerWithValueType1])(implicit z: Zone): Ptr[struct_FuncPointerWithValueType2] = {
      val ptr = alloc[struct_FuncPointerWithValueType2]
      ptr.memberFunction = memberFunction
      ptr
    }
  }
}
