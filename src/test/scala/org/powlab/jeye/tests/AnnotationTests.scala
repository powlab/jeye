package org.powlab.jeye.tests

import org.powlab.jeye.tests.annotation._
import org.powlab.jeye.decompile._

package annotation {

  //TODO fail
  @org.junit.Ignore
  class AnnotationTest1Test extends DecompileTestClass(classOf[AnnotationTest1]) {}

  //TODO fail
  @org.junit.Ignore
  class AnnotationTest2Test extends DecompileTestClass(classOf[AnnotationTest2]) {}

  //TODO fail
  @org.junit.Ignore
  class AnnotationTest3Test extends DecompileTestClass(classOf[AnnotationTest3]) {}

  //TODO fail
  @org.junit.Ignore
  class AnnotationTest4Test extends DecompileTestClass(classOf[AnnotationTest4]) {}

  //TODO fail
  @org.junit.Ignore
  class AnnotationTestAnnotationTest extends DecompileTestClass(classOf[AnnotationTestAnnotation]) {}

  //TODO fail
  @org.junit.Ignore
  class AnnotationTestAnnotation1Test extends DecompileTestClass(classOf[AnnotationTestAnnotation1]) {}

  //TODO fail
  @org.junit.Ignore
  class AnnotationTestAnnotation2Test extends DecompileTestClass(classOf[AnnotationTestAnnotation2]) {}

}