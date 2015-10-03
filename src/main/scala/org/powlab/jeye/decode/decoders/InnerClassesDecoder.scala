package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions.Sex
import org.powlab.jeye.utils.Clazz

//@TODO Некорректно определяется название внутреннего класса, если в названии класса есть символ $
object InnerClassesDecoder extends IDecoder {
  override def decode(clazz: Clazz): IExpression = {
    val classFile = clazz.classFile
    val innerClasses = findInnerClassesNames(classFile)
    val block = new BlockExpression
    innerClasses.foreach {
      className => {
        val expression = clazz.innerClasses.find(_.name == className) match {
          case Some(c) => Decoder.decode(c, isInner = true)
          case None => Sex(s"//Class $className not found")
        }
        block += expression
      }
    }
    block
  }

  private def findInnerClassesNames(classFile: ClassFile): Array[String] = {
    val cpUtils = classFile.constantPoolUtils
    val name = cpUtils.getClassInformator(classFile.this_class).javaName.split("\\.").last
    classFile.attributes.filter(_.isInstanceOf[InnerClassesAttribute]).flatMap {
      case attribute: InnerClassesAttribute => attribute.classes.filter(clazz => {
        val innerName = cpUtils.getClassInformator(clazz.inner_class_info_index).javaName.split("\\.").last
        clazz.outer_class_info_index == classFile.this_class ||
          (clazz.outer_class_info_index == 0 && innerName.startsWith(name)) &&
            clazz.inner_class_info_index != classFile.this_class
      }).map(clazz => {
        cpUtils.getClassInformator(clazz.inner_class_info_index).javaName.split("\\.").last
      })
    }.reverse
  }
}
