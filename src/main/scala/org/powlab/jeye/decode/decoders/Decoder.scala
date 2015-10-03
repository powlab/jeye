package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core.AccessFlags._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions.EMPTY_EXPRESSION
import org.powlab.jeye.decode.transformer.{EnumTransformer, PostTransformer}
import org.powlab.jeye.utils.Clazz
import scala.collection.mutable.ListBuffer
import org.powlab.jeye.decode.ClassFacades
import org.powlab.jeye.decode.expra.ExpressionGuideWorker
import org.powlab.jeye.decode.transformer.RemoveRedundantMethodTransformer
import org.powlab.jeye.decode.transformer.RemoveRedundantVariableTransformer

trait IDecoder {
  def decode(clazz: Clazz): IExpression
}

object Decoder {
  def decode(clazz: Clazz, isInner: Boolean = false): IExpression = new Decoder().decode(clazz, isInner)
}

case class DecodeContext(clazz: Clazz, rootDecoder: IDecoder)

class Decoder {

  private val transformers = new ListBuffer[PostTransformer]

  def register(transformer: PostTransformer): Unit = transformers += transformer

  //register(new EnumTransformer)

  /**
   * Декодирование класса Class/Enum/Annotation
   * @param clazz
   * @return
   */
  def decode(clazz: Clazz, isInner: Boolean = false): IExpression = {
    val classFile = clazz.classFile
    ClassFacades.registry(clazz.classFile, isInner)

    val block = new BlockExpression
    if (isSynthetic(classFile.access_flags)) {
      return EMPTY_EXPRESSION
    }

    if (!isInner) {
      block += PackageDecoder.decode(clazz) += ImportsDecoder.decode(clazz)
    }

    val clazzExpression = if (isEnum(classFile.access_flags)) {
      decodeEnum(clazz)
    } else if (isAnnotation(classFile.access_flags)) {
      decodeAnnotation(clazz)
    } else {
      decodeClass(clazz)
    }

    block += clazzExpression

    val guide = new ExpressionGuideWorker(block)
    guide += new RemoveRedundantMethodTransformer(classFile)
    guide += new RemoveRedundantVariableTransformer
    guide.go

    // TODO here: не правильно так, удаляются static block, пока отключил
    //transformers.foreach(_.transform(block))

    block
  }

  /**
   * Декодирование внутреннего класса Class/Enum/Annotation
   * @param clazz
   * @return
   */
  def decodeInner(clazz: Clazz): IExpression = decode(clazz, isInner = true)

  private def decodeClass(clazz: Clazz): IExpression =
    ClassExpression(ClassAnnotationsDecoder.decode(clazz), ClassNameDecoder.decode(clazz),
      new BlockExpression +=
        FieldsDecoder.decode(clazz) +=
        MethodsDecoder.decode(clazz) +=
        InnerClassesDecoder.decode(clazz))

  private def decodeEnum(clazz: Clazz): IExpression =
    ClassExpression(ClassAnnotationsDecoder.decode(clazz), ClassNameDecoder.decode(clazz),
      new BlockExpression +=
        EnumFieldsDecoder.decode(clazz) +=
        EnumMethodsDecoder.decode(clazz) +=
        InnerClassesDecoder.decode(clazz))

  private def decodeAnnotation(clazz: Clazz): IExpression = {
    val extendedContext = new ExtraInfo(ClassFacades.get(clazz.classFile))
    ClassExpression(ClassAnnotationsDecoder.decode(clazz), ClassNameDecoder.decode(clazz),
      new BlockExpression ++=
        clazz.classFile.methods.map(method => new MethodDeclareDecoder(clazz.classFile).decode(method, extendedContext)).toList)
  }

}
