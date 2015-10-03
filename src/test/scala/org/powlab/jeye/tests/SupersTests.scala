package org.powlab.jeye.tests

import org.powlab.jeye.tests.supers._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package supers {

  class SuperBaseTest extends DecompileTestClass(classOf[SuperBase]) {}

  class SuperTest1Test extends DecompileTestClass(classOf[SuperTest1]) {}

  // TODO OK/FAIL: проблема с дженериками не имеет место к тесту
  @org.junit.Ignore
  class SuperTest2Test extends DecompileTestClass(classOf[SuperTest2], true, ArrayBuffer(("others.get(0)", "(SuperTest2)others.get(0)"))) {}

  // TODO OK/FAIL: проблема с дженериками не имеет место к тесту
  @org.junit.Ignore
  class SuperTest3Test extends DecompileTestClass(classOf[SuperTest3], true, ArrayBuffer(("others.get(0)", "(SuperTest3)others.get(0)"))) {}
}