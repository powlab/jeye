package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core.ClassFile
import org.powlab.jeye.core.AccessFlags
import org.powlab.jeye.decode.expression.{BlockExpression, IExpression, Expressions}
import org.powlab.jeye.decode.ClassFacade.methodKey
import org.powlab.jeye.decode.expra.{ExpressionAnalyzator, SwitchByEnumExpra}
import org.powlab.jeye.decode.ClassFacades
import org.powlab.jeye.utils.{Clazz, DecodeUtils}
import org.powlab.jeye.core.CodeAttribute
import org.powlab.jeye.utils.AttributeUtils
import org.powlab.jeye.core.StructureScope
import org.powlab.jeye.core.parsing.DescriptorParser

class BaseMethodsDecoder extends IDecoder {

  override def decode(clazz: Clazz): IExpression = {
    val classFile = clazz.classFile
    if (classFile.methods.length > 0) {
      val block = new BlockExpression
      val extendedContext: ExtraInfo = new ExtraInfo(ClassFacades.get(clazz.classFile))
      val methods = getMethods(classFile)
      methods.foreach(method => {
        val result = new MethodDecoder(classFile).decode(method, extendedContext)
        // анализируем синтетический метод для enum
        // TODO here: работает для eclipse
        // TODO here: так как добавлен ClassFacades - можно делать для всех (javac/ecj)
        //val methodName = classFile.constantPoolUtils.getUtf8(method.name_index)
        //val methodDescriptor = extendedContext.classFacade.getMethodDescriptor(methodKey(method))
        // TODO here: переделать на нормальные трансформеры!
        //val switchExpra = new SwitchByEnumExpra(methodName, methodDescriptor)
        //if (AccessFlags.isSynthetic(method.access_flags) && switchExpra.accept()) {
        //  ExpressionAnalyzator.scan(result, switchExpra)
        //  if (switchExpra.map.nonEmpty) {
        //    extendedContext.addEnum(switchExpra.enumClassName, switchExpra.map)
        //  }
       // }
        // ХАК, подумать как сделать номально
//        val methodName = classFile.constantPoolUtils.getUtf8(method.name_index)
//        if (DecodeUtils.isConstructor(methodName)) {
//
//        } else
        block += result
      })
      block
    } else {
      Expressions.EMPTY_EXPRESSION
    }
  }

  protected def getMethods(classFile: ClassFile) = {
    val cpUtils = classFile.constantPoolUtils
    classFile.methods.filter(method => {
      var accept = true
//      val methodName = cpUtils.getUtf8(method.name_index)
//      if (classFile.constantPoolUtils.constructorCount == 1 && DecodeUtils.isConstructor(methodName)) {
//        if (AttributeUtils.has[CodeAttribute](method.attributes)) {
//          val code = AttributeUtils.get[CodeAttribute](method.attributes)
//          accept = code.code_length > 5;
//          if (!accept) {
//            val descriptor = cpUtils.getUtf8(method.descriptor_index)
//            val methodDescriptor = DescriptorParser.parseMethodDescriptor(descriptor)
//            accept = methodDescriptor.parameters.nonEmpty
//          }
//        }
//      }
      //println(methodName, AccessFlags.getFlags(StructureScope.METHOD_SCOPE, method.access_flags))
      accept
      // &&
      // test only
      //"getc" == methodName
      //true
    }).sortBy(method => {
      // Пока порядок статических методов лучше оставлять таким, какой он в байткоде
      //if (AccessFlags.isStatic(method.access_flags)) 0
      //else
      if (DecodeUtils.isConstructor(classFile.constantPoolUtils.getUtf8(method.name_index))) 1
      else 2
    })
  }

}