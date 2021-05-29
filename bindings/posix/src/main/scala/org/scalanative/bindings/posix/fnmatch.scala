package org.scalanative.bindings.posix

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@extern
object fnmatch {
  def fnmatch(__pattern: CString, __name: CString, __flags: CInt): CInt = extern

  object defines {
    val _FNMATCH_H: CInt = 1
    val FNM_NOMATCH: CInt = 1
  }
}
