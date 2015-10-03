package org.powlab.jeye.tests

import org.powlab.jeye.tests.syncs._
import org.powlab.jeye.decompile._

package syncs {

  class SyncTest1Test extends DecompileTestClass(classOf[SyncTest1]) {}

  class SyncTest10Test extends DecompileTestClass(classOf[SyncTest10], true) {}

  class SyncTest11Test extends DecompileTestClass(classOf[SyncTest11], true) {}

  class SyncTest12Test extends DecompileTestClass(classOf[SyncTest12], true) {}

  class SyncTest1aTest extends DecompileTestClass(classOf[SyncTest1a]) {}

  class SyncTest2Test extends DecompileTestClass(classOf[SyncTest2]) {}

  class SyncTest2aTest extends DecompileTestClass(classOf[SyncTest2a]) {}

  class SyncTest3Test extends DecompileTestClass(classOf[SyncTest3]) {}

  class SyncTest3aTest extends DecompileTestClass(classOf[SyncTest3a]) {}

  // TODO OK: компилятор оптимизирует байткод
  @org.junit.Ignore
  class SyncTest3bTest extends DecompileTestClass(classOf[SyncTest3b]) {}

  // TODO OK: компилятор оптимизирует байткод
  @org.junit.Ignore
  class SyncTest3cTest extends DecompileTestClass(classOf[SyncTest3c]) {}

  class SyncTest4Test extends DecompileTestClass(classOf[SyncTest4]) {}

  class SyncTest4aTest extends DecompileTestClass(classOf[SyncTest4a]) {}

  class SyncTest5Test extends DecompileTestClass(classOf[SyncTest5]) {}

  class SyncTest6Test extends DecompileTestClass(classOf[SyncTest6]) {}

  class SyncTest7Test extends DecompileTestClass(classOf[SyncTest7]) {}

  class SyncTest8Test extends DecompileTestClass(classOf[SyncTest8]) {}

  class SyncTest8bTest extends DecompileTestClass(classOf[SyncTest8b]) {}

  // TODO FAIL: этот тест на try/catch
  @org.junit.Ignore
  class SyncTest8cTest extends DecompileTestClass(classOf[SyncTest8c]) {}

  class SyncTest9Test extends DecompileTestClass(classOf[SyncTest9]) {}
}