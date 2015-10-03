package org.powlab.jeye.tests

import org.powlab.jeye.tests.shortcirc._
import org.powlab.jeye.decompile._

package shortcirc {

  class ShortCircuitAssignTest1Test extends DecompileTestClass(classOf[ShortCircuitAssignTest1], true) {}

  class ShortCircuitAssignTest1aTest extends DecompileTestClass(classOf[ShortCircuitAssignTest1a], true) {}

  class ShortCircuitAssignTest2Test extends DecompileTestClass(classOf[ShortCircuitAssignTest2], true) {}

  class ShortCircuitAssignTest3Test extends DecompileTestClass(classOf[ShortCircuitAssignTest3], true) {}

  class ShortCircuitAssignTest4Test extends DecompileTestClass(classOf[ShortCircuitAssignTest4], true) {}

  class ShortCircuitAssignTest4aTest extends DecompileTestClass(classOf[ShortCircuitAssignTest4a], true) {}

  // TODO FAIL: проблемы в определении if патернов
  @org.junit.Ignore
  class ShortCircuitAssignTest4bTest extends DecompileTestClass(classOf[ShortCircuitAssignTest4b], true) {}

  class ShortCircuitAssignTest4cTest extends DecompileTestClass(classOf[ShortCircuitAssignTest4c], true) {}

  // TODO FAIL: проблемы в определении if патернов
  @org.junit.Ignore
  class ShortCircuitAssignTest4dTest extends DecompileTestClass(classOf[ShortCircuitAssignTest4d], true) {}

  // TODO FAIL: проблемы в определении if патернов
  @org.junit.Ignore
  class ShortCircuitAssignTest4eTest extends DecompileTestClass(classOf[ShortCircuitAssignTest4e], true) {}

  // TODO FAIL: проблемы в определении if патернов
  @org.junit.Ignore
  class ShortCircuitAssignTest4fTest extends DecompileTestClass(classOf[ShortCircuitAssignTest4f], true) {}

  class ShortCircuitAssignTest5Test extends DecompileTestClass(classOf[ShortCircuitAssignTest5], true) {}

  class ShortCircuitAssignTest6Test extends DecompileTestClass(classOf[ShortCircuitAssignTest6], true) {}

  // TODO FAIL: проблемы в определении if патернов при отрицании
  @org.junit.Ignore
  class ShortCircuitAssignTest7Test extends DecompileTestClass(classOf[ShortCircuitAssignTest7], true) {}
}