package org.example.app.stdlib

import scala.scalanative.unsigned._
import scala.scalanative.native._

@extern
object stdlib {
  def access(path: CString, mode: CInt): CInt = extern
  def read(fildes: CInt, buf: Ptr[Byte], nbyte: CInt): CInt = extern
  //def printf(format: CString, varArgs: CVararg*): CInt = extern
  def printf(format: CString, args: CVarArg*): CInt = Zone {
    implicit z => stdio.vprintf(format, toCVarArgList(args.toSeq))
  }
}
