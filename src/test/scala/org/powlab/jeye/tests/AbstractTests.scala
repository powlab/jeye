package org.powlab.jeye.tests

import org.powlab.jeye.tests.abstracts._
import org.powlab.jeye.decompile._

package abstracts {

  class AbstractTestBaseTest extends DecompileTestClass(classOf[AbstractTestBase], true) {}

  class AbstractTest2Test extends DecompileTestClass(classOf[AbstractTest2[_]]) {}

}
