package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.AccessFlags._
import org.powlab.jeye.decode.expression.{BlockExpression, IExpression, MethodExpression}
import org.powlab.jeye.decode.decoders.AnnotationDecodeUtils.AnnotationExpression
import org.powlab.jeye.utils.AttributeUtils


class MethodDecoder(classFile: ClassFile) extends MethodPartDecoder(classFile) {

  private val annotationDecoder = new ClassAnnotationDecoder(classFile)
  private val declareDecoder = new MethodDeclareDecoder(classFile)
  lazy private val bodyDecoder = new MethodBodyDecoder(classFile)

  def decode(method: MemberInfo, extraInfo: ExtraInfo = ExtraInfo.EMPTY): MethodExpression = {
    val annotations: List[AnnotationExpression] = method.attributes.collect {
      case attr: RuntimeAnnotationsAttribute =>
        attr.annotations.map(annotation => {
          annotationDecoder.decode(annotation)
        })
    }.flatten.toList
    val signature = declareDecoder.decode(method, extraInfo)
    val isAbstractFlag = isAbstract(method.access_flags)
    val body = if (! isAbstractFlag) {
      if (AttributeUtils.has[CodeAttribute](method.attributes)) {
        bodyDecoder.decode(method, extraInfo)
      } else {
        new BlockExpression
      }
    } else {
      new BlockExpression
    }

    MethodExpression(new BlockExpression ++= annotations, signature, body, isAbstractFlag)
  }

}