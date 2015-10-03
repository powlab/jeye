package org.powlab.jeye.core

import java.io.{ByteArrayInputStream, DataInputStream}
import java.nio.ByteBuffer

object Utils {
  def getCharFromByte(value: Byte): Character = value.asInstanceOf[Int].asInstanceOf[Char]

  def getBytesStream(bytes: Array[Byte]): SimpleStream = new SimpleStream(new DataInputStream(new ByteArrayInputStream(bytes)))

  def toShort(unsignedbyte1: Int, unsignedbyte2: Int): Short = {
    ((unsignedbyte1 << 8) | unsignedbyte2).asInstanceOf[Short]
  }

  def toInt(bytes: Array[Byte]): Int = ByteBuffer.wrap(bytes).getInt

  def toFloat(bytes: Array[Byte]): Float = ByteBuffer.wrap(bytes).getFloat

  def toFloatString(bytes: Array[Byte]): String = String.valueOf(toFloat(bytes)) + "f"

  def toDouble(highBytes: Array[Byte], lowBytes: Array[Byte]): Double = {
    val bytes: Array[Byte] = new Array[Byte](8)
    System.arraycopy(lowBytes, 0, bytes, 4, 4)
    System.arraycopy(highBytes, 0, bytes, 0, 4)
    ByteBuffer.wrap(bytes).getDouble
  }

  def toDoubleString(highBytes: Array[Byte], lowBytes: Array[Byte]): String = String.valueOf(toDouble(highBytes, lowBytes))

  def toLong(highBytes: Array[Byte], lowBytes: Array[Byte]): Long = {
    val bytes: Array[Byte] = new Array[Byte](8)
    System.arraycopy(lowBytes, 0, bytes, 4, 4)
    System.arraycopy(highBytes, 0, bytes, 0, 4)
    ByteBuffer.wrap(bytes).getLong
  }

  def toLongString(highBytes: Array[Byte], lowBytes: Array[Byte]): String = String.valueOf(toLong(highBytes, lowBytes)) + "L"

  def intToByte(bytic: Int): Byte = bytic.asInstanceOf[Byte]

  def intToShort(shortic: Int): Short = shortic.asInstanceOf[Short]
}
