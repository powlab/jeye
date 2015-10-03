package org.powlab.jeye.decompile

import java.io.File
import org.junit.{Assert, Test}
import org.powlab.jeye.decode.Formater
import org.powlab.jeye.decode.decoders.Decoder
import org.powlab.jeye.utils.DecodeUtils.getPathClassName
import org.powlab.jeye.utils.{DecompileIterator, Package, PackageTree}
import scala.io.Source
import scala.io.Source._
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.expression.Expressions

class DecompileTestClass(cl: Class[_], ignoreImport: Boolean = false, replacers: ArrayBuffer[(String, String)] = ArrayBuffer.empty) {
  private final val srcPath = "../../src/test/java/"

  @Test
  def decompileTest() {
    val compileDir = new File(getClass.getProtectionDomain.getCodeSource.getLocation.getFile)
    val root = Package(DecompileIterator.from(compileDir))
    val sourceDir = new File(compileDir, srcPath)
    val srcFile = cl.getCanonicalName
    val className = getPathClassName(srcFile)
    val javaSource = fromFile(new File(sourceDir, getPathClassName(srcFile) + ".java"))
    val decodedSource = PackageTree.findClassByFullName(root, srcFile) match {
      case Some(clazz) =>
        val classExpr = Decoder.decode(clazz)
        fromString(Formater.format(classExpr, Expressions.EMPTY_EXPRESSION))
      case None => throw new IllegalStateException("Clazz " + className + " not found")
    }
    var expectedResult = preparedSource(javaSource, ignoreImport)
    replacers.foreach(replacer => {
      expectedResult = expectedResult.replace(replacer._1, replacer._2)
    })
    val decodedResult = preparedSource(decodedSource, ignoreImport)
    Assert.assertEquals(expectedResult, decodedResult)
  }

  private def preparedSource(source: Source, ignoreImport: Boolean): String = {
    val string = (source.getLines().map(line => {
      var start = line.indexOf("//")
      if (ignoreImport && line.startsWith("import ")) {
        start = 0
      }
      if (start != -1) line.substring(0, start) else line
    }).mkString("\n"))
    println(string)
    string.replaceAll("@Override", "").replaceAll("\\s+", "")
  }
}
