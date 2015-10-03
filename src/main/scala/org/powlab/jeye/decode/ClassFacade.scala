package org.powlab.jeye.decode

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.parsing.DescriptorParser
import org.powlab.jeye.decode._
import org.powlab.jeye.utils.ClassInformator
import org.powlab.jeye.decode.LocalVariableStore.LocalVariableStore
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.decoders.ExtraInfo
import org.powlab.jeye.decode.processor.reference.MethodInstructionData

/**
 * Менеджер [ClassFacade]
 */
object ClassFacades {
  private val classFacades = Map[String, ClassFacade]()

  /**
   * TODO here: возможно нужно добавить блок синхронизации (когда перейдем на многопоточность)
   */
  def registry(classMeta: String, classFacade: ClassFacade) {
    classFacades.put(classMeta, classFacade)
  }

  def registry(classFile: ClassFile, inner:Boolean): ClassFacade = {
    val classFacade = ClassFacade(classFile, inner)
    val cpUtils = classFile.constantPoolUtils;
    val classMeta = cpUtils.thisClass.meta
    registry(classMeta, classFacade)
    classFacade
  }

  def get(classMeta: String): Option[ClassFacade] = classFacades.get(classMeta)

  def get(classFile: ClassFile): ClassFacade = {
    val cpUtils = classFile.constantPoolUtils;
    val classMeta = cpUtils.thisClass.meta
    classFacades(classMeta)
  }
}

/**
 * Фасад класса.
 * Предназначен для хранения описаний методов и полей
 *
 * TODO here: можно использовать как прослойку для кэширования данных
 *
 * TODO here: для корректной работы нужно построить отношение наследования
 * для rt.jar и декомпилируемому jar: суть в том, чтобы понимать как связаны
 * классы, например, java.lang.Integer и java.lang.Number ? Соответственно,
 * когда идет боксинг 'int' типа, то вызов метода с сигнатурой Number, считается
 * допустимым.
 * Также важна информация о varargs и других характеристиках вызываемых методов
 * Скорее всего, будет не сколько реализаций ClassFacade:
 * - на основе текстовой информации (архив, например, информация о rt.jar)
 * - на основе байт-кода (для текущего jar и указанных в списке при декомпиляции)
 */
object ClassFacade {

  private val EMPTY_MIDS = new ArrayBuffer[MethodInstructionData]()

  def methodKey(member: MemberInfo): String = {
    member.name_index + "-" + member.descriptor_index
  }

  def getMids(value: Any): ArrayBuffer[MethodInstructionData] = {
    if (value.isInstanceOf[ArrayBuffer[MethodInstructionData]]) {
      value.asInstanceOf[ArrayBuffer[MethodInstructionData]]
    } else {
      EMPTY_MIDS
    }
  }

  def apply(classFile: ClassFile, inner:Boolean): ClassFacade = {
    val cpUtils = classFile.constantPoolUtils;
    /** Мэпинг имен на MethodDescriptor или список MethodDescriptor*/
    val name2methods = Map[String, Any]()
    val key2MethodData = Map[String, MethodInstructionData]()
    var signatureVariants: Boolean = false
    classFile.methods.foreach(method => {
      val mName = cpUtils.getUtf8(method.name_index)
      val mDescriptorView = cpUtils.getUtf8(method.descriptor_index)
      val mDescriptor = DescriptorParser.parseMethodDescriptor(mDescriptorView)
      val mKey = methodKey(method)
      val mid = new MethodInstructionData(mName, method.access_flags, mDescriptor, cpUtils.thisClass)
      key2MethodData(mKey) = mid

      val value = name2methods.getOrElse(mName, null)
      if (value == null) {
        name2methods(mName) = mid
      } else if (value.isInstanceOf[MethodInstructionData]) {
        val list = new ArrayBuffer[MethodInstructionData]
        list += value.asInstanceOf[MethodInstructionData]
        list += mid
        name2methods(mName) = list
      } else {
        value.asInstanceOf[ArrayBuffer[MethodInstructionData]] += mid
      }
    })
    new ClassFacade(inner, name2methods, key2MethodData)
  }
}

import org.powlab.jeye.decode.ClassFacade._

class ClassFacade(inner:Boolean, name2methods: Map[String, Any], key2MethodData: Map[String, MethodInstructionData]) {

  val signatureVariants: Boolean = name2methods.filter(pair => getMids(pair._2).nonEmpty).find(entry => {
    (getMids(entry._2)).find(mid => {
      hasSameSignature(entry._1, mid.descriptor)
    }).isDefined
  }).isDefined

  def getMethodDescriptor(key: String): MethodDescriptor = key2MethodData(key).descriptor

  def getMid(mName: String, mDescriptor: MethodDescriptor): MethodInstructionData = {
    val valueOpt = name2methods.get(mName)
    if (valueOpt.isDefined) {
      val obj = valueOpt.get
      var mid: MethodInstructionData = null
      if (obj.isInstanceOf[MethodInstructionData]) {
        val mid = obj.asInstanceOf[MethodInstructionData]
        if (MethodDescriptor.same(mid.descriptor, mDescriptor)) {
          return mid
        }
      } else {
        val midOpt = getMids(obj).find(mid => MethodDescriptor.same(mid.descriptor, mDescriptor))
        if (midOpt.isDefined) {
          return midOpt.get
        }
      }
    }
    null
  }

  /**
   * Проверяет, что класс содержит методы с одинаковыми сигнатурами и перекрытыми типами
   */
  def hasSignatureVariants = signatureVariants

  /**
   * Проверить, имеется ли аналогичный метод по сигнатуре?
   */
  def hasSameSignature(mName: String, mDescriptor: MethodDescriptor): Boolean = {
    val value = name2methods.getOrElse(mName, "")
    val mids = getMids(value)
    mids.count(mid => {
      val descriptor = mid.descriptor
      descriptor.parameters.length == mDescriptor.parameters.length &&
        isSimilar(descriptor.returnType, mDescriptor.returnType) &&
        (descriptor.parameters.isEmpty ||
          descriptor.parameters.zip(mDescriptor.parameters).count(pair => isSimilar(pair._1, pair._2)) == descriptor.parameters.length)
    }) > 1
  }

  def getParamVariants(mName: String, index: Int, mDescriptor: MethodDescriptor): ArrayBuffer[ParameterDescriptor] = {
    val value = name2methods.getOrElse(mName, "")
    val mids = getMids(value)
    val params = ArrayBuffer[ParameterDescriptor]()
    mids.foreach(mid => {
      val descriptor = mid.descriptor
      if (descriptor.parameters.length == mDescriptor.parameters.length &&
        isSimilar(descriptor.returnType, mDescriptor.returnType) &&
        (descriptor.parameters.isEmpty ||
          descriptor.parameters.zip(mDescriptor.parameters).count(pair => isSimilar(pair._1, pair._2)) == descriptor.parameters.length)) {
        params += descriptor.parameters(index)
      }
    })
    params
  }

  def isPrimirive(param1: ParameterDescriptor, param2: ParameterDescriptor): Boolean = {
    descOrder(param1) < MAX_ORDER && descOrder(param2) < MAX_ORDER
  }

  private def isSimilar(param1: ParameterDescriptor, param2: ParameterDescriptor): Boolean = {
    if (param1.isArray && param2.isArray) {
      val arrParam1 = param1.asInstanceOf[ArrayDescriptor]
      val arrParam2 = param2.asInstanceOf[ArrayDescriptor]
      arrParam1.dimension == arrParam2.dimension && arrParam1.lowType == arrParam2.lowType
    } else if (!param1.isArray && !param2.isArray) {
      param1.baseType == param2.baseType || isPrimirive(param1, param2) ||
        isAutoboxing(param1, param2) || isRelevantAutoboxing(param1, param2)
    } else {
      false
    }
  }

  private def isAutoboxing(param1: ParameterDescriptor, param2: ParameterDescriptor): Boolean = {
    isAutoboxing(param1, param2, TYPE_BOOLEAN_DESCRIPTOR, TYPE_REF_BOOLEAN_DESCRIPTOR) ||
      isAutoboxing(param1, param2, TYPE_CHAR_DESCRIPTOR, TYPE_REF_CHARACTER_DESCRIPTOR) ||
      isAutoboxing(param1, param2, TYPE_BYTE_DESCRIPTOR, TYPE_REF_BYTE_DESCRIPTOR) ||
      isAutoboxing(param1, param2, TYPE_SHORT_DESCRIPTOR, TYPE_REF_SHORT_DESCRIPTOR) ||
      isAutoboxing(param1, param2, TYPE_INT_DESCRIPTOR, TYPE_REF_INTEGER_DESCRIPTOR) ||
      isAutoboxing(param1, param2, TYPE_FLOAT_DESCRIPTOR, TYPE_REF_FLOAT_DESCRIPTOR) ||
      isAutoboxing(param1, param2, TYPE_DOUBLE_DESCRIPTOR, TYPE_REF_DOUBLE_DESCRIPTOR)
  }

  private def isRelevantAutoboxing(param1: ParameterDescriptor, param2: ParameterDescriptor): Boolean = {
    (descOrder(param1) < descOrder(param2) || descOrder(param2) < descOrder(param2) || param2 == param2) &&
      !param1.isArray && !param2.isArray
  }

  private def isAutoboxing(param1: ParameterDescriptor, param2: ParameterDescriptor, primitiveParam: ParameterDescriptor, refParam: ParameterDescriptor): Boolean = {
    (param1 == primitiveParam && param2 == refParam) || (param1 == refParam && param2 == primitiveParam)
  }

}