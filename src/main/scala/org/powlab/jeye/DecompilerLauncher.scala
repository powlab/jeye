package org.powlab.jeye

import java.io._
import java.nio.file.Paths

import org.powlab.jeye.decode.Formater
import org.powlab.jeye.decode.decoders.Decoder
import org.powlab.jeye.decode.expression.Expressions
import org.powlab.jeye.utils.{ DecompileIterator, Package }

object DecompilerLauncher extends App {

  case class Config(file: Option[File] = None,
    out: Option[File] = None,
    err: Option[File] = None)

  val defaultFile = "output.txt"
  val DEFAULT_ERROR_FILE = new File("output_errors.txt")

  val parser = new scopt.OptionParser[Config]("jeye") {
    head("jeye", "0.x")
    opt[File]('f', "file") required () action { (x, c) =>
      c.copy(file = Some(x))
    } text "Input file"
    opt[File]('o', "out") required () action { (x, c) =>
      c.copy(out = Some(x))
    } text "Output file"
    opt[File]('e', "errors file") optional () action { (x, c) =>
      c.copy(err = Some(x))
    } text "Error file"
    note("Java Decompiler")
    help("help") text "prints this usage text"
  }

  parser.parse(args, Config()).foreach { config: DecompilerLauncher.Config =>
    val time = System.currentTimeMillis()
    val rootPackage = Package(DecompileIterator.from(config.file.get))
    println("tree building time: " + (System.currentTimeMillis() - time))
    val out = new File(if (config.out.isDefined) config.out.get.getAbsolutePath else defaultFile)
    out.mkdir()
    rootPackage.allClasses.foreach { clazz =>
      try {
        val body = Formater.format(Decoder.decode(clazz), Expressions.EMPTY_EXPRESSION)
        val lines = body.split("\n")
        val path = lines(0)
        val packageName = path.substring("package ".length, path.length - 1)
        val packageNames = packageName.split('.')
        val packageDir = Paths.get(out.getAbsolutePath, packageNames: _*).toFile
        packageDir.mkdirs()
        val bw = new BufferedWriter(new FileWriter(Paths.get(packageDir.getAbsolutePath, clazz.name.concat(".java")).toFile))
        try {
          bw.write(body)
        } finally {
          bw.close()
        }
      } catch {
        case ex: Exception => ex.printStackTrace()
      }
    }
    println("time: " + (System.currentTimeMillis() - time))
    println("tree: " + rootPackage)
  }
}
