package org.scalanative.bindings.tests

import org.scalatest.funspec.AnyFunSpec

class IconvSpec extends AnyFunSpec {
  describe("iconv") {
    it("should convert back and forth between UTF-8 and ISO-8859-1") {
      //#usage-example
      import org.scalanative.bindings.iconv._
      import scala.scalanative.unsafe._
      import scala.scalanative.unsigned._
      import scala.scalanative.libc._
      import java.nio.charset.Charset

      val UTF8   = Charset.forName("UTF-8")
      val encode = iconv_open(c"UTF-8", c"ISO-8859-1")
      val decode = iconv_open(c"ISO-8859-1", c"UTF-8")

      Zone { implicit zone =>
        val originalBuf    = toCString("øre", UTF8) // Ear in Danish
        val originalBufPtr = alloc[CString]
        !originalBufPtr = originalBuf
        val originalBytesLeft = alloc[CSize]
        !originalBytesLeft = string.strlen(originalBuf)
        //#usage-example
        assert(!originalBytesLeft == 4)
        //#usage-example

        val translatedBuf    = alloc[Byte](32)
        val translatedBufPtr = alloc[CString]
        !translatedBufPtr = translatedBuf
        val translatedBytesLeft = alloc[CSize]
        !translatedBytesLeft = 32.toULong

        val translatedCode = iconv(
          encode,
          originalBufPtr,
          originalBytesLeft,
          translatedBufPtr,
          translatedBytesLeft
        )
        //#usage-example

        assert(translatedCode == 0)

        !translatedBufPtr = translatedBuf
        !translatedBytesLeft = string.strlen(translatedBuf)

        val roundtripBuf    = alloc[Byte](32)
        val roundtripBufPtr = alloc[CString]
        !roundtripBufPtr = roundtripBuf
        val roundtripBytesLeft = alloc[CSize]
        !roundtripBytesLeft = 32.toULong

        val roundtripCode = iconv(
          decode,
          translatedBufPtr,
          translatedBytesLeft,
          roundtripBufPtr,
          roundtripBytesLeft
        )

        assert(string.strcmp(originalBuf, roundtripBuf) == 0)
      //#usage-example
      }
      //#usage-example

      //#usage-example
      iconv_close(encode)
      //#usage-example
      iconv_close(decode)
    }
  }
}
