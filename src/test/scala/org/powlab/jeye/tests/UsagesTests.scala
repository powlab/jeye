package org.powlab.jeye.tests

import org.powlab.jeye.tests.usages._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package usages {

  class UsageTest1Test extends DecompileTestClass(classOf[UsageTest1], false, ArrayBuffer(("s=", "return"), ("returns;", ""))) {}

  class UsageTest2Test extends DecompileTestClass(classOf[UsageTest2], true) {}

  class UsageTest3Test extends DecompileTestClass(classOf[UsageTest3], true) {}

  class UsageTest4Test extends DecompileTestClass(classOf[UsageTest4], true) {}
}