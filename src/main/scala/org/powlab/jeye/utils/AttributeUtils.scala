package org.powlab.jeye.utils

import org.powlab.jeye.core._
import scala.reflect.runtime.universe._
import scala.reflect.runtime.currentMirror

/**
 * TODO here: перенести в core назвать Attributes
 */
object AttributeUtils {

  def has[T: TypeTag](attributes: Array[AttributeBaseInfo]): Boolean = attributes.exists(checkType[T])

  def find[T: TypeTag](attributes: Array[AttributeBaseInfo]): Option[T] = attributes.find(checkType[T]).map(_.asInstanceOf[T])

  def get[T: TypeTag](attributes: Array[AttributeBaseInfo]): T = find[T](attributes).get

  private def checkType[T: TypeTag](value: Any): Boolean = currentMirror.reflect(value).symbol.toType <:< typeOf[T]

}
