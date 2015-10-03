package org.powlab.jeye.tests

import org.powlab.jeye.tests.create._
import org.powlab.jeye.decompile._

package create {

  // TODO FAIL: не проработан механизм с generic и типами
  @org.junit.Ignore
  class CreateTest1Test extends DecompileTestClass(classOf[CreateTest1]) {}

  class CreateTest2Test extends DecompileTestClass(classOf[CreateTest2]) {}

  class CreateTest3Test extends DecompileTestClass(classOf[CreateTest3]) {}
}