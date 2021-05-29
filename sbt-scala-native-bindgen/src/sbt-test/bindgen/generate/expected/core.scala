package org.example.app.core

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("core")
@extern
object core {
  def count_words(text: CString): CInt = extern
  def __not_excluded(): Unit = extern
}
