package org.powlab.jeye.utils

import java.io.{File, InputStream}
import java.nio.file.{Files, Paths}

import scala.util.Random

//@FIXME додумать нужны ли эти классы
trait ClassAccessor {
  def getStream(className: String): InputStream
}

class FSAccessor(iterator: Iterator[FileElement]) extends ClassAccessor {

  val prefix = Random.alphanumeric.take(10).mkString
  val tmpPath = Files.createTempDirectory(prefix)

  while (iterator.hasNext) {
    val item = iterator.next()
    val path = Paths.get(tmpPath + File.separator + item.path)
    Files.createDirectories(path.getParent)
    Files.copy(item.stream, path)
    item.stream.close()
  }

  def getStream(className: String): InputStream = {
    val classPath = className.replace(".", File.separator)
    val tempPath = Paths.get(tmpPath + File.separator + classPath + ".class")
    Files.newInputStream(tempPath)
  }

}

class InMemoryAccessor(iterator: Iterator[FileElement]) extends ClassAccessor {

  val list = iterator.toList

  def getStream(className: String): InputStream = {
    list.find(item => item.path == className.replace(".", "/") + ".class") match {
      case Some(source) => source.stream
      case None => throw new RuntimeException("Все плохо")
    }
  }

}
