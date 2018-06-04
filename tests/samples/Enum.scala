import scala.scalanative._
import scala.scalanative.native._
import scala.scalanative.native.Nat._

@native.link("Enum")
@native.extern
object Enum {
  type enum_days = native.CInt
}

import Enum._

object EnumEnums {
  final val enum_days_MONDAY = 0
  final val enum_days_TUESDAY = 200
  final val enum_days_WEDNESDAY = 201
  final val enum_days_THURSDAY = 4
  final val enum_days_FRIDAY = 5
  final val enum_days_SATURDAY = 3
  final val enum_days_SUNDAY = 4
}