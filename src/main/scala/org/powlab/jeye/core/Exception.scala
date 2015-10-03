package org.powlab.jeye.core

import java.io.{ ByteArrayInputStream, DataInputStream }
import java.nio.ByteBuffer

/**
 * Ошибки приложения jEye
 * Цель: максимально детализировать причину и следствие, указать область возникновения ошибки
 */
object Exception {
  val PROCESSOR_AREA = "Процессоры инструкций"
  val TREE_AREA = "Дерево инструкций"
  val NODE_AREA = "Инструкции"
  val NODE_DETAILS_AREA = "Детали инструкций"
  val PATTERN_AREA = "Шаблоны инструкций"
  val SID_AREA = "Структурированные идентификаторы"
  val TERNARY_AREA = "Тернарный оператор"
  val EXPRESSION_AREA = "Выражения"
  val INJECTOR_AREA = "Инъекция инструкций"
  val RESOURCE_AREA = "Контейнер с инструкциями"
  val EXPRA_AREA = "Анализатор выражений"
  val TRANSFORM_AREA = "Трансформер выражений"
  val GUIDE_AREA = "Путеводитель по выражениям"

  def apply(area: String, reason: String, effect: String, action: String, cause: Throwable = null): RuntimeException = {
    val builder = new StringBuilder
    builder.append("Ошибка:\n")
    builder.append("Область: ").append(area).append("\n")
    builder.append("Причина: ").append(reason).append("\n")
    builder.append("Следствие: ").append(effect).append("\n")
    builder.append("Действие: ").append(action)
    if (cause == null) {
      new RuntimeException(builder.toString)
    } else {
      new RuntimeException(builder.toString, cause)
    }
  }
}