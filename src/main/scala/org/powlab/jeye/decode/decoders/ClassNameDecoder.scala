package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.AccessFlags._
import org.powlab.jeye.core.parsing.SignatureParser._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions.{Sex, EMPTY_EXPRESSION}
import org.powlab.jeye.utils.{AttributeUtils, Clazz, DecodeUtils}

/**
 * TODO here: требуется рефакторинг!!!!!!!!!!
 */
object ClassNameDecoder extends IDecoder {
  override def decode(clazz: Clazz): ClassDeclareExpression = {
    val classFile = clazz.classFile
    val cpUtils = classFile.constantPoolUtils
    val signatureDecoder = new SignatureDecoder()
    val parentName = findParentName(classFile) map {
      name => name + "$"
    } getOrElse ""
    val signature = AttributeUtils.find[SignatureAttribute](classFile.attributes) map {
      case signature: SignatureAttribute => signatureDecoder.decode(
        parseSignature(cpUtils.getUtf8(signature.signature_index), StructureScope.CLASS_SCOPE))
    } getOrElse EMPTY_EXPRESSION
    val className = cpUtils.getClassInformator(classFile.this_class).simpleName
      .replace(".", "$").substring(parentName.length)
    val postfix = if (signature != EMPTY_EXPRESSION) {
      // TODO here: в двух строчках кода 2 хака!!!
      var afterClass = signature.view(EMPTY_EXPRESSION).replaceFirst(" extends Enum<.+>", "") // Для енумов вырезаем extends
      if (isInterface(classFile.access_flags)) {
        afterClass = afterClass.replace("implements", "extends")
      }
      afterClass
    } else
    // TODO here: вынести этот ограничение внутрь цикла итерации по интерфейсамs
    if (! isAnnotation(classFile.access_flags)) {
      var afterClass = ""
      val superClass = cpUtils.superClass
      if (Descriptors.TYPE_OBJECT_DESCRIPTOR.meta != superClass.meta) {
        afterClass = " extends " + superClass.simpleName
      }
      var index = 0
      while (index < classFile.interfaces_count) {
        val firstByte = classFile.interfaces(index << 1)
        val secondByte = classFile.interfaces((index << 1) + 1)
        val shortValue = Utils.toShort(firstByte, secondByte)
        val interfaceClass = cpUtils.getClassInformator(shortValue)
        if (index == 0) {
          afterClass = afterClass + " implements "
        } else {
          afterClass = afterClass + ", "
        }
        afterClass = afterClass + interfaceClass.simpleName
        index = index + 1
      }
      afterClass
    } else {
      ""
    }

    //@TODO Нужен рефакторинг ниже
    val enumFlags = getVisibleFlags(StructureScope.INNER_CLASS_SCOPE, classFile.access_flags, flag =>
      flag != ACC_ABSTRACT &&
      flag != ACC_INTERFACE &&
      flag != ACC_ANNOTATION &&
      flag != ACC_FINAL).map(_.title).mkString(" ")
    val customFlags = getVisibleFlags(StructureScope.INNER_CLASS_SCOPE, classFile.access_flags, flag =>
      flag != ACC_ABSTRACT &&
      flag != ACC_INTERFACE &&
      flag != ACC_ANNOTATION).map(_.title).mkString(" ")
    val defaultFlags = getVisibleFlags(StructureScope.INNER_CLASS_SCOPE, classFile.access_flags).map(_.title).mkString(" ")
    val prefix = if (isEnum(classFile.access_flags)) {
      enumFlags.trim() + " " + ACC_ENUM.title
    } else if (isAnnotation(classFile.access_flags)) {
      customFlags.trim() + " " + ACC_ANNOTATION.title
    } else if (isInterface(classFile.access_flags)) {
      customFlags.trim() + " " + ACC_INTERFACE.title
    } else {
      defaultFlags + " " + "class"
    }

    ClassDeclareExpression(prefix.trim + " ", className, postfix)
  }

  private def findParentName(classFile: ClassFile): Option[String] = {
    val cpUtils = classFile.constantPoolUtils
    classFile.attributes.find(_.isInstanceOf[InnerClassesAttribute]) flatMap {
      case attribute: InnerClassesAttribute => attribute.classes.find(clazz =>
        clazz.inner_class_info_index == classFile.this_class &&
          clazz.outer_class_info_index != 0
      ) map {
        clazz => {
          cpUtils.getClassInformator(clazz.outer_class_info_index).javaName.split("\\.").last
        }
      }
    }
  }
}

