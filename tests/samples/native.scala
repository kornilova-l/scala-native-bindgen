package org.scalanative.bindgen.samples

import scala.scalanative.unsigned._
import scala.scalanative.unsafe._

@link("bindgentests")
@extern
object nativeLib {
  type nativeFunc0 = CInt
  @link("native")
  def nativeFunc0(p0: CInt): Unit = extern
  def nativeFunc(p0: CFloat): Unit = extern
}
