package org.powlab.jeye.tests

import org.powlab.jeye.tests.call._
import org.powlab.jeye.decompile._

package call {

  //TODO FAIL: проблемы с определением типа
  @org.junit.Ignore
  class CallTest1Test extends DecompileTestClass(classOf[CallTest1]) {}

  class CalTest01Test extends DecompileTestClass(classOf[CalTest01], true) {}

}