package org.powlab.jeye.tests

import org.powlab.jeye.tests.bound._
import org.powlab.jeye.decompile._

package bound {

  //TODO OK: добавить/вернуть Override аннотацию
  class BoundTestImplTest extends DecompileTestClass(classOf[BoundTestImpl[_]]) {}

  class BoundTestPart1Test extends DecompileTestClass(classOf[BoundTestPart1[_, _]]) {}

  class BoundTestPart2Test extends DecompileTestClass(classOf[BoundTestPart2[_, _]]) {}

  class BoundTestPart3Test extends DecompileTestClass(classOf[BoundTestPart3[_]]) {}

  //TODO FAILL: пока не умеем работать с дженериками локальных переменных.
  @org.junit.Ignore
  class BoundTest0Test extends DecompileTestClass(classOf[BoundTest0]) {}

}