package org.powlab.jeye.decode.processor.stack

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.powlab.jeye.core._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.decode.expression._;
import org.powlab.jeye.core.Descriptors._

class StackInformatorTest {

  @Test
  def testIsCategory1 {
    assertFalse(StackInformator.isCategory1(null))
    getRegistryTypes.filter(typeInfo => {
      typeInfo._2 != TYPE_DOUBLE && typeInfo._2 != TYPE_LONG
    }).foreach(typeInfo => assertTrue(StackInformator.isCategory1(TypedExpression("", new ParameterDescriptor(typeInfo._2, "")))))
  }

  @Test
  def testIsCategory2 {
    assertFalse(isCategory2(null))
    getRegistryTypes.filter(typeInfo => {
      typeInfo._2 == TYPE_DOUBLE || typeInfo._2 == TYPE_LONG
    }).foreach(typeInfo => StackInformator.isCategory2(TypedExpression("", new ParameterDescriptor(typeInfo._2, ""))))
  }

}