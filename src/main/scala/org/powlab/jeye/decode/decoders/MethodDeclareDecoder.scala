package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.AccessFlags._
import org.powlab.jeye.core.Signatures.MethodTypeSignature
import org.powlab.jeye.core.parsing.SignatureParser.parseSignature
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.Namer
import org.powlab.jeye.decode.ClassFacade.methodKey
import org.powlab.jeye.utils.{ DecodeUtils }
import scala.language.postfixOps

import org.powlab.jeye.decode.decoders.MethodDeclareDecoder.INTERFACE_FILTER
import org.powlab.jeye.decode.LocalVariableStore

object MethodDeclareDecoder {
  val INTERFACE_FILTER: FlagAcceptor = flag => flag != ACC_ABSTRACT && flag != ACC_PUBLIC
}
/**
 * TODO here: отрефакторить код, сделать его более читаемым
 */
class MethodDeclareDecoder(classFile: ClassFile) extends MethodPartDecoder(classFile) {

  def decode(method: MemberInfo, extendedContext: ExtraInfo): MethodDeclareExpression = {
    val descriptor = extendedContext.classFacade.getMethodDescriptor(methodKey(method))
    val filter: FlagAcceptor = DecodeUtils.select(isInterface(classFile.access_flags), INTERFACE_FILTER, null)
    val methodFlags = getVisibleFlags(StructureScope.METHOD_SCOPE, method.access_flags, filter)
    // 1. Название метода и метка конструктора
    val methodName = cpUtils.getUtf8(method.name_index)
    var isConstructor: Boolean = false
    var isStaticBlock: Boolean = false
    val methodNameExpr = if (DecodeUtils.isStaticBlock(methodName)) {
      isStaticBlock = true
      EMPTY_EXPRESSION
    } else if (DecodeUtils.isConstructor(methodName)) {
      isConstructor = true
      Sex(cpUtils.thisClass.simpleName)
    } else {
      Sex(methodName)
    }

    // 3. Поиск описания сигнатуры
    val signature: Option[SignatureInfo] = method.attributes.find(_.isInstanceOf[SignatureAttribute]).collect {
      case signature: SignatureAttribute => new SignatureDecoder().decodeMethodSignature(
        parseSignature(cpUtils.getUtf8(signature.signature_index), StructureScope.METHOD_SCOPE).asInstanceOf[MethodTypeSignature])
    }

    // 4. Модификаторы доступа метода
    val modifiers = ModifiersExpression(method.access_flags, methodFlags)

    // 5. Исключения
    val exceptions = method.attributes.collect {
      case attr: ExceptionsAttribute => attr.exception_index_table.map(index => {
        cpUtils.getUtf8(classFile.constant_pool(index).asInstanceOf[ConstantClassInfo].name_index)
      })
    }.flatten.toList

    val exceptionsExpression = if (signature.isDefined && signature.get.exceptions.nonEmpty) {
      ThrowsExpression(signature.get.exceptions)
    } else if (exceptions.nonEmpty) {
      ThrowsExpression(exceptions.map(DecodeUtils.getSimpleClassName))
    } else {
      EMPTY_EXPRESSION
    }

    // 6. Параметры
    val namer = getNamer(method)
    var index = if (isStatic(method.access_flags)) 0 else 1
    val locNames = descriptor.parameters.map(parameter => {
      val argument = namer.getNextName(parameter.baseType, index)
      if (Descriptors.isCategory2(parameter)) {
        index += 1
      }
      index += 1
      argument
    })
    var parametersSeq: Seq[String] =
      if (signature.isDefined)
        for {
          (param, i) <- signature.get.methodParameters.view.zipWithIndex
          parameterName = locNames(i)
        } yield s"$param $parameterName"
      else
        for {
          (param, i) <- descriptor.parameters.zipWithIndex
        } yield s"${DecodeUtils.getViewType(param.meta)} ${locNames(i)}"

    // TODO here: осознаю всю хачность
    if (parametersSeq.nonEmpty && isVarargs(method.access_flags)) {
      val lastDescParam = descriptor.parameters.last
      val replacedValue = DecodeUtils.select(lastDescParam.isArray, "[] ", " ")
      val varArgsParam = parametersSeq.last.replace(replacedValue, " ... ")
      parametersSeq = parametersSeq.updated(parametersSeq.size - 1, varArgsParam)
    }

    // 7. Возвращаемое значение
    val returnText = if (isConstructor || isStaticBlock) EMPTY_EXPRESSION
                    else if (signature.isDefined) Sex(signature.get.typeReturn)
                    else Sex(DecodeUtils.getViewType(descriptor.returnType.meta))

    // 8. Generics
    val parametersExpression = if (signature.isDefined) signature.get.typeParameters else EMPTY_EXPRESSION

    // 9. Для аннотаций используется default
    val default = method.attributes.find(_.isInstanceOf[AnnotationDefaultAttribute]) collect {
      case attribute: AnnotationDefaultAttribute => new AnnotationDefaultsDecoder(classFile).decode(attribute)
    }
    val defaultExpression = if (default.isDefined) default.get else EMPTY_EXPRESSION

    // 10. Точка с запятой
    val semicolon = if (isAbstract(method.access_flags) || isInterface(classFile.access_flags) || default.isDefined) ";" else ""

    MethodDeclareExpression(modifiers, parametersExpression, returnText, methodNameExpr, parametersSeq,
      exceptionsExpression, defaultExpression, semicolon, isConstructor)
  }

}
