package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._

/**
 * TODO here: перенести в трансформер!
 */
object EnumMethodsDecoder extends BaseMethodsDecoder {
  override def getMethods(classFile: ClassFile): Array[MemberInfo] =
    super.getMethods(classFile).filter(method => {
      val name = classFile.constantPoolUtils.getUtf8(method.name_index)
      name != "values" && name != "valueOf"
    })
}