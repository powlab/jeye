package org.powlab.jeye.core.parsing

import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.Types._

import scala.collection.mutable.ArrayBuffer

object DescriptorParser {

  val Pisc = (descriptor: String) => {
    val commonlyUsedDescriptor = getCommonlyUsed(descriptor)
    if (commonlyUsedDescriptor.isDefined) {
      commonlyUsedDescriptor.get
    } else {
      parseFieldDescriptor(descriptor)
    }
  }

  def parseFieldDescriptor(descriptor: String): ParameterDescriptor = {
    val symbol = descriptor.charAt(0);
    symbol match {
      case value if isBaseType(value) => parsePrimitive(descriptor)
      case value if isVoidType(value) => parsePrimitive(descriptor)
      case value if isReferenceType(value) => parseReference(descriptor)
      case value if isArrayType(value) => parseArray(descriptor)
      case _ => throw new IllegalStateException("Not a field descriptor: " + descriptor)
    }
  }

  def parseMethodDescriptor(descriptor: String): MethodDescriptor = {
    var openIndex = descriptor.indexOf('(') + 1
    val endIndex = descriptor.indexOf(')', openIndex);
    val parameters = ArrayBuffer[ParameterDescriptor]();
    while (openIndex < endIndex) {
      val parameter = parseFieldDescriptor(descriptor.substring(openIndex))
      parameters += parameter
      openIndex += parameter.meta.length
    }
    val returnDescriptor = parseFieldDescriptor(descriptor.substring(endIndex + 1));
    return new MethodDescriptor(parameters.toArray, returnDescriptor)
  }

  private def parsePrimitive(descriptor: String): ParameterDescriptor = {
    getCommonlyUsed(descriptor.charAt(0))
  }

  private def parseReference(descriptor: String): ParameterDescriptor = {
    val end = descriptor.indexOf(';')
    val meta = descriptor.substring(0, end + 1)
    val pdOtp = getCommonlyUsed(meta)
    if (pdOtp.isDefined) {
      pdOtp.get
    } else {
      new ParameterDescriptor(TYPE_REFERENCE, meta)
    }
  }

  /**
   * TODO here: добавить часто используемые массивы в getCommonlyUsed
   */
  private def parseArray(descriptor: String): ParameterDescriptor = {
    val dimension = descriptor.indexWhere(symbol => (symbol != '['))
    val componentDescriptor = parseFieldDescriptor(descriptor.substring(dimension));
    val meta = descriptor.substring(0, dimension + componentDescriptor.meta.length());
    val pdOtp = getCommonlyUsed(meta)
    if (pdOtp.isDefined) {
      pdOtp.get
    } else {
      new ArrayDescriptor(componentDescriptor, dimension, meta)
    }
  }

}
