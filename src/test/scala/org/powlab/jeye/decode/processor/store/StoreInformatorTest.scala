package org.powlab.jeye.decode.processor.store

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.processor.store.StoreInformator._
import org.powlab.jeye.decode.TestUtils._
import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.ArrayBuffer

class StoreInformatorTest {

  @Test
  def testIsBaseStoreNode() {
    assertFalse(isBaseStoreNode(null))
    val baseStores = new ArrayBuffer[OpCode]
    baseStores += OPCODE_ISTORE
    baseStores += OPCODE_ISTORE_0
    baseStores += OPCODE_ISTORE_1
    baseStores += OPCODE_ISTORE_2
    baseStores += OPCODE_ISTORE_3
    baseStores += OPCODE_LSTORE
    baseStores += OPCODE_LSTORE_0
    baseStores += OPCODE_LSTORE_1
    baseStores += OPCODE_LSTORE_2
    baseStores += OPCODE_LSTORE_3
    baseStores += OPCODE_FSTORE
    baseStores += OPCODE_FSTORE_0
    baseStores += OPCODE_FSTORE_1
    baseStores += OPCODE_FSTORE_2
    baseStores += OPCODE_FSTORE_3
    baseStores += OPCODE_DSTORE
    baseStores += OPCODE_DSTORE_0
    baseStores += OPCODE_DSTORE_1
    baseStores += OPCODE_DSTORE_2
    baseStores += OPCODE_DSTORE_3
    baseStores += OPCODE_ASTORE
    baseStores += OPCODE_ASTORE_0
    baseStores += OPCODE_ASTORE_1
    baseStores += OPCODE_ASTORE_2
    baseStores += OPCODE_ASTORE_3
    OPCODES.filter(!baseStores.contains(_)).foreach(opcode => assertFalse(isBaseStoreNode(makeOpcode(opcode))))
    baseStores.foreach(opcode => assertTrue(isBaseStoreNode(makeOpcode(opcode))))
  }

  @Test
  def testIsReferenceStoreNode {
    assertFalse(isBaseStoreNode(null))
    val stores = new ArrayBuffer[OpCode]
    stores += OPCODE_ASTORE
    stores += OPCODE_ASTORE_0
    stores += OPCODE_ASTORE_1
    stores += OPCODE_ASTORE_2
    stores += OPCODE_ASTORE_3
    OPCODES.filter(!stores.contains(_)).foreach(opcode => assertFalse(isReferenceStoreNode(makeOpcode(opcode))))
    stores.foreach(opcode => assertTrue(isReferenceStoreNode(makeOpcode(opcode))))
  }

}