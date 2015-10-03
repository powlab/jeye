package org.powlab.jeye.decode.sids

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import org.junit.Test
import org.junit.Assert._

/**
 * Api по sid'у
 * #1
 * #2
 * #3_1#1
 * #3_1#2
 */
class SidTest {

  /**
   * Тест функции создания sid
   */
  @Test
  def testMake() {
    assertEquals(Sid.INITIAL_ID, Sid.make("", 1))
    assertEquals("#1_1#1", Sid.make("#1_1", 1))
  }

  def testSidGet() {
    val sid = Sid.INITIAL_SID
    assertEquals(null, sid.parentId)
    assertEquals("#2", sid.nextId)
    assertEquals("#2_1#1", sid.childId(1))
    val childSid = sid.childSid(1)
    assertEquals("#2", childSid.parentId)
  }
}
