package org.powlab.jeye.decode.decoders

import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.utils.Clazz

object PackageDecoder extends IDecoder {
  def decode(clazz: Clazz): IExpression = {
    val cpUtils = clazz.classFile.constantPoolUtils
    val classFile = clazz.classFile
    val constantClass = cpUtils.getClassInformator(classFile.this_class)
    val index = constantClass.name.lastIndexOf("/")
    if (index != -1) {
      val pack = constantClass.name.substring(0, index).replace("/", ".")
      Sex(s"package $pack;")
    } else {
      EMPTY_EXPRESSION
    }
  }
}
