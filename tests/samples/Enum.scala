package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object Enum {
  type enum_days = CUnsignedInt
  object enum_days {
    final val MONDAY: enum_days = 0.toUInt
    final val TUESDAY: enum_days = 200.toUInt
    final val WEDNESDAY: enum_days = 201.toUInt
    final val THURSDAY: enum_days = 4.toUInt
    final val FRIDAY: enum_days = 5.toUInt
    final val SATURDAY: enum_days = 3.toUInt
    final val SUNDAY: enum_days = 4.toUInt
  }

  type enum_bigValues = CUnsignedLong
  object enum_bigValues {
    final val BIG_A: enum_bigValues = 10000000000L.toULong
    final val BIG_B: enum_bigValues = 1L.toULong
  }

  type enum_anonymous_0 = CUnsignedInt
  object enum_anonymous_0 {
    final val ANON_A: enum_anonymous_0 = 0.toUInt
    final val ANON_B: enum_anonymous_0 = 1.toUInt
  }

  type enum_negativeValues = CInt
  object enum_negativeValues {
    final val NEG_A: enum_negativeValues = -1
    final val NEG_B: enum_negativeValues = -2
  }

  type enum_bigNegativeValues = CLong
  object enum_bigNegativeValues {
    final val BIG_NEG_A: enum_bigNegativeValues = -10000000000L
    final val BIG_NEG_B: enum_bigNegativeValues = -1L
  }

  def get_WEDNESDAY(): enum_days = extern
  def check_BIG_NEG_A(big_neg_a: enum_bigNegativeValues): CString = extern
}
