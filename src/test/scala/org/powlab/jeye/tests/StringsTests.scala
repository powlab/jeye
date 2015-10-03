package org.powlab.jeye.tests

import org.powlab.jeye.tests.strings._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package strings {

  // TODO OK: сработал оптимизатор, stringbuilder заменился на +
  @org.junit.Ignore
  class StringBufferTest1Test extends DecompileTestClass(classOf[StringBufferTest1]) {}

  class StringBufferTest1aTest extends DecompileTestClass(classOf[StringBufferTest1a]) {}

  class StringBuilderTest1Test extends DecompileTestClass(classOf[StringBuilderTest1]) {}

  class StringBuilderTest2Test extends DecompileTestClass(classOf[StringBuilderTest2], true) {}

  // TODO OK: скобки не нужны, особенности теста.
  @org.junit.Ignore
  class StringBuilderTest3Test extends DecompileTestClass(classOf[StringBuilderTest3]) {}

  class StringTest1Test extends DecompileTestClass(classOf[StringTest1], false, ArrayBuffer(("0xcc9e2d51", "-862048943"), ("0x1b873593", "461845907"), ("0xe6546b64", "-430675100"), ("0x85ebca6b", "-2048144789"), ("0xc2b2ae35", "-1028477387"))) {}

  class StringTest2Test extends DecompileTestClass(classOf[StringTest2], true) {}
}