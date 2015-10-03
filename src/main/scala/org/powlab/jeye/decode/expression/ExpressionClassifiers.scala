package org.powlab.jeye.decode.expression

object ExpressionClassifiers {

  private val EA = ExpressionAttribute
  private val EC = ExpressionClassifier

  // -- Аттрибуты, связанные с обходом -- \\
  val EA_WIDE = EA("wide") // возможен обход вширь
  val EA_DEPTH = EA("depth") // возможен обход вглубь
  val EA_FIXED = EA("fixed") // не требует обхода

  // -- Аттрибуты - характеристики -- \\
  val EA_TYPED = EA("typed") // типизированный
  val EA_CONST = EA("const") // константа
  val EA_IFABLE = EA("ifable") // условный
  val EA_BRACKETABLE = EA("bracketable") // оборачиваемый в скобки
  val EA_CHANGEABLE = EA("changeable") // изменчивый
  val EA_INVOKEABLE = EA("invokable") // вызываемый
  val EA_ASSIGNABLE = EA("assignable") // присваеваемый

  // -- Аттрибуты - именованные -- \\
  val EA_TYPE = EA("type") // типовой
  val EA_WORD = EA("word") // строковой
  val EA_SIGN = EA("sign") // знаковый
  val EA_LINE = EA("line") // линейный
  val EA_CLASS_DECLARE = EA("class_declare") // объявление класса
  val EA_MODIFIERS = EA("modifiers")
  val EA_TYPE_PARAMETERS = EA("type_parameters")
  val EA_RETURN_TYPE = EA("return_type")
  val EA_THROWS_EXPRESSION = EA("throws_expression")
  val EA_METHOD_PARAMETERS = EA("method_parameters")
  val EA_CLASS = EA("class") // класс
  val EA_METHOD_DECLARE = EA("method_declare") // объявление метода
  val EA_METHOD = EA("method") // метод
  val EA_FIELD_DECLARE = EA("field_declare") // объявление поля
  val EA_FIELD = EA("field") // поле
  val EA_BLOCK = EA("block") // блочный
  val EA_STATEMENT = EA("statement") // именнованный блок
  val EA_EMPTY = EA("empty") // пустой
  val EA_NULL = EA("null") // нулевой
  val EA_BOOLEAN = EA("boolean") // логический
  val EA_BYTE = EA("byte") // байтовый
  val EA_SHORT = EA("short") // 2х-байтовый
  val EA_CHAR = EA("char") // символьный
  val EA_INT = EA("int") // целый
  val EA_LONG = EA("long") // целый расширенный
  val EA_FLOAT = EA("float") // вещественный
  val EA_DOUBLE = EA("double") // вещественный расширенный
  val EA_STRING = EA("string") // строковый
  val EA_CLASS_CONST = EA("class_const") // классовый
  val EA_ENUM_LITERAL = EA("enum_literal") // перечисляемый
  val EA_ARRAY_LITERAL = EA("array_literal") // массив
  val EA_ANNOTATION = EA("annotaion") // аннотация
  val EA_DEFAULT_VALUE = EA("default_value") // дефолтный
  val EA_IMPORT = EA("import") // импортируемый
  val EA_METHOD_SIGNATURE = EA("method_signature") // сигнатура метода
  val EA_LOCAL_VARIABLE = EA("local_variable") // переменный
  val EA_LABEL = EA("label") // помеченный
  val EA_BREAK_LABEL = EA("break_label") // переход на метку
  val EA_CONTINUE_LABEL = EA("continue_label") // переход на метку
  val EA_TERNARY_REF = EA("ternary_ref") // ссылка на тернарный
  val EA_TERNARY = EA("ternary") // тернарный
  val EA_TERNARY_BOOLEAN = EA("ternary_boolean") // тернарно-логический
  val EA_WHILE_CYCLE = EA("while_cycle") // while-цикличный
  val EA_FOR_CYCLE = EA("for_cycle") // for-цикличный
  val EA_FOREACH_CYCLE = EA("foreach_cycle") // for-цикличный
  val EA_IF_WORD = EA("if_word") // полное со словом if условие
  val EA_IF_SIMPLE = EA("if_simple") // простое условие
  val EA_IF_BOOLEAN = EA("if_boolean") // логическое условие
  val EA_IF_GROUP = EA("if_group") // групповое (and, or) условие
  val EA_IF_XOR_GROUP = EA("if_xor_group") // групповое (xor) условие
  val EA_IF_CMP = EA("if_cmp") // выбор значения по условию
  val EA_SWITCH = EA("switch") // выбираемый
  val EA_RETURN = EA("return") // возвращаемый
  val EA_PRIMITIVE_CAST = EA("primitive_cast") // приведение одного примитива к другому
  val EA_CASE = EA("case") // вариант
  val EA_CATCH = EA("catch") // отлавливаемый
  val EA_GET_ARRAY_ITEM = EA("get_array_item") // получить элемент массива
  val EA_MATH_PAIR = EA("math_pair") // занк + переменная
  val EA_MATH = EA("math") // математический
  val EA_MATH_NEGATE = EA("math_negate") // отрицательный
  val EA_MATH_TILDE = EA("math_tilde") // инвертированный
  val EA_PRE_INC = EA("pre_inc") // инкрементируемый
  val EA_POST_INC = EA("post_inc") // инкрементируемый
  val EA_STORE_DECLARE_VAR = EA("store_declare") // обяъвление локальной переменной без инициализации
  val EA_STORE_VAR = EA("store_var") // сохранение значения в локальную переменную
  val EA_STORE_NEW_VAR = EA("store_new_var") // сохранение значения в локальную переменную в первый раз
  val EA_STORE_ARRAY_VAR = EA("store_array_var") // сохранение значения в массиве
  val EA_GET_FIELD = EA("get_field") // получение поля класса
  val EA_PUT_FIELD = EA("put_field") // установка поля класса
  val EA_GET_STATIC_FIELD = EA("get_static_field") // получение статического поля класса
  val EA_PUT_STATIC_FIELD = EA("put_static_field") // установка статического поля класса
  val EA_INSTANCE_OF = EA("instance_of") // аналогичного типа
  val EA_ARGUMENTS = EA("arguments") // аргументы
  val EA_INVOKE_INTERFACE = EA("invoke_interface") // вызов метода
  val EA_INVOKE_VIRTUAL = EA("invoke_virtual") // вызов метода
  val EA_CONSTRUCTOR = EA("constructor") // конструкстор
  val EA_NEW_OBJECT = EA("new_object") // создание объекта
  val EA_NEW_ARRAY = EA("new_array") // создание массива
  val EA_INIT_ARRAY = EA("init_array") // инициализация массива
  val EA_ARRAY_LENGTH = EA("array_length") // размерность массива
  val EA_INVOKE_SPECIAL = EA("invoke_special") // вызов метода
  val EA_INVOKE_STATIC = EA("invoke_static") // вызов метода
  val EA_METHOD_TYPE = EA("method_type") // вызов метода получения типа
  val EA_INVOKE_DYNAMIC = EA("invoke_dynamic") // динамический вызов метода
  val EA_SYNCHRONIZE = EA("synchronize") // синхронизированный
  val EA_CHECK_CAST = EA("check_cast") // приведение к типу
  val EA_THROW = EA("throw") // выбрасываемый
  val EA_WRAPPED = EA("wrapped") // оборачиваемый
  val EA_COMMENT = EA("comment") // коментируемый

  // -- Классификаторы -- \\
  val EC_TYPE = EC(EA_TYPE, EA_TYPED, EA_WIDE)
  val EC_WORD = EC(EA_WORD, EA_FIXED)
  val EC_SIGN = EC(EA_SIGN, EA_FIXED)
  val EC_LINE = EC(EA_LINE, EA_CHANGEABLE, EA_WIDE)
  val EC_CLASS_DECLARE = EC(EA_CLASS_DECLARE, EA_FIXED)
  val EC_CLASS = EC(EA_CLASS, EA_WIDE)
  val EC_METHOD_DECLARE = EC(EA_METHOD_DECLARE, EA_WIDE)
  val EC_TYPE_PARAMETERS = EC(EA_TYPE_PARAMETERS, EA_FIXED)
  val EC_RETURN_TYPE = EC(EA_RETURN_TYPE, EA_FIXED)
  val EC_MODIFIERS = EC(EA_MODIFIERS, EA_FIXED)
  val EC_THROWS_EXPRESSION = EC(EA_THROWS_EXPRESSION, EA_FIXED)
  val EC_METHOD_PARAMETERS = EC(EA_METHOD_PARAMETERS, EA_FIXED)
  val EC_METHOD = EC(EA_METHOD, EA_WIDE)
  val EC_FIELD_DECLARE = EC(EA_FIELD_DECLARE, EA_WIDE)
  val EC_FIELD = EC(EA_FIELD, EA_WIDE)
  val EC_BLOCK = EC(EA_BLOCK, EA_CHANGEABLE, EA_DEPTH)
  val EC_STATEMENT = EC(EA_STATEMENT, EA_CHANGEABLE, EA_DEPTH)
  val EC_EMPTY = EC(EA_EMPTY, EA_TYPED, EA_FIXED)
  val EC_NULL = EC(EA_NULL, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_BOOLEAN = EC(EA_BOOLEAN, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_BYTE = EC(EA_BYTE, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_SHORT = EC(EA_SHORT, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_CHAR = EC(EA_CHAR, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_INT = EC(EA_INT, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_LONG = EC(EA_LONG, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_FLOAT = EC(EA_FLOAT, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_DOUBLE = EC(EA_DOUBLE, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_STRING = EC(EA_STRING, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_CLASS_CONST = EC(EA_CLASS_CONST, EA_TYPED, EA_CONST, EA_FIXED)
  val EC_ENUM_LITERAL = EC(EA_ENUM_LITERAL, EA_FIXED)
  val EC_ARRAY_LITERAL = EC(EA_ARRAY_LITERAL, EA_WIDE)
  val EC_ANNOTATION = EC(EA_ANNOTATION, EA_FIXED) // исправить на EA_WIDE - когда выражение будет переделано
  val EC_DEFAULT_VALUE = EC(EA_DEFAULT_VALUE, EA_FIXED) // исправить на EA_WIDE - когда выражение будет переделано
  val EC_IMPORT = EC(EA_IMPORT, EA_FIXED)
  val EC_METHOD_SIGNATURE = EC(EA_METHOD_SIGNATURE, EA_FIXED)
  val EC_LOCAL_VARIABLE = EC(EA_LOCAL_VARIABLE, EA_TYPED, EA_WIDE)
  val EC_LABEL = EC(EA_LABEL, EA_FIXED)
  val EC_BREAK_LABEL = EC(EA_BREAK_LABEL, EA_FIXED)
  val EC_CONTINUE_LABEL = EC(EA_CONTINUE_LABEL, EA_FIXED)
  val EC_TERNARY_REF = EC(EA_TERNARY_REF, EA_TYPED, EA_WIDE)
  val EC_TERNARY = EC(EA_TERNARY, EA_TYPED, EA_WIDE)
  val EC_TERNARY_BOOLEAN = EC(EA_TERNARY_BOOLEAN, EA_TYPED, EA_WIDE)
  val EC_WHILE_CYCLE = EC(EA_WHILE_CYCLE, EA_WIDE)
  val EC_FOR_CYCLE = EC(EA_FOR_CYCLE, EA_WIDE)
  val EC_FOREACH_CYCLE = EC(EA_FOREACH_CYCLE, EA_WIDE)
  val EC_IF_WORD = EC(EA_IF_WORD, EA_TYPED, EA_WIDE)
  val EC_IF_SIMPLE = EC(EA_IF_SIMPLE, EA_IFABLE, EA_TYPED, EA_WIDE)
  val EC_IF_BOOLEAN = EC(EA_IF_BOOLEAN, EA_IFABLE, EA_TYPED, EA_WIDE)
  val EC_IF_GROUP = EC(EA_IF_GROUP, EA_IFABLE, EA_TYPED, EA_WIDE)
  val EC_IF_XOR_GROUP = EC(EA_IF_XOR_GROUP, EA_IFABLE, EA_TYPED, EA_WIDE)
  val EC_IF_CMP = EC(EA_IF_CMP, EA_TYPED, EA_WIDE)
  val EC_SWITCH = EC(EA_SWITCH, EA_WIDE)
  val EC_RETURN = EC(EA_RETURN, EA_TYPED, EA_WIDE)
  val EC_PRIMITIVE_CAST = EC(EA_PRIMITIVE_CAST, EA_TYPED, EA_WIDE)
  val EC_CASE = EC(EA_CASE, EA_WIDE)
  val EC_CATCH = EC(EA_CATCH, EA_FIXED)
  val EC_GET_ARRAY_ITEM = EC(EA_GET_ARRAY_ITEM, EA_TYPED, EA_WIDE)
  val EC_MATH_PAIR = EC(EA_MATH_PAIR, EA_WIDE)
  val EC_MATH = EC(EA_MATH, EA_TYPED, EA_WIDE)
  val EC_MATH_NEGATE = EC(EA_MATH_NEGATE, EA_TYPED, EA_WIDE)
  val EC_MATH_TILDE = EC(EA_MATH_TILDE, EA_TYPED, EA_WIDE)
  val EC_PRE_INC = EC(EA_PRE_INC, EA_TYPED, EA_WIDE)
  val EC_POST_INC = EC(EA_POST_INC, EA_TYPED, EA_WIDE)
  val EC_STORE_DECLARE_VAR = EC(EA_STORE_DECLARE_VAR, EA_TYPED, EA_WIDE)
  val EC_STORE_VAR = EC(EA_STORE_VAR, EA_ASSIGNABLE, EA_TYPED, EA_WIDE)
  val EC_STORE_NEW_VAR = EC(EA_STORE_NEW_VAR, EA_ASSIGNABLE, EA_TYPED, EA_WIDE)
  val EC_STORE_ARRAY_VAR = EC(EA_STORE_ARRAY_VAR, EA_ASSIGNABLE, EA_TYPED, EA_WIDE)
  val EC_GET_FIELD = EC(EA_GET_FIELD, EA_TYPED, EA_WIDE)
  val EC_PUT_FIELD = EC(EA_PUT_FIELD, EA_ASSIGNABLE, EA_TYPED, EA_WIDE)
  val EC_GET_STATIC_FIELD = EC(EA_GET_STATIC_FIELD, EA_TYPED, EA_WIDE)
  val EC_PUT_STATIC_FIELD = EC(EA_PUT_STATIC_FIELD, EA_ASSIGNABLE, EA_TYPED, EA_WIDE)
  val EC_INSTANCE_OF = EC(EA_INSTANCE_OF, EA_TYPED, EA_WIDE)
  val EC_ARGUMENTS = EC(EA_ARGUMENTS, EA_CHANGEABLE, EA_WIDE)
  val EC_INVOKE_INTERFACE = EC(EA_INVOKE_INTERFACE, EA_TYPED, EA_INVOKEABLE, EA_WIDE)
  val EC_INVOKE_VIRTUAL = EC(EA_INVOKE_VIRTUAL, EA_TYPED, EA_INVOKEABLE, EA_WIDE)
  val EC_CONSTRUCTOR = EC(EA_CONSTRUCTOR, EA_TYPED, EA_INVOKEABLE, EA_WIDE)
  val EC_NEW_OBJECT = EC(EA_NEW_OBJECT, EA_TYPED, EA_WIDE)
  val EC_NEW_ARRAY = EC(EA_NEW_ARRAY, EA_TYPED, EA_WIDE)
  val EC_INIT_ARRAY = EC(EA_INIT_ARRAY, EA_TYPED, EA_WIDE)
  val EC_ARRAY_LENGTH = EC(EA_ARRAY_LENGTH, EA_TYPED, EA_WIDE)
  val EC_INVOKE_SPECIAL = EC(EA_INVOKE_SPECIAL, EA_TYPED, EA_INVOKEABLE, EA_WIDE)
  val EC_INVOKE_STATIC = EC(EA_INVOKE_STATIC, EA_TYPED, EA_INVOKEABLE, EA_WIDE)
  val EС_METHOD_TYPE = EC(EA_METHOD_TYPE, EA_TYPED, EA_FIXED)
  val EC_INVOKE_DYNAMIC = EC(EA_INVOKE_DYNAMIC, EA_TYPED, EA_WIDE)
  val EC_SYNCHRONIZE = EC(EA_SYNCHRONIZE, EA_WIDE)
  val EC_CHECK_CAST = EC(EA_CHECK_CAST, EA_TYPED, EA_BRACKETABLE, EA_WIDE)
  val EC_THROW = EC(EA_THROW, EA_WIDE)
  val EC_WRAPPED = EC(EA_WRAPPED, EA_WIDE)
  val EС_COMMENT = EC(EA_COMMENT, EA_FIXED)

}

/** Аттрибуты выражений */
object ExpressionAttribute {
  def apply(name: String): ExpressionAttribute = new ExpressionAttribute(name)
}

/** Аттрибуты выражений */
class ExpressionAttribute(val name: String) {
  override def toString(): String = name
}

/** Классификация выражений */
object ExpressionClassifier {

  def apply(attr: ExpressionAttribute, attrs: ExpressionAttribute*): ExpressionClassifier = {
    new ExpressionClassifier((attr :: attrs.toList).toArray)
  }

}

/** Классификатор выражений */
class ExpressionClassifier(attrs: Array[ExpressionAttribute]) {
  def head = attrs(0)
  def is(attr: ExpressionAttribute): Boolean = head == attr
  def has(attr: ExpressionAttribute): Boolean = attrs.contains(attr)

  override def toString(): String = "clf-name: " + head + ", attrs: " + attrs.tail.mkString(", ")
}
