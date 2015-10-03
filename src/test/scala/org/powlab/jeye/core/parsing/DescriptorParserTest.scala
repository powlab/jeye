package org.powlab.jeye.core.parsing

import org.powlab.jeye.core.Types
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class DescriptorParserTest extends Specification {
  override def is = s2""" ${"Specification for parsing descriptor".title}

    Descriptor can either be: $endp
        1. $aPrimitiveDescriptor
        2. $aMethodDescriptor
      """

  def aPrimitiveDescriptor = s2"""
    Primitive descriptor parsing should
     1. check primitiveDescriptor1 ${primitiveDescriptor().test1} """

  case class primitiveDescriptor() {

    def test1 = s2"""
      First primitive descriptor should
        1. check ${primitiveDescriptor1().t1} """

    case class primitiveDescriptor1() {
      val descriptor = "Ljava/lang/Object;"
      val ds = DescriptorParser.parseFieldDescriptor(descriptor)

      def t1 = {
        Types.TYPE_REFERENCE must_== ds.baseType
      }
    }

  }

  def aMethodDescriptor = s2"""
    Method descriptor parsing should
     1. check methodDescriptor1 ${methodDescriptor().test1} """

  case class methodDescriptor() {

    def test1 = s2"""
      Method descriptor should
        1. check spec example ${methodDescriptor1().t1} """

    case class methodDescriptor1() {
      val descriptor = "(IDLjava/lang/Thread;)Ljava/lang/Object;"
      val ds = DescriptorParser.parseMethodDescriptor(descriptor)

      def t1 = {
        Types.TYPE_INT must_== ds.parameters(0).baseType
        Types.TYPE_DOUBLE must_== ds.parameters(1).baseType
        Types.TYPE_REFERENCE must_== ds.parameters(2).baseType
        Types.TYPE_REFERENCE must_== ds.returnType.baseType
      }
    }

  }

}
