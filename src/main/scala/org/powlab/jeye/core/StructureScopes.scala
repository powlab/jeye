package org.powlab.jeye.core

/**
 * Область описываемых данных
 */
object StructureScope {
  val CLASS_SCOPE = new StructureScope("class", 1)
  val METHOD_SCOPE = new StructureScope("method", 2)
  val FIELD_SCOPE = new StructureScope("field", 3)
  val INNER_CLASS_SCOPE = new StructureScope("inner_class", 4)
  //TODO here: добавить скопы: enum, annotation и интерфейс

}

/**
 * Область описываемых данных
 */
class StructureScope(val name: String, val code: Int) {
  override def toString: String = "[" + code + "] " + name
}


