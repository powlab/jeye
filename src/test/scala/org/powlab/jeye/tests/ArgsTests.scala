package org.powlab.jeye.tests

import org.powlab.jeye.tests.args._
import org.powlab.jeye.decompile._

package args {

  //TODO OK: Декодируется корректно, но не аналогично исходному коду, нужно поработать с типами
  @org.junit.Ignore
  class ArgClassifyTest1Test extends DecompileTestClass(classOf[ArgClassifyTest1], true) {}

  class ArgClassifyTest2Test extends DecompileTestClass(classOf[ArgClassifyTest2]) {}

  class ArgTest1Test extends DecompileTestClass(classOf[ArgTest1]) {}

  class ArgTest2Test extends DecompileTestClass(classOf[ArgTest2], true) {}

  class ArgTest3Test extends DecompileTestClass(classOf[ArgTest3], true) {}

}