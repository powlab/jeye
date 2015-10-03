package org.powlab.jeye.tests

import org.powlab.jeye.tests.constant._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package constant {

  //TODO OK: компилятор съдает байткод (нужно делать вызов компилированного класса после декомпиляции)
  class ConstantFolding1Test extends DecompileTestClass(classOf[ConstantFolding1], false, ArrayBuffer(("this\"+\"is\"+\"literal\"+\"concatenation.", "thisisliteralconcatenation."))) {}

  //TODO OK: компилятор съдает байткод (нужно делать вызов компилированного класса после декомпиляции)
  class ConstantFolding2Test extends DecompileTestClass(classOf[ConstantFolding2], true, ArrayBuffer(("1+2+3+4+5+6;", "21;"), ("1+2+3+4+5", "15"))) {}

  //TODO OK: компилятор съдает байткод (нужно делать вызов компилированного класса после декомпиляции)
  @org.junit.Ignore
  class ConstantFolding3Test extends DecompileTestClass(classOf[ConstantFolding3]) {}

  class ConstantFolding4Test extends DecompileTestClass(classOf[ConstantFolding4], true) {}

}