package org.powlab.jeye

import java.io.{DataInputStream, File}

import org.junit.Test
import org.powlab.jeye.core._
import org.powlab.jeye.core.SimpleStream
import org.powlab.jeye.core.parsing.ClassFileFormatParser
import org.powlab.jeye.decode.RuntimeOpcodes._
import org.powlab.jeye.decode.decoders.MethodDecoder
import org.powlab.jeye.utils.DecodeUtils.getPathClassName
import org.powlab.jeye.utils.{AttributeUtils, PrintUtils}

class DecompilerLauncherTest {

  private final val packages: Array[String] = Array("org.powlab.jeye.tests")
  private final val srcPath = "../../src/test/java/"

  @Test
  def decompileTest() = {
    val compileDir = new File(getClass.getProtectionDomain.getCodeSource.getLocation.getFile)
    val sourceDir = new File(compileDir, srcPath)
    packages.foreach(pack => {
      val packageDir = new File(sourceDir, getPathClassName(pack))
      packageDir.listFiles().foreach(sourceFile => {
        if (sourceFile.isFile) {
          val source = scala.io.Source.fromFile(sourceFile).getLines().map(line => {
            val start = line.indexOf("//")
            if (start != -1) line.substring(0, start) else line
          }).mkString
          val compileName = "/" + getPathClassName(pack + "." + sourceFile.getName).replace("/java", ".class")
          val stream = new SimpleStream(new DataInputStream(DecompilerLauncher.getClass.getResourceAsStream(compileName)))
          val parsedClass = ClassFileFormatParser.parse(stream)
          val methods = parsedClass.methods
          val decoder = new MethodDecoder(parsedClass)
          //          methods.foreach(method => printMethodCode(parsedClass, method))
        }
      })
    })
  }

  @Test
  def decompile() = {
    val source = "/org/powlab/jeye/scenario/data/Sample2.class"
    val stream = new SimpleStream(new DataInputStream(DecompilerLauncher.getClass.getResourceAsStream(source)))
    val parsedClass = ClassFileFormatParser.parse(stream)
    val methods = parsedClass.methods
    val decoder = new MethodDecoder(parsedClass)
    methods.foreach(method => printMethodCode(parsedClass, method))
  }

  def printMethodCode(parsedClass: ClassFile, method: MemberInfo) {
    val cpUtils = parsedClass.constantPoolUtils
    println("-- method name: " + cpUtils.getUtf8(method.name_index))
    val codeAttribute = AttributeUtils.get[CodeAttribute](method.attributes)
    val runtimeOpcodes = parseRuntimeOpcodes(codeAttribute.code)
    PrintUtils.printRuntimeOpcodes(runtimeOpcodes, cpUtils, 2)
    println()
  }
}

