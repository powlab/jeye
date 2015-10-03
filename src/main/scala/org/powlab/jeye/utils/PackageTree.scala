package org.powlab.jeye.utils

import java.io.{DataInputStream, InputStream}

import org.powlab.jeye.core._
import org.powlab.jeye.core.SimpleStream
import org.powlab.jeye.core.parsing.ClassFileFormatParser

import scala.collection.mutable.ListBuffer

object Package {

  case class PackageBuilder(name: String, nodes: ListBuffer[Node]) extends Node

  case class ClazzBuilder(name: String, classFile: ClassFile, innerClasses: ListBuffer[ClazzBuilder]) extends Node

  def apply(iterator: Iterator[FileElement]): Package = {
    val rootPackageBuilder = PackageBuilder("", new ListBuffer[Node]())
    while (iterator.hasNext) {
      val entry = iterator.next()
      if (entry.isDirectory) {
        val packages = entry.path.split("/")
        packages.foldLeft(rootPackageBuilder)(reducePackages)
      } else if (entry.path.endsWith(".class")) {
        val classFile = ClassFileFormatParser.parse(new SimpleStream(new DataInputStream(entry.stream)))
        entry.stream.close() //@TODO Обыграть и закрывать стрим нормально
        val informator = classFile.constantPoolUtils.getClassInformator(classFile.this_class)
        val elements = informator.javaName.split("\\.")
        val clazz = elements.last
        val packages = elements.init
        val pack = packages.foldLeft(rootPackageBuilder)(reducePackages)
        pack.nodes += ClazzBuilder(clazz, classFile, ListBuffer[ClazzBuilder]())
      } else {
        val elements = entry.path.split("/")
        val resource = elements.last
        val packages = elements.init
        val pack = packages.foldLeft(rootPackageBuilder)(reducePackages)
        pack.nodes += Resource(resource, entry.stream)
      }
    }

    buildInnerClasses(rootPackageBuilder)

    buildPackage(rootPackageBuilder)
  }

  private def reducePackages(first: PackageBuilder, second: String): PackageBuilder =
    first.nodes.filter(_.isInstanceOf[PackageBuilder]).map(_.asInstanceOf[PackageBuilder]).find(_.name == second) match {
      case Some(p: PackageBuilder) => p
      case None => val pack = PackageBuilder(second, new ListBuffer[Node]())
        first.nodes += pack
        pack
    }

  private def buildPackage(builder: PackageBuilder): Package = Package(builder.name, builder.nodes.map({
    case builder: PackageBuilder => buildPackage(builder)
    case builder: ClazzBuilder => buildClass(builder)
    case other => other
  }).toList)

  private def buildClass(builder: ClazzBuilder): Clazz = Clazz(builder.name, builder.classFile, builder.innerClasses.collect {
    case builder: ClazzBuilder => buildClass(builder)
  }.toList)

  private def buildInnerClasses(root: PackageBuilder): Unit = {
    def innerClasses(classFile: ClassFile): Array[String] = {
      val cpUtils = classFile.constantPoolUtils
      val name = cpUtils.getClassInformator(classFile.this_class).javaName.split("\\.").last
      classFile.attributes.filter(_.isInstanceOf[InnerClassesAttribute]).flatMap {
        case attribute: InnerClassesAttribute => attribute.classes.filter(clazz => {
          val innerName = cpUtils.getClassInformator(clazz.inner_class_info_index).javaName.split("\\.").last
          clazz.outer_class_info_index == classFile.this_class ||
            (clazz.outer_class_info_index == 0 && innerName.startsWith(name)) &&
              clazz.inner_class_info_index != classFile.this_class
        }).map(clazz => {
          cpUtils.getClassInformator(clazz.inner_class_info_index).javaName.split("\\.").last
        })
      }
    }

    def findInnerClasses(clazz: ClazzBuilder) = {
      val inners = innerClasses(clazz.classFile)
      inners.foreach(inner => {
        findClassByName(inner, root.nodes) foreach {
          c =>
            clazz.innerClasses += c
            root.nodes -= c
        }
      })
    }
    def findClassByName(name: String, nodes: ListBuffer[Node]): Option[ClazzBuilder] = {
      nodes.filter(clazz => clazz.isInstanceOf[ClazzBuilder]).map(_.asInstanceOf[ClazzBuilder]).find(_.name == name)
    }

    root.nodes.filter(_.isInstanceOf[ClazzBuilder]).collect {
      case clazz: ClazzBuilder => findInnerClasses(clazz)
    }

    root.nodes.filter(_.isInstanceOf[PackageBuilder]).collect {
      case pack: PackageBuilder => buildInnerClasses(pack)
    }
  }

}

sealed trait Node

object PackageTree {

  def findClassByFullName(root: Package, name: String): Option[Clazz] = {
    val tokens = name.split("\\.")
    findClassByNameAndPackage(root, tokens.last, tokens.init.mkString("."))
  }

  def findClassByNameAndPackage(root: Package, name: String, pack: String): Option[Clazz] = {
    val path = pack.split("\\.").toList.iterator
    findPack(root, path.next(), path) match {
      case Some(p) => p.classes.find(_.name == name)
      case None => None
    }
  }

  private def findPack(pack: Package, packToken: String, packIterator: Iterator[String]): Option[Package] = {
    pack.packages.find(_.name == packToken) match {
      case Some(p) => if (packIterator.hasNext) {
        findPack(p, packIterator.next(), packIterator)
      } else Some(p)
      case None => None
    }
  }

}

case class Package(name: String, nodes: List[Node]) extends Node {
  lazy val packages: List[Package] = nodes.filter(_.isInstanceOf[Package]).map(_.asInstanceOf[Package])
  lazy val resources: List[Resource] = nodes.filter(_.isInstanceOf[Resource]).map(_.asInstanceOf[Resource])
  lazy val classes: List[Clazz] = nodes.filter(_.isInstanceOf[Clazz]).map(_.asInstanceOf[Clazz])

  lazy val allClasses: List[Clazz] = classes ++ packages.flatMap(_.allClasses)
}

case class Clazz(name: String, classFile: ClassFile, innerClasses: List[Clazz] = List()) extends Node

case class Resource(name: String, stream: InputStream) extends Node
