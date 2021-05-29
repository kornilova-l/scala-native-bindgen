package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

object Typedef {
  type enum_days = CUnsignedInt
  object enum_days {
    final val MONDAY: enum_days = 0.toUInt
    final val TUESDAY: enum_days = 1.toUInt
    final val WEDNESDAY: enum_days = 2.toUInt
    final val THURSDAY: enum_days = 3.toUInt
    final val FRIDAY: enum_days = 4.toUInt
    final val SATURDAY: enum_days = 5.toUInt
    final val SUNDAY: enum_days = 6.toUInt
  }

  type enum_toggle_e = CUnsignedInt
  object enum_toggle_e {
    final val OFF: enum_toggle_e = 0.toUInt
    final val ON: enum_toggle_e = 1.toUInt
  }

  type toggle_e = enum_toggle_e
  type int2int = CFuncPtr1[CInt, CInt]
  type day2string = CFuncPtr1[enum_days, CString]
  type toggle = CFuncPtr1[toggle_e, Unit]
}
