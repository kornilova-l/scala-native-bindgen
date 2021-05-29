package org.scalanative.bindings

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@extern
object iconv {
  type iconv_t = Ptr[Byte]
  def iconv_open(__tocode: CString, __fromcode: CString): Ptr[Byte] = extern
  def iconv(__cd: Ptr[Byte], __inbuf: Ptr[CString], __inbytesleft: Ptr[CSize], __outbuf: Ptr[CString], __outbytesleft: Ptr[CSize]): CSize = extern
  def iconv_close(__cd: Ptr[Byte]): CInt = extern

  object defines {
    val _ICONV_H: CInt = 1
  }
}
