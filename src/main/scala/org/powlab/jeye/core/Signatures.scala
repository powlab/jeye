package org.powlab.jeye.core

import org.powlab.jeye.core.Types._

/**
 * Сигнатруы.
 * Jvm specification Chapter Descriptors and Signatures
 */
object Signatures {

  case class ClassSignature(formalTypeParameters: Array[FormalTypeParameter],
    superClassSignature: ClassTypeSignature,
    superInterfaceSignatures: Array[ClassTypeSignature],
    signature: String) extends IType(signature) {
    name = "ClassSignature"
  }

  case class FormalTypeParameter(identifier: String,
    classBound: FieldTypeSignature,
    interfacesBound: Array[FieldTypeSignature],
    signature: String) extends IType(signature) {
    name = "FormalTypeParameter"
  }

  case class FieldTypeSignature(val fieldType: IType, signature: String) extends IType(signature) {
    name = "FieldTypeSignature"
  }

  case class ClassTypeSignatureSuffix(identifier: String, typeArguments: Array[TypeArgument], signature: String) extends IType(signature) {
    name = "ClassTypeSignatureSuffix"
  }

  case class ClassTypeSignature(identifier: String,
    typeArguments: Array[TypeArgument],
    suffixes: Array[ClassTypeSignatureSuffix],
    signature: String) extends IType(signature) {
    name = "ClassTypeSignature"
  }

  case class TypeArgument(wildCardIndicator: Char, fieldTypeSignature: FieldTypeSignature, signature: String) extends IType(signature) {
    name = "TypeArgument"
  }

  case class ArrayTypeSignature(dimension: Int, componentType: IType, signature: String) extends IType(signature) {
    name = "ArrayTypeSignature"
    def isClassType: Boolean = {
      componentType.isInstanceOf[ClassTypeSignature]
    }

    def isVariableType: Boolean = {
      componentType.isInstanceOf[TypeVariableSignature]
    }

    def isBaseType: Boolean = {
      componentType.isInstanceOf[BaseType]
    }
  }

  case class TypeVariableSignature(identifier: String, signature: String) extends IType(signature) {
    name = "TypeVariableSignature"
  }

  case class MethodTypeSignature(formalTypeParameters: Array[FormalTypeParameter],
    typesSignatures: Array[IType],
    returnType: IType,
    throwsSignature: Array[IType],
    signature: String) extends IType(signature) {
    name = "MethodTypeSignature"
  }

}
