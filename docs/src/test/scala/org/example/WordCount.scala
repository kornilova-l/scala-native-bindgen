package org.example.wordcount

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("wordcount")
@extern
object WordCount {
  type struct_wordcount = CStruct3[CLong, CLong, CLong]
  //#using-stdio-file
  def wordcount(wordcount: Ptr[struct_wordcount], file: Ptr[scala.scalanative.libc.stdio.FILE]): CInt = extern
  //#using-stdio-file

  object implicits {
    implicit class struct_wordcount_ops(val p: Ptr[struct_wordcount]) extends AnyVal {
      def chars: CLong = p._1
      def chars_=(value: CLong): Unit = p._1 = value
      def lines: CLong = p._2
      def lines_=(value: CLong): Unit = p._2 = value
      def words: CLong = p._3
      def words_=(value: CLong): Unit = p._3 = value
    }
  }

  object struct_wordcount {
    import implicits._
    def apply()(implicit z: Zone): Ptr[struct_wordcount] = alloc[struct_wordcount]
    def apply(chars: CLong, lines: CLong, words: CLong)(implicit z: Zone): Ptr[struct_wordcount] = {
      val ptr = alloc[struct_wordcount]
      ptr.chars = chars
      ptr.lines = lines
      ptr.words = words
      ptr
    }
  }
}
