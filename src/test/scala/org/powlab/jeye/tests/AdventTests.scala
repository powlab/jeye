package org.powlab.jeye.tests

import org.powlab.jeye.tests.advent._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package advent {

  class Eg1Test extends DecompileTestClass(classOf[Eg1]) {}

  class Eg2Test extends DecompileTestClass(classOf[Eg2], false, ArrayBuffer(("intint3=", "return"), ("returnint3;", ""))) {}

  class Eg3Test extends DecompileTestClass(classOf[Eg3]) {}

  class Eg4Test extends DecompileTestClass(classOf[Eg4], true) {}

}