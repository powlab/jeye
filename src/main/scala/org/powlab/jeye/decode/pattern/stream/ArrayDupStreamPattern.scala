package org.powlab.jeye.decode.pattern.stream

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.store.StoreInformator.isBaseStoreNode
import org.powlab.jeye.decode.processor.stack.StackInformator.isDupNode
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.{isAnewarrayNode, isNewarrayNode}
import org.powlab.jeye.decode.graph.OpcodeNode

/**
 * anewarray/newarray + dup - говорит нам о том, что нужно создать локальную переменную
 * и засетить в нее массив
 * Группа состоит из anewarray/newarray
 *
 * anewarray - создает массивы объектов
 * newarray  - создает массивы примитивов
 *
 * Суть:
 * Для выражения  Class[] classes = new Class[] {String.class, int.class, Double.class};
 * генериться пара опкодов anewarray + dup - для которой нужно сделать особый обработчик -
 * создать локальную переменную (в данном случае classes) и передать ее в операнд стэк
 * Если этого не сделать то в операнд стэк уйдет выражение 'new Class[]' и
 * вместо classes[0] мы получим new Class[][0]
 * На самом деле anewarray + dup = anewarray + store + load, но так как 2ой вариант длинее, компилятор берет первый
 */
class ArrayDupStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = new OpcodeDetail(DETAIL_ARRAY_DUP)
  def resolve(dupNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    if (!isDupNode(dupNode) || tree.previewCount(dupNode) != 1) {
      return null
    }
    val arrayNode = tree.preview(dupNode)
    if (! (isAnewarrayNode(arrayNode) || isNewarrayNode(arrayNode))) {
      return null
    }
    return arrayNode
  }

}



