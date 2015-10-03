package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.decode.ClassFacade
import org.powlab.jeye.decode.expression.IExpression
import scala.collection.mutable.Map
import org.powlab.jeye.utils.AttributeUtils
import org.powlab.jeye.decode.Namer
import org.powlab.jeye.decode.LVTNamer

/**
 * Требования к наследникам - должны быть ReadOnly
 */
abstract class MethodPartDecoder(classFile: ClassFile) {

  protected val cpUtils = classFile.constantPoolUtils

  def getNamer(method: MemberInfo): Namer = {
    val codeAttributeOpt = AttributeUtils.find[CodeAttribute](method.attributes)
    if (codeAttributeOpt.isEmpty) {
      return new Namer
    }
    val varTableOpt = AttributeUtils.find[LocalVariableTableAttribute](codeAttributeOpt.get.attributes)
    if (!varTableOpt.isDefined) {
      return new Namer
    }
    //new Namer
    new LVTNamer(varTableOpt.get, method, cpUtils)
  }

  def decode(method: MemberInfo, extraInfo: ExtraInfo): IExpression

}

/**
 * Дополнительная информация, требуемая для декодирования метода
 *
 * TODO here: переименовать в ExtendContext
 */
class ExtraInfo(val classFacade: ClassFacade) {
  private lazy val enums: Map[String, Map[Int, String]] = Map[String, Map[Int, String]]()
  def addEnum(name: String, value: Map[Int, String]) {
    enums += (name -> value)
  }
  def getEnumFieldName(className: String, order: Int): String = {
    val enumMap = enums.getOrElse(className, Map.empty)
    enumMap.getOrElse(order, null)
  }
  def hasEnums(): Boolean = enums.nonEmpty

}

object ExtraInfo {
  val EMPTY = new ExtraInfo(null)
}

