package org.powlab.jeye.decompile

import org.powlab.jeye.tests._
import org.powlab.jeye.tests.primitives._
import org.powlab.jeye.tests.advent.{ Eg1, Eg2, Eg3, Eg4 }

import scala.collection.mutable.ArrayBuffer

package test {

  import org.powlab.jeye.scenario.new_scenario.{ FieldsSignatures, MethodsSignatures }

  class MethodsSignaturesTest extends DecompileTestClass(classOf[MethodsSignatures[_]]) {}

  class FieldsSignaturesTest extends DecompileTestClass(classOf[FieldsSignatures[_, _, _, _, _, _]]) {}

  class IntTest1Test extends DecompileTestClass(classOf[IntTest1]) {}

  class IntTest2Test extends DecompileTestClass(classOf[IntTest2]) {}

}