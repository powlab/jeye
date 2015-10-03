package org.powlab.jeye.utils

import java.io._
import java.util.jar.JarFile
import java.util.zip.{ZipFile, ZipInputStream}

//@FIXME Здесь в итераторах нужно закрывать корректно entry архивов
case class FileElement(path: String, stream: InputStream, isDirectory: Boolean)

object DecompileIterator {

  object IteratorSourceType extends Enumeration {
    type IteratorSourceType = Value
    val JAR, ZIP, DIRECTORY, CLASS = Value

    def valueOf(file: File) = if (file.getName.endsWith(".zip")) {
      ZIP
    } else if (file.getName.endsWith(".class")) {
      CLASS
    } else if (file.isDirectory) {
      DIRECTORY
    } else if (file.getName.endsWith(".jar")) {
      JAR
    } else {
      throw new IllegalStateException("File type is not found")
    }
  }

  import org.powlab.jeye.utils.DecompileIterator.IteratorSourceType._

  def from(file: File): Iterator[FileElement] = from(file, IteratorSourceType.valueOf(file))

  def from(file: File, iteratorSourceType: IteratorSourceType): Iterator[FileElement] = iteratorSourceType match {
    case ZIP => new ZipIterator(file)
    case CLASS => new ClassFileIterator(file)
    case DIRECTORY => new DirectoryIterator(file)
    case JAR => new JarIterator(file)
  }

}

class DecompileIterator(file: File) extends Iterator[FileElement] {

  val DecompileIterator = if (file.getName.endsWith(".zip")) {
    new ZipIterator(file)
  } else if (file.getName.endsWith(".class")) {
    new ClassFileIterator(file)
  } else if (file.isDirectory) {
    new DirectoryIterator(file)
  } else if (file.getName.endsWith(".jar")) {
    new JarIterator(file)
  } else throw new IllegalStateException("file is not class")

  override def hasNext: Boolean = DecompileIterator.hasNext

  override def next(): FileElement = DecompileIterator.next()

}

class JarIterator(file: File) extends Iterator[FileElement] {
  val jar = new JarFile(file)
  val enumEntries = jar.entries()

  override def hasNext: Boolean = enumEntries.hasMoreElements

  override def next(): FileElement = {
    val entry = enumEntries.nextElement()
    val is = new BufferedInputStream(jar.getInputStream(entry))
    FileElement(entry.getName, is, entry.isDirectory)
  }
}

class DirectoryIterator(file: File) extends Iterator[FileElement] {
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  val iterator = recursiveListFiles(file).toIterator

  override def hasNext: Boolean = iterator.hasNext

  override def next(): FileElement = {
    val next = iterator.next()

    val path = next.getPath.substring(file.getAbsolutePath.length + 1).replace("\\", "/")
    if (!next.isDirectory) {
      val is = new BufferedInputStream(new FileInputStream(next))
      FileElement(path, is, next.isDirectory)
    } else {
      FileElement(path, null, next.isDirectory)
    }
  }
}

class ZipIterator(file: File) extends Iterator[FileElement] {
  var current: FileElement = null
  val zipFile = new ZipFile(file)
  val zin = new ZipInputStream(new FileInputStream(file))

  override def hasNext: Boolean = {
    val entry = zin.getNextEntry
    if (entry != null) {
      val is = zipFile.getInputStream(entry)
      current = FileElement(entry.getName, is, entry.isDirectory)
      zin.closeEntry()
      true
    } else {
      current = null
      zin.close()
      false
    }
  }

  override def next(): FileElement = current
}

class ClassFileIterator(file: File) extends Iterator[FileElement] {
  var isRead = false

  override def hasNext: Boolean = !isRead

  override def next(): FileElement = {
    isRead = true
    val is = new BufferedInputStream(new FileInputStream(file))
    FileElement(file.getName, is, file.isDirectory)
  }
}

