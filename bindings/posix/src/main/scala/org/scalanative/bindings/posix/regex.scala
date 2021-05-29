package org.scalanative.bindings.posix

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@extern
object regex {
  type enum_reg_errcode_t = CUnsignedInt
  object enum_reg_errcode_t {
    final val REG_NOERROR: enum_reg_errcode_t = 0.toUInt
    final val REG_NOMATCH: enum_reg_errcode_t = 1.toUInt
    final val REG_BADPAT: enum_reg_errcode_t = 2.toUInt
    final val REG_ECOLLATE: enum_reg_errcode_t = 3.toUInt
    final val REG_ECTYPE: enum_reg_errcode_t = 4.toUInt
    final val REG_EESCAPE: enum_reg_errcode_t = 5.toUInt
    final val REG_ESUBREG: enum_reg_errcode_t = 6.toUInt
    final val REG_EBRACK: enum_reg_errcode_t = 7.toUInt
    final val REG_EPAREN: enum_reg_errcode_t = 8.toUInt
    final val REG_EBRACE: enum_reg_errcode_t = 9.toUInt
    final val REG_BADBR: enum_reg_errcode_t = 10.toUInt
    final val REG_ERANGE: enum_reg_errcode_t = 11.toUInt
    final val REG_ESPACE: enum_reg_errcode_t = 12.toUInt
    final val REG_BADRPT: enum_reg_errcode_t = 13.toUInt
    final val REG_EEND: enum_reg_errcode_t = 14.toUInt
    final val REG_ESIZE: enum_reg_errcode_t = 15.toUInt
    final val REG_ERPAREN: enum_reg_errcode_t = 16.toUInt
  }

  type s_reg_t = CLong
  type active_reg_t = CUnsignedLong
  type reg_syntax_t = CUnsignedLong
  type reg_errcode_t = enum_reg_errcode_t
  type struct_re_pattern_buffer = CArray[Byte, Nat.Digit2[Nat._6, Nat._4]]
  type regex_t = struct_re_pattern_buffer
  type regoff_t = CInt
  type struct_regmatch_t = CStruct2[regoff_t, regoff_t]
  type regmatch_t = struct_regmatch_t
  val re_syntax_options: reg_syntax_t = extern
  def regcomp(__preg: Ptr[regex_t], __pattern: CString, __cflags: CInt): CInt = extern
  def regexec(__preg: Ptr[regex_t], __string: CString, __nmatch: CSize, __pmatch: Ptr[regmatch_t], __eflags: CInt): CInt = extern
  def regerror(__errcode: CInt, __preg: Ptr[regex_t], __errbuf: CString, __errbuf_size: CSize): CSize = extern
  def regfree(__preg: Ptr[regex_t]): Unit = extern

  object defines {
    val _REGEX_H: CInt = 1
    val REG_EXTENDED: CInt = 1
    val REG_NOTBOL: CInt = 1
  }

  object implicits {
    implicit class struct_regmatch_t_ops(val p: Ptr[struct_regmatch_t]) extends AnyVal {
      def rm_so: regoff_t = p._1
      def rm_so_=(value: regoff_t): Unit = p._1 = value
      def rm_eo: regoff_t = p._2
      def rm_eo_=(value: regoff_t): Unit = p._2 = value
    }
  }

  object struct_regmatch_t {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_regmatch_t] = alloc[struct_regmatch_t]
    def apply(rm_so: regoff_t, rm_eo: regoff_t)(implicit z: Zone): Ptr[struct_regmatch_t] = {
      val ptr = alloc[struct_regmatch_t]
      ptr.rm_so = rm_so
      ptr.rm_eo = rm_eo
      ptr
    }
  }
}
