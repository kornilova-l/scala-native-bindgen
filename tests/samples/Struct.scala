package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object Struct {
  type enum_pointIndex = CUnsignedInt
  object enum_pointIndex {
    final val X1: enum_pointIndex = 0.toUInt
    final val Y1: enum_pointIndex = 1.toUInt
    final val X2: enum_pointIndex = 2.toUInt
    final val Y2: enum_pointIndex = 3.toUInt
  }

  type enum_struct_op = CUnsignedInt
  object enum_struct_op {
    final val STRUCT_SET: enum_struct_op = 0.toUInt
    final val STRUCT_TEST: enum_struct_op = 1.toUInt
  }

  type struct_point = CStruct2[CInt, CInt]
  type point = struct_point
  type struct_points = CStruct2[struct_point, point]
  type point_s = Ptr[struct_point]
  type struct_bigStruct = CArray[Byte, Nat.Digit3[Nat._1, Nat._1, Nat._2]]
  type struct_anonymous_0 = CStruct2[CChar, CInt]
  type struct_structWithAnonymousStruct = CStruct2[CInt, struct_anonymous_0]
  type struct_packedStruct = CStruct1[CChar]
  type struct_bitFieldStruct = CArray[Byte, Nat._2]
  type struct_bitFieldOffsetDivByEight = CArray[Byte, Nat._4]
  def setPoints(points: Ptr[struct_points], x1: CInt, y1: CInt, x2: CInt, y2: CInt): Unit = extern
  def getPoint(points: Ptr[struct_points], pointIndex: enum_pointIndex): CInt = extern
  def createPoint(): Ptr[struct_point] = extern
  def getBigStructSize(): CInt = extern
  def getCharFromAnonymousStruct(s: Ptr[struct_structWithAnonymousStruct]): CChar = extern
  def getIntFromAnonymousStruct(s: Ptr[struct_structWithAnonymousStruct]): CChar = extern
  def struct_test_long(s: Ptr[struct_bigStruct], op: enum_struct_op, value: CLong): CInt = extern
  def struct_test_double(s: Ptr[struct_bigStruct], op: enum_struct_op, value: CDouble): CInt = extern
  def struct_test_point(s: Ptr[struct_bigStruct], op: enum_struct_op, value: Ptr[struct_point]): CInt = extern

  object implicits {
    implicit class struct_point_ops(val p: Ptr[struct_point]) extends AnyVal {
      def x: CInt = p._1
      def x_=(value: CInt): Unit = p._1 = value
      def y: CInt = p._2
      def y_=(value: CInt): Unit = p._2 = value
    }

    implicit class struct_points_ops(val p: Ptr[struct_points]) extends AnyVal {
      def p1: Ptr[struct_point] = p.at1
      def p1_=(value: Ptr[struct_point]): Unit = p._1 = value
      def p2: Ptr[point] = p.at2
      def p2_=(value: Ptr[point]): Unit = p._2 = value
    }

    implicit class struct_bigStruct_ops(val p: Ptr[struct_bigStruct]) extends AnyVal {
      def one: CLong = !p.asInstanceOf[Ptr[CLong]]
      def one_=(value: CLong): Unit = !p.asInstanceOf[Ptr[CLong]] = value
      def two: CChar = !(p + 8).asInstanceOf[Ptr[CChar]]
      def two_=(value: CChar): Unit = !(p + 8).asInstanceOf[Ptr[CChar]] = value
      def three: CInt = !(p + 12).asInstanceOf[Ptr[CInt]]
      def three_=(value: CInt): Unit = !(p + 12).asInstanceOf[Ptr[CInt]] = value
      def four: CFloat = !(p + 16).asInstanceOf[Ptr[CFloat]]
      def four_=(value: CFloat): Unit = !(p + 16).asInstanceOf[Ptr[CFloat]] = value
      def five: CDouble = !(p + 24).asInstanceOf[Ptr[CDouble]]
      def five_=(value: CDouble): Unit = !(p + 24).asInstanceOf[Ptr[CDouble]] = value
      def six: Ptr[struct_point] = (p + 32).asInstanceOf[Ptr[struct_point]]
      def six_=(value: Ptr[struct_point]): Unit = !(p + 32).asInstanceOf[Ptr[struct_point]] = value
      def seven: Ptr[struct_point] = !(p + 40).asInstanceOf[Ptr[Ptr[struct_point]]]
      def seven_=(value: Ptr[struct_point]): Unit = !(p + 40).asInstanceOf[Ptr[Ptr[struct_point]]] = value
      def eight: CInt = !(p + 48).asInstanceOf[Ptr[CInt]]
      def eight_=(value: CInt): Unit = !(p + 48).asInstanceOf[Ptr[CInt]] = value
      def nine: CInt = !(p + 52).asInstanceOf[Ptr[CInt]]
      def nine_=(value: CInt): Unit = !(p + 52).asInstanceOf[Ptr[CInt]] = value
      def ten: CInt = !(p + 56).asInstanceOf[Ptr[CInt]]
      def ten_=(value: CInt): Unit = !(p + 56).asInstanceOf[Ptr[CInt]] = value
      def eleven: CInt = !(p + 60).asInstanceOf[Ptr[CInt]]
      def eleven_=(value: CInt): Unit = !(p + 60).asInstanceOf[Ptr[CInt]] = value
      def twelve: CInt = !(p + 64).asInstanceOf[Ptr[CInt]]
      def twelve_=(value: CInt): Unit = !(p + 64).asInstanceOf[Ptr[CInt]] = value
      def thirteen: CInt = !(p + 68).asInstanceOf[Ptr[CInt]]
      def thirteen_=(value: CInt): Unit = !(p + 68).asInstanceOf[Ptr[CInt]] = value
      def fourteen: CInt = !(p + 72).asInstanceOf[Ptr[CInt]]
      def fourteen_=(value: CInt): Unit = !(p + 72).asInstanceOf[Ptr[CInt]] = value
      def fifteen: CInt = !(p + 76).asInstanceOf[Ptr[CInt]]
      def fifteen_=(value: CInt): Unit = !(p + 76).asInstanceOf[Ptr[CInt]] = value
      def sixteen: CInt = !(p + 80).asInstanceOf[Ptr[CInt]]
      def sixteen_=(value: CInt): Unit = !(p + 80).asInstanceOf[Ptr[CInt]] = value
      def seventeen: CInt = !(p + 84).asInstanceOf[Ptr[CInt]]
      def seventeen_=(value: CInt): Unit = !(p + 84).asInstanceOf[Ptr[CInt]] = value
      def eighteen: CInt = !(p + 88).asInstanceOf[Ptr[CInt]]
      def eighteen_=(value: CInt): Unit = !(p + 88).asInstanceOf[Ptr[CInt]] = value
      def nineteen: CInt = !(p + 92).asInstanceOf[Ptr[CInt]]
      def nineteen_=(value: CInt): Unit = !(p + 92).asInstanceOf[Ptr[CInt]] = value
      def twenty: CInt = !(p + 96).asInstanceOf[Ptr[CInt]]
      def twenty_=(value: CInt): Unit = !(p + 96).asInstanceOf[Ptr[CInt]] = value
      def twentyOne: CInt = !(p + 100).asInstanceOf[Ptr[CInt]]
      def twentyOne_=(value: CInt): Unit = !(p + 100).asInstanceOf[Ptr[CInt]] = value
      def twentyTwo: CInt = !(p + 104).asInstanceOf[Ptr[CInt]]
      def twentyTwo_=(value: CInt): Unit = !(p + 104).asInstanceOf[Ptr[CInt]] = value
      def twentyThree: CInt = !(p + 108).asInstanceOf[Ptr[CInt]]
      def twentyThree_=(value: CInt): Unit = !(p + 108).asInstanceOf[Ptr[CInt]] = value
    }

    implicit class struct_anonymous_0_ops(val p: Ptr[struct_anonymous_0]) extends AnyVal {
      def c: CChar = p._1
      def c_=(value: CChar): Unit = p._1 = value
      def i: CInt = p._2
      def i_=(value: CInt): Unit = p._2 = value
    }

    implicit class struct_structWithAnonymousStruct_ops(val p: Ptr[struct_structWithAnonymousStruct]) extends AnyVal {
      def a: CInt = p._1
      def a_=(value: CInt): Unit = p._1 = value
      def anonymousStruct: Ptr[struct_anonymous_0] = p.at2
      def anonymousStruct_=(value: Ptr[struct_anonymous_0]): Unit = p._2 = value
    }
  }

  object struct_point {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_point] = alloc[struct_point]
    def apply(x: CInt, y: CInt)(implicit z: Zone): Ptr[struct_point] = {
      val ptr = alloc[struct_point]
      ptr.x = x
      ptr.y = y
      ptr
    }
  }

  object struct_points {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_points] = alloc[struct_points]
    def apply(p1: Ptr[struct_point], p2: Ptr[point])(implicit z: Zone): Ptr[struct_points] = {
      val ptr = alloc[struct_points]
      ptr.p1 = p1
      ptr.p2 = p2
      ptr
    }
  }

  object struct_bigStruct {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_bigStruct] = alloc[struct_bigStruct]
    def apply(one: CLong, two: CChar, three: CInt, four: CFloat, five: CDouble, six: Ptr[struct_point], seven: Ptr[struct_point], eight: CInt, nine: CInt, ten: CInt, eleven: CInt, twelve: CInt, thirteen: CInt, fourteen: CInt, fifteen: CInt, sixteen: CInt, seventeen: CInt, eighteen: CInt, nineteen: CInt, twenty: CInt, twentyOne: CInt, twentyTwo: CInt, twentyThree: CInt)(implicit z: Zone): Ptr[struct_bigStruct] = {
      val ptr = alloc[struct_bigStruct]
      ptr.one = one
      ptr.two = two
      ptr.three = three
      ptr.four = four
      ptr.five = five
      ptr.six = six
      ptr.seven = seven
      ptr.eight = eight
      ptr.nine = nine
      ptr.ten = ten
      ptr.eleven = eleven
      ptr.twelve = twelve
      ptr.thirteen = thirteen
      ptr.fourteen = fourteen
      ptr.fifteen = fifteen
      ptr.sixteen = sixteen
      ptr.seventeen = seventeen
      ptr.eighteen = eighteen
      ptr.nineteen = nineteen
      ptr.twenty = twenty
      ptr.twentyOne = twentyOne
      ptr.twentyTwo = twentyTwo
      ptr.twentyThree = twentyThree
      ptr
    }
  }

  object struct_anonymous_0 {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_anonymous_0] = alloc[struct_anonymous_0]
    def apply(c: CChar, i: CInt)(implicit z: Zone): Ptr[struct_anonymous_0] = {
      val ptr = alloc[struct_anonymous_0]
      ptr.c = c
      ptr.i = i
      ptr
    }
  }

  object struct_structWithAnonymousStruct {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_structWithAnonymousStruct] = alloc[struct_structWithAnonymousStruct]
    def apply(a: CInt, anonymousStruct: Ptr[struct_anonymous_0])(implicit z: Zone): Ptr[struct_structWithAnonymousStruct] = {
      val ptr = alloc[struct_structWithAnonymousStruct]
      ptr.a = a
      ptr.anonymousStruct = anonymousStruct
      ptr
    }
  }
}
