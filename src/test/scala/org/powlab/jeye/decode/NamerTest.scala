package org.powlab.jeye.decode

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.powlab.jeye.core._

import Types._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NamerTest extends Specification {

  "methodSignature" should {
    val namer = new Namer
    val booleans = (for (i <- 1 to 3) yield namer.getNextName(TYPE_BOOLEAN)).toList
    "equals three booleans" in {
      booleans must beEqualTo(List("boolean0", "boolean1", "boolean2"))
    }

    "boolean2 isReserved" in {
      namer.isReserved("boolean2") must beTrue
    }

    val ints = (for (i <- 1 to 3) yield namer.getNextName(TYPE_INT)).toList
    "equals three ints" in {
      ints must beEqualTo(List("int0", "int1", "int2"))
    }
  }

}
