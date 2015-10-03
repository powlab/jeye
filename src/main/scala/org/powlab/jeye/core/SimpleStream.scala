package org.powlab.jeye.core

import java.io.DataInputStream

class SimpleStream(val dis: DataInputStream) {

  def readU1(): Int = dis.readUnsignedByte

  def readU1S(): Byte = dis.read().toByte

  def readU2(): Int = dis.readUnsignedShort

  def readU4(): Int = dis.readInt

  def readBytes(count: Int): Array[Byte] = {
    val buf: Array[Byte] = new Array[Byte](count)
    var wasRead: Int = 0
    while ( {
      wasRead += dis.read(buf, wasRead, count - wasRead)
      wasRead
    } != count) {
      ;
    }
    buf
  }

}
