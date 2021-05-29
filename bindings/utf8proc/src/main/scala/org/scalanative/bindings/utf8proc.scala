package org.scalanative.bindings

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._, Nat._

@link("utf8proc")
@extern
object utf8proc {
  type enum_utf8proc_option_t = CUnsignedInt
  object enum_utf8proc_option_t {
    final val UTF8PROC_NULLTERM: enum_utf8proc_option_t = 1.toUInt
    final val UTF8PROC_STABLE: enum_utf8proc_option_t = 2.toUInt
    final val UTF8PROC_COMPAT: enum_utf8proc_option_t = 4.toUInt
    final val UTF8PROC_COMPOSE: enum_utf8proc_option_t = 8.toUInt
    final val UTF8PROC_DECOMPOSE: enum_utf8proc_option_t = 16.toUInt
    final val UTF8PROC_IGNORE: enum_utf8proc_option_t = 32.toUInt
    final val UTF8PROC_REJECTNA: enum_utf8proc_option_t = 64.toUInt
    final val UTF8PROC_NLF2LS: enum_utf8proc_option_t = 128.toUInt
    final val UTF8PROC_NLF2PS: enum_utf8proc_option_t = 256.toUInt
    final val UTF8PROC_NLF2LF: enum_utf8proc_option_t = 384.toUInt
    final val UTF8PROC_STRIPCC: enum_utf8proc_option_t = 512.toUInt
    final val UTF8PROC_CASEFOLD: enum_utf8proc_option_t = 1024.toUInt
    final val UTF8PROC_CHARBOUND: enum_utf8proc_option_t = 2048.toUInt
    final val UTF8PROC_LUMP: enum_utf8proc_option_t = 4096.toUInt
    final val UTF8PROC_STRIPMARK: enum_utf8proc_option_t = 8192.toUInt
  }

  type enum_utf8proc_category_t = CUnsignedInt
  object enum_utf8proc_category_t {
    final val UTF8PROC_CATEGORY_CN: enum_utf8proc_category_t = 0.toUInt
    final val UTF8PROC_CATEGORY_LU: enum_utf8proc_category_t = 1.toUInt
    final val UTF8PROC_CATEGORY_LL: enum_utf8proc_category_t = 2.toUInt
    final val UTF8PROC_CATEGORY_LT: enum_utf8proc_category_t = 3.toUInt
    final val UTF8PROC_CATEGORY_LM: enum_utf8proc_category_t = 4.toUInt
    final val UTF8PROC_CATEGORY_LO: enum_utf8proc_category_t = 5.toUInt
    final val UTF8PROC_CATEGORY_MN: enum_utf8proc_category_t = 6.toUInt
    final val UTF8PROC_CATEGORY_MC: enum_utf8proc_category_t = 7.toUInt
    final val UTF8PROC_CATEGORY_ME: enum_utf8proc_category_t = 8.toUInt
    final val UTF8PROC_CATEGORY_ND: enum_utf8proc_category_t = 9.toUInt
    final val UTF8PROC_CATEGORY_NL: enum_utf8proc_category_t = 10.toUInt
    final val UTF8PROC_CATEGORY_NO: enum_utf8proc_category_t = 11.toUInt
    final val UTF8PROC_CATEGORY_PC: enum_utf8proc_category_t = 12.toUInt
    final val UTF8PROC_CATEGORY_PD: enum_utf8proc_category_t = 13.toUInt
    final val UTF8PROC_CATEGORY_PS: enum_utf8proc_category_t = 14.toUInt
    final val UTF8PROC_CATEGORY_PE: enum_utf8proc_category_t = 15.toUInt
    final val UTF8PROC_CATEGORY_PI: enum_utf8proc_category_t = 16.toUInt
    final val UTF8PROC_CATEGORY_PF: enum_utf8proc_category_t = 17.toUInt
    final val UTF8PROC_CATEGORY_PO: enum_utf8proc_category_t = 18.toUInt
    final val UTF8PROC_CATEGORY_SM: enum_utf8proc_category_t = 19.toUInt
    final val UTF8PROC_CATEGORY_SC: enum_utf8proc_category_t = 20.toUInt
    final val UTF8PROC_CATEGORY_SK: enum_utf8proc_category_t = 21.toUInt
    final val UTF8PROC_CATEGORY_SO: enum_utf8proc_category_t = 22.toUInt
    final val UTF8PROC_CATEGORY_ZS: enum_utf8proc_category_t = 23.toUInt
    final val UTF8PROC_CATEGORY_ZL: enum_utf8proc_category_t = 24.toUInt
    final val UTF8PROC_CATEGORY_ZP: enum_utf8proc_category_t = 25.toUInt
    final val UTF8PROC_CATEGORY_CC: enum_utf8proc_category_t = 26.toUInt
    final val UTF8PROC_CATEGORY_CF: enum_utf8proc_category_t = 27.toUInt
    final val UTF8PROC_CATEGORY_CS: enum_utf8proc_category_t = 28.toUInt
    final val UTF8PROC_CATEGORY_CO: enum_utf8proc_category_t = 29.toUInt
  }

  type enum_utf8proc_bidi_class_t = CUnsignedInt
  object enum_utf8proc_bidi_class_t {
    final val UTF8PROC_BIDI_CLASS_L: enum_utf8proc_bidi_class_t = 1.toUInt
    final val UTF8PROC_BIDI_CLASS_LRE: enum_utf8proc_bidi_class_t = 2.toUInt
    final val UTF8PROC_BIDI_CLASS_LRO: enum_utf8proc_bidi_class_t = 3.toUInt
    final val UTF8PROC_BIDI_CLASS_R: enum_utf8proc_bidi_class_t = 4.toUInt
    final val UTF8PROC_BIDI_CLASS_AL: enum_utf8proc_bidi_class_t = 5.toUInt
    final val UTF8PROC_BIDI_CLASS_RLE: enum_utf8proc_bidi_class_t = 6.toUInt
    final val UTF8PROC_BIDI_CLASS_RLO: enum_utf8proc_bidi_class_t = 7.toUInt
    final val UTF8PROC_BIDI_CLASS_PDF: enum_utf8proc_bidi_class_t = 8.toUInt
    final val UTF8PROC_BIDI_CLASS_EN: enum_utf8proc_bidi_class_t = 9.toUInt
    final val UTF8PROC_BIDI_CLASS_ES: enum_utf8proc_bidi_class_t = 10.toUInt
    final val UTF8PROC_BIDI_CLASS_ET: enum_utf8proc_bidi_class_t = 11.toUInt
    final val UTF8PROC_BIDI_CLASS_AN: enum_utf8proc_bidi_class_t = 12.toUInt
    final val UTF8PROC_BIDI_CLASS_CS: enum_utf8proc_bidi_class_t = 13.toUInt
    final val UTF8PROC_BIDI_CLASS_NSM: enum_utf8proc_bidi_class_t = 14.toUInt
    final val UTF8PROC_BIDI_CLASS_BN: enum_utf8proc_bidi_class_t = 15.toUInt
    final val UTF8PROC_BIDI_CLASS_B: enum_utf8proc_bidi_class_t = 16.toUInt
    final val UTF8PROC_BIDI_CLASS_S: enum_utf8proc_bidi_class_t = 17.toUInt
    final val UTF8PROC_BIDI_CLASS_WS: enum_utf8proc_bidi_class_t = 18.toUInt
    final val UTF8PROC_BIDI_CLASS_ON: enum_utf8proc_bidi_class_t = 19.toUInt
    final val UTF8PROC_BIDI_CLASS_LRI: enum_utf8proc_bidi_class_t = 20.toUInt
    final val UTF8PROC_BIDI_CLASS_RLI: enum_utf8proc_bidi_class_t = 21.toUInt
    final val UTF8PROC_BIDI_CLASS_FSI: enum_utf8proc_bidi_class_t = 22.toUInt
    final val UTF8PROC_BIDI_CLASS_PDI: enum_utf8proc_bidi_class_t = 23.toUInt
  }

  type enum_utf8proc_decomp_type_t = CUnsignedInt
  object enum_utf8proc_decomp_type_t {
    final val UTF8PROC_DECOMP_TYPE_FONT: enum_utf8proc_decomp_type_t = 1.toUInt
    final val UTF8PROC_DECOMP_TYPE_NOBREAK: enum_utf8proc_decomp_type_t = 2.toUInt
    final val UTF8PROC_DECOMP_TYPE_INITIAL: enum_utf8proc_decomp_type_t = 3.toUInt
    final val UTF8PROC_DECOMP_TYPE_MEDIAL: enum_utf8proc_decomp_type_t = 4.toUInt
    final val UTF8PROC_DECOMP_TYPE_FINAL: enum_utf8proc_decomp_type_t = 5.toUInt
    final val UTF8PROC_DECOMP_TYPE_ISOLATED: enum_utf8proc_decomp_type_t = 6.toUInt
    final val UTF8PROC_DECOMP_TYPE_CIRCLE: enum_utf8proc_decomp_type_t = 7.toUInt
    final val UTF8PROC_DECOMP_TYPE_SUPER: enum_utf8proc_decomp_type_t = 8.toUInt
    final val UTF8PROC_DECOMP_TYPE_SUB: enum_utf8proc_decomp_type_t = 9.toUInt
    final val UTF8PROC_DECOMP_TYPE_VERTICAL: enum_utf8proc_decomp_type_t = 10.toUInt
    final val UTF8PROC_DECOMP_TYPE_WIDE: enum_utf8proc_decomp_type_t = 11.toUInt
    final val UTF8PROC_DECOMP_TYPE_NARROW: enum_utf8proc_decomp_type_t = 12.toUInt
    final val UTF8PROC_DECOMP_TYPE_SMALL: enum_utf8proc_decomp_type_t = 13.toUInt
    final val UTF8PROC_DECOMP_TYPE_SQUARE: enum_utf8proc_decomp_type_t = 14.toUInt
    final val UTF8PROC_DECOMP_TYPE_FRACTION: enum_utf8proc_decomp_type_t = 15.toUInt
    final val UTF8PROC_DECOMP_TYPE_COMPAT: enum_utf8proc_decomp_type_t = 16.toUInt
  }

  type enum_utf8proc_boundclass_t = CUnsignedInt
  object enum_utf8proc_boundclass_t {
    final val UTF8PROC_BOUNDCLASS_START: enum_utf8proc_boundclass_t = 0.toUInt
    final val UTF8PROC_BOUNDCLASS_OTHER: enum_utf8proc_boundclass_t = 1.toUInt
    final val UTF8PROC_BOUNDCLASS_CR: enum_utf8proc_boundclass_t = 2.toUInt
    final val UTF8PROC_BOUNDCLASS_LF: enum_utf8proc_boundclass_t = 3.toUInt
    final val UTF8PROC_BOUNDCLASS_CONTROL: enum_utf8proc_boundclass_t = 4.toUInt
    final val UTF8PROC_BOUNDCLASS_EXTEND: enum_utf8proc_boundclass_t = 5.toUInt
    final val UTF8PROC_BOUNDCLASS_L: enum_utf8proc_boundclass_t = 6.toUInt
    final val UTF8PROC_BOUNDCLASS_V: enum_utf8proc_boundclass_t = 7.toUInt
    final val UTF8PROC_BOUNDCLASS_T: enum_utf8proc_boundclass_t = 8.toUInt
    final val UTF8PROC_BOUNDCLASS_LV: enum_utf8proc_boundclass_t = 9.toUInt
    final val UTF8PROC_BOUNDCLASS_LVT: enum_utf8proc_boundclass_t = 10.toUInt
    final val UTF8PROC_BOUNDCLASS_REGIONAL_INDICATOR: enum_utf8proc_boundclass_t = 11.toUInt
    final val UTF8PROC_BOUNDCLASS_SPACINGMARK: enum_utf8proc_boundclass_t = 12.toUInt
    final val UTF8PROC_BOUNDCLASS_PREPEND: enum_utf8proc_boundclass_t = 13.toUInt
    final val UTF8PROC_BOUNDCLASS_ZWJ: enum_utf8proc_boundclass_t = 14.toUInt
    final val UTF8PROC_BOUNDCLASS_E_BASE: enum_utf8proc_boundclass_t = 15.toUInt
    final val UTF8PROC_BOUNDCLASS_E_MODIFIER: enum_utf8proc_boundclass_t = 16.toUInt
    final val UTF8PROC_BOUNDCLASS_GLUE_AFTER_ZWJ: enum_utf8proc_boundclass_t = 17.toUInt
    final val UTF8PROC_BOUNDCLASS_E_BASE_GAZ: enum_utf8proc_boundclass_t = 18.toUInt
  }

  type int8_t = CSignedChar
  type int16_t = CShort
  type int32_t = CInt
  type uint8_t = CUnsignedChar
  type uint16_t = CUnsignedShort
  type uint32_t = CUnsignedInt
  type utf8proc_int8_t = int8_t
  type utf8proc_uint8_t = uint8_t
  type utf8proc_int16_t = int16_t
  type utf8proc_uint16_t = uint16_t
  type utf8proc_int32_t = int32_t
  type utf8proc_uint32_t = uint32_t
  type utf8proc_size_t = CSize
  type utf8proc_ssize_t = CPtrDiff
  type utf8proc_bool = CBool
  type utf8proc_option_t = enum_utf8proc_option_t
  type utf8proc_propval_t = utf8proc_int16_t
  type struct_utf8proc_property_struct = CArray[Byte, Digit2[_2, _4]]
  type utf8proc_property_t = struct_utf8proc_property_struct
  type utf8proc_category_t = enum_utf8proc_category_t
  type utf8proc_bidi_class_t = enum_utf8proc_bidi_class_t
  type utf8proc_decomp_type_t = enum_utf8proc_decomp_type_t
  type utf8proc_boundclass_t = enum_utf8proc_boundclass_t
  type utf8proc_custom_func = CFuncPtr2[utf8proc_int32_t, Ptr[Byte], utf8proc_int32_t]
  val utf8proc_utf8class: CArray[utf8proc_int8_t, Digit3[_2, _5, _6]] = extern
  def utf8proc_version(): CString = extern
  def utf8proc_errmsg(errcode: utf8proc_ssize_t): CString = extern
  def utf8proc_iterate(str: Ptr[utf8proc_uint8_t], strlen: utf8proc_ssize_t, codepoint_ref: Ptr[utf8proc_int32_t]): utf8proc_ssize_t = extern
  def utf8proc_codepoint_valid(codepoint: utf8proc_int32_t): utf8proc_bool = extern
  def utf8proc_encode_char(codepoint: utf8proc_int32_t, dst: Ptr[utf8proc_uint8_t]): utf8proc_ssize_t = extern
  def utf8proc_get_property(codepoint: utf8proc_int32_t): Ptr[utf8proc_property_t] = extern
  def utf8proc_decompose_char(codepoint: utf8proc_int32_t, dst: Ptr[utf8proc_int32_t], bufsize: utf8proc_ssize_t, options: utf8proc_option_t, last_boundclass: Ptr[CInt]): utf8proc_ssize_t = extern
  def utf8proc_decompose(str: Ptr[utf8proc_uint8_t], strlen: utf8proc_ssize_t, buffer: Ptr[utf8proc_int32_t], bufsize: utf8proc_ssize_t, options: utf8proc_option_t): utf8proc_ssize_t = extern
  def utf8proc_decompose_custom(str: Ptr[utf8proc_uint8_t], strlen: utf8proc_ssize_t, buffer: Ptr[utf8proc_int32_t], bufsize: utf8proc_ssize_t, options: utf8proc_option_t, custom_func: CFuncPtr2[utf8proc_int32_t, Ptr[Byte], utf8proc_int32_t], custom_data: Ptr[Byte]): utf8proc_ssize_t = extern
  def utf8proc_normalize_utf32(buffer: Ptr[utf8proc_int32_t], length: utf8proc_ssize_t, options: utf8proc_option_t): utf8proc_ssize_t = extern
  def utf8proc_reencode(buffer: Ptr[utf8proc_int32_t], length: utf8proc_ssize_t, options: utf8proc_option_t): utf8proc_ssize_t = extern
  def utf8proc_grapheme_break_stateful(codepoint1: utf8proc_int32_t, codepoint2: utf8proc_int32_t, state: Ptr[utf8proc_int32_t]): utf8proc_bool = extern
  def utf8proc_grapheme_break(codepoint1: utf8proc_int32_t, codepoint2: utf8proc_int32_t): utf8proc_bool = extern
  def utf8proc_tolower(c: utf8proc_int32_t): utf8proc_int32_t = extern
  def utf8proc_toupper(c: utf8proc_int32_t): utf8proc_int32_t = extern
  def utf8proc_totitle(c: utf8proc_int32_t): utf8proc_int32_t = extern
  def utf8proc_charwidth(codepoint: utf8proc_int32_t): CInt = extern
  def utf8proc_category(codepoint: utf8proc_int32_t): utf8proc_category_t = extern
  def utf8proc_category_string(codepoint: utf8proc_int32_t): CString = extern
  def utf8proc_map(str: Ptr[utf8proc_uint8_t], strlen: utf8proc_ssize_t, dstptr: Ptr[Ptr[utf8proc_uint8_t]], options: utf8proc_option_t): utf8proc_ssize_t = extern
  def utf8proc_map_custom(str: Ptr[utf8proc_uint8_t], strlen: utf8proc_ssize_t, dstptr: Ptr[Ptr[utf8proc_uint8_t]], options: utf8proc_option_t, custom_func: CFuncPtr2[utf8proc_int32_t, Ptr[Byte], utf8proc_int32_t], custom_data: Ptr[Byte]): utf8proc_ssize_t = extern
  def utf8proc_NFD(str: Ptr[utf8proc_uint8_t]): Ptr[utf8proc_uint8_t] = extern
  def utf8proc_NFC(str: Ptr[utf8proc_uint8_t]): Ptr[utf8proc_uint8_t] = extern
  def utf8proc_NFKD(str: Ptr[utf8proc_uint8_t]): Ptr[utf8proc_uint8_t] = extern
  def utf8proc_NFKC(str: Ptr[utf8proc_uint8_t]): Ptr[utf8proc_uint8_t] = extern

  object defines {
    val UTF8PROC_VERSION_MAJOR: CInt = 2
    val UTF8PROC_VERSION_MINOR: CInt = 1
    val UTF8PROC_VERSION_PATCH: CInt = 0
    val UTF8PROC_ERROR_NOMEM: CInt = -1
    val UTF8PROC_ERROR_OVERFLOW: CInt = -2
    val UTF8PROC_ERROR_INVALIDUTF8: CInt = -3
    val UTF8PROC_ERROR_NOTASSIGNED: CInt = -4
    val UTF8PROC_ERROR_INVALIDOPTS: CInt = -5
  }
}
