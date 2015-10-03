package org.powlab.jeye.tests

import org.powlab.jeye.tests.boxing._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package boxing {

  class BoxingTest1Test extends DecompileTestClass(classOf[BoxingTest1]) {}

  class BoxingTest2Test extends DecompileTestClass(classOf[BoxingTest2]) {}

  class BoxingTest3Test extends DecompileTestClass(classOf[BoxingTest3]) {}

  class BoxingTest4Test extends DecompileTestClass(classOf[BoxingTest4]) {}

  //TODO OK: с боксингом все ок, срабатывает оптимизатор на тернарный оператор
  @org.junit.Ignore
  class BoxingTest5Test extends DecompileTestClass(classOf[BoxingTest5]) {}

  //TODO OK: с боксингом все ок, срабатывает оптимизатор на тернарный оператор
  @org.junit.Ignore
  class BoxingTest5aTest extends DecompileTestClass(classOf[BoxingTest5a]) {}

  class BoxingTest6Test extends DecompileTestClass(classOf[BoxingTest6]) {}

  class BoxingTest6aTest extends DecompileTestClass(classOf[BoxingTest6a]) {}

  class BoxingTest6bTest extends DecompileTestClass(classOf[BoxingTest6b]) {}

  class BoxingTest6cTest extends DecompileTestClass(classOf[BoxingTest6c]) {}

  class BoxingTest6dTest extends DecompileTestClass(classOf[BoxingTest6d]) {}

  class BoxingTest7Test extends DecompileTestClass(classOf[BoxingTest7]) {}

  class BoxingTest8Test extends DecompileTestClass(classOf[BoxingTest8], true) {}

  // TODO here: нужно улучшить, убрать (Number)
  class BoxingTest8bTest extends DecompileTestClass(classOf[BoxingTest8b], true) {}

  class BoxingTest8cTest extends DecompileTestClass(classOf[BoxingTest8c], true) {}

  class BoxingTest9Test extends DecompileTestClass(classOf[BoxingTest9]) {}

  class BoxingTest10Test extends DecompileTestClass(classOf[BoxingTest10]) {}

  class BoxingTest11Test extends DecompileTestClass(classOf[BoxingTest11], true) {}

  //TODO fail: не декомпелируется выражение integer++, т.е. для типа java.lang.Integer
  @org.junit.Ignore
  class BoxingTest11aTest extends DecompileTestClass(classOf[BoxingTest11a]) {}

  //TODO fail: не декомпилируется generic у переменных, например, Iterator<Integer>
  @org.junit.Ignore
  class BoxingTest11bTest extends DecompileTestClass(classOf[BoxingTest11b]) {}

  class BoxingTest12Test extends DecompileTestClass(classOf[BoxingTest12], true) {}

  class BoxingTest13Test extends DecompileTestClass(classOf[BoxingTest13], true) {}

  class BoxingTest14Test extends DecompileTestClass(classOf[BoxingTest14]) {}

  class BoxingTest15Test extends DecompileTestClass(classOf[BoxingTest15], true) {}

  class BoxingTest15aTest extends DecompileTestClass(classOf[BoxingTest15a], true) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду, так как идет потеря инфы
  @org.junit.Ignore
  class BoxingTest16Test extends DecompileTestClass(classOf[BoxingTest16]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду, так как идет потеря инфы
  @org.junit.Ignore
  class BoxingTest16aTest extends DecompileTestClass(classOf[BoxingTest16a]) {}

  //TODO fail: Не определяется Object reference1, определяется как Integer, для решения нужна карта наследования
  @org.junit.Ignore
  class BoxingTest16a2Test extends DecompileTestClass(classOf[BoxingTest16a2]) {}

  class BoxingTest16a3Test extends DecompileTestClass(classOf[BoxingTest16a3]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду, так как идет потеря инфы
  @org.junit.Ignore
  class BoxingTest16a4Test extends DecompileTestClass(classOf[BoxingTest16a4]) {}

  //TODO OK: Декодируется корректно, но можно улучшить, для улучшения решения нужна карта наследования
  @org.junit.Ignore
  class BoxingTest16bTest extends DecompileTestClass(classOf[BoxingTest16b]) {}

  class BoxingTest16b2Test extends DecompileTestClass(classOf[BoxingTest16b2]) {}

  //TODO OK: Декодируется корректно, но можно улучшить, для улучшения решения нужна карта наследования
  @org.junit.Ignore
  class BoxingTest16cTest extends DecompileTestClass(classOf[BoxingTest16c]) {}

  class BoxingTest16dTest extends DecompileTestClass(classOf[BoxingTest16d]) {}

  class BoxingTest17Test extends DecompileTestClass(classOf[BoxingTest17]) {}

  class BoxingTest18Test extends DecompileTestClass(classOf[BoxingTest18]) {}

  class BoxingTest19Test extends DecompileTestClass(classOf[BoxingTest19]) {}

  //TODO OK: Декодируется корректно: даже лучше
  @org.junit.Ignore
  class BoxingTest20Test extends DecompileTestClass(classOf[BoxingTest20]) {}

  class BoxingTest21Test extends DecompileTestClass(classOf[BoxingTest21]) {}

  class BoxingTest22Test extends DecompileTestClass(classOf[BoxingTest22]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду, так как идет потеря инфы
  @org.junit.Ignore
  class BoxingTest23Test extends DecompileTestClass(classOf[BoxingTest23]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду. Вопрос к тесту
  @org.junit.Ignore
  class BoxingTest24Test extends DecompileTestClass(classOf[BoxingTest24]) {}

  //TODO OK: Object -> Integer. Нужно разобраться с StackMapTable
  @org.junit.Ignore
  class BoxingTest25Test extends DecompileTestClass(classOf[BoxingTest25]) {}

  class BoxingTest26Test extends DecompileTestClass(classOf[BoxingTest26]) {}

  class BoxingTest26aTest extends DecompileTestClass(classOf[BoxingTest26a]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду. Вопрос к тесту
  @org.junit.Ignore
  class BoxingTest26bTest extends DecompileTestClass(classOf[BoxingTest26b]) {}

  class BoxingTest27Test extends DecompileTestClass(classOf[BoxingTest27]) {}

  class BoxingTest27bTest extends DecompileTestClass(classOf[BoxingTest27b]) {}

  class BoxingTest27cTest extends DecompileTestClass(classOf[BoxingTest27c]) {}

  class BoxingTest27dTest extends DecompileTestClass(classOf[BoxingTest27d]) {}

  class BoxingTest28Test extends DecompileTestClass(classOf[BoxingTest28]) {}

  class BoxingTest28aTest extends DecompileTestClass(classOf[BoxingTest28a]) {}

  class BoxingTest28bTest extends DecompileTestClass(classOf[BoxingTest28b]) {}

  //TODO OK: Слишком не однозначно. предлагаю оставить без анбоксинга
  @org.junit.Ignore
  class BoxingTest28cTest extends DecompileTestClass(classOf[BoxingTest28c]) {}

  class BoxingTest28dTest extends DecompileTestClass(classOf[BoxingTest28d]) {}

  //TODO OK: Декодируется корректно, как и в тестах выше, если сделаем здесь, нужно править текст тестов выше
  @org.junit.Ignore
  class BoxingTest29Test extends DecompileTestClass(classOf[BoxingTest29]) {}

  //TODO OK: Декодируется корректно, лучше
  class BoxingTest29bTest extends DecompileTestClass(classOf[BoxingTest29b], false, ArrayBuffer(("(Integer)", ""))) {}

  //TODO OK: Декодируется корректно, как и в тестах выше, если сделаем здесь, нужно править текст тестов выше
  @org.junit.Ignore
  class BoxingTest30Test extends DecompileTestClass(classOf[BoxingTest30]) {}

  class BoxingTest31Test extends DecompileTestClass(classOf[BoxingTest31], true) {}

  class BoxingTest32Test extends DecompileTestClass(classOf[BoxingTest32]) {}

  //TODO fail: очень не плохо. Для корректной декомпиляции нужно иметь classFacade для каждого класса
  @org.junit.Ignore
  class BoxingTest33Test extends DecompileTestClass(classOf[BoxingTest33]) {}

  //TODO fail: очень не плохо:
  //1. Не корректно дек-тся имя внутреннего класса при вызовах в аргументах BoxingTest33a.Horrid -> Horrid
  //2. Нет статического модификатора у внутреннего класса
  //Зона ответственности:
  @org.junit.Ignore
  class BoxingTest33aTest extends DecompileTestClass(classOf[BoxingTest33a]) {}

  //TODO OK: для boolean без разницы!
  @org.junit.Ignore
  class BoxingTest34Test extends DecompileTestClass(classOf[BoxingTest34], true) {}

  class BoxingTest35Test extends DecompileTestClass(classOf[BoxingTest35]) {}

  class BoxingTest36Test extends DecompileTestClass(classOf[BoxingTest36]) {}

}