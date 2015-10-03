package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.Signatures._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.utils.DecodeUtils

case class SignatureInfo(typeParameters: TypeParametersExpression, typeReturn: String, methodParameters: Array[String], exceptions: Array[String])

//@TODO Доделать декодер для классов + протестировать декодер для методов
class SignatureDecoder {

  def decode(iType: IType): IExpression = Sex(decodeSignature(iType))

  def decodeMethodSignature(signature: MethodTypeSignature): SignatureInfo = {
    val typeParameters = signature.formalTypeParameters.map(decodeFormalTypeParameter)
    val typeReturn = decodeSignature(signature.returnType)
    val throws = signature.throwsSignature.map(decodeSignature)
    val typeParametersText = TypeParametersExpression(typeParameters)
    SignatureInfo(typeParametersText, typeReturn, signature.typesSignatures.map(decodeSignature), throws)
  }

  private def decodeClassTypeSignature(signature: ClassTypeSignature): String = {
    val className = DecodeUtils.getSimpleClassName(signature.identifier)
    val suffixes = signature.suffixes.map(decodeClassTypeSignatureSuffix).mkString(".")
    val arguments = signature.typeArguments.map(decodeTypeArgument)
    s"$className${if (arguments.size > 0) arguments.mkString("<", ", ", ">") else ""}$suffixes"
  }

  private def decodeClassTypeSignatureSuffix(suffix: ClassTypeSignatureSuffix): String = {
    val className = DecodeUtils.getSimpleClassName(suffix.identifier)
    val arguments = suffix.typeArguments.map(decodeTypeArgument)
    s"$className${if (arguments.size > 0) arguments.mkString("<", ", ", ">") else ""}"
  }

  private def decodeTypeArgument(argument: TypeArgument): String = if (argument.fieldTypeSignature != null) {
    s"${decodeSignature(argument.fieldTypeSignature)}"
  } else {
    "?"
  }

  private def decodeTypeVariableSignature(signature: TypeVariableSignature): String = signature.identifier

  private def decodeArrayTypeSignature(signature: ArrayTypeSignature): String = {
    val dimension = (for (i <- 1 to signature.dimension) yield "[]") mkString ""
    s"${decodeSignature(signature.componentType)}$dimension"
  }

  //@TODO Отрефакторить метод
  private def decodeFormalTypeParameter(signature: FormalTypeParameter): String = {
    val identifier = signature.identifier
    val classBoundText = if (signature.classBound != null) s" extends ${decodeSignature(signature.classBound)}" else " extends"
    val interfaces = signature.interfacesBound.map(decodeSignature)
    val interfacesText = if (interfaces.nonEmpty) s" ${if (signature.classBound != null) s"& " else ""}${interfaces.mkString(" & ")}" else ""
    s"$identifier${if (" extends Object".equals(classBoundText)) "" else classBoundText}$interfacesText"
  }

  def decodeClassSignature(signature: ClassSignature): String = {
    val typeParameters = signature.formalTypeParameters.map(decodeFormalTypeParameter)
    val superClass = decodeClassTypeSignature(signature.superClassSignature)
    val interfaces = signature.superInterfaceSignatures.map(decodeClassTypeSignature)
    val typeParametersText = if (typeParameters.nonEmpty) typeParameters.mkString("<", ", ", ">") else ""
    val superClassText = if (superClass.nonEmpty && !"Object".equals(superClass)) s" extends $superClass" else ""
    val interfacesText = if (interfaces.nonEmpty) s" implements ${interfaces.mkString(" & ")}" else ""
    s"$typeParametersText$superClassText$interfacesText"
  }

  private def decodeBaseType(baseType: BaseType) = baseType.description

  private def decodeSignature(iType: IType): String = iType match {
    case signature: FieldTypeSignature => decodeSignature(signature.fieldType)
    case signature: ClassTypeSignature => decodeClassTypeSignature(signature)
    case signature: ArrayTypeSignature => decodeArrayTypeSignature(signature)
    case signature: TypeVariableSignature => decodeTypeVariableSignature(signature)
    case signature: ClassSignature => decodeClassSignature(signature)
    case signature: BaseType => decodeBaseType(signature)
  }

}
