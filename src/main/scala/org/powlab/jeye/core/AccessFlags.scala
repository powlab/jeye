package org.powlab.jeye.core

import java.util.IdentityHashMap
import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer

/**
 * Модификаторы
 */
object AccessFlags {

  val ACC_PUBLIC        = Flag("public",        0x0001)
  val ACC_PRIVATE       = Flag("private",       0x0002)
  val ACC_PROTECTED     = Flag("protected",     0x0004)
  val ACC_STATIC        = Flag("static",        0x0008)
  val ACC_FINAL         = Flag("final",         0x0010)
  val ACC_SUPER         = Flag("super",         0x0020)
  val ACC_SYNCHRONIZED  = Flag("synchronized",  0x0020)
  val ACC_VOLATILE      = Flag("volatile",      0x0040)
  val ACC_BRIDGE        = Flag("bridge",        0x0040)
  val ACC_TRANSIENT     = Flag("transient",     0x0080)
  val ACC_VARARGS       = Flag("varargs",       0x0080)
  val ACC_NATIVE        = Flag("native",        0x0100)
  val ACC_INTERFACE     = Flag("interface",     0x0200)
  val ACC_ABSTRACT      = Flag("abstract",      0x0400)
  val ACC_STRICT        = Flag("strictfp",      0x0800)
  val ACC_SYNTHETIC     = Flag("synthetic",     0x1000)
  val ACC_ANNOTATION    = Flag("@interface",    0x2000)
  val ACC_ENUM          = Flag("enum",          0x4000)

  private val FLAGS = Array[Flag](ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_SUPER, ACC_SYNCHRONIZED,
      ACC_VOLATILE, ACC_BRIDGE, ACC_TRANSIENT, ACC_VARARGS, ACC_NATIVE, ACC_INTERFACE, ACC_ABSTRACT, ACC_STRICT,
      ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM)

  private val CLASS_FLAGS = new FlagGroup(StructureScope.CLASS_SCOPE,
      List[Flag](ACC_PUBLIC, ACC_FINAL, ACC_SUPER, ACC_INTERFACE, ACC_ABSTRACT, ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM))

  private val FILED_FLAGS = new FlagGroup(StructureScope.FIELD_SCOPE,
      List[Flag](ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_VOLATILE, ACC_TRANSIENT, ACC_SYNTHETIC, ACC_ENUM))

  private val METHOD_FLAGS = new FlagGroup(StructureScope.METHOD_SCOPE,
      List[Flag](ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED, ACC_BRIDGE, ACC_VARARGS,
                  ACC_NATIVE, ACC_ABSTRACT, ACC_STRICT, ACC_SYNTHETIC))

  private val INNER_CLASS_FLAGS = new FlagGroup(StructureScope.INNER_CLASS_SCOPE,
      List[Flag](ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_INTERFACE, ACC_ABSTRACT,
                  ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM))

  val SCOPE2FLAGS = new IdentityHashMap[StructureScope, FlagGroup]
  SCOPE2FLAGS.put(StructureScope.CLASS_SCOPE, CLASS_FLAGS)
  SCOPE2FLAGS.put(StructureScope.FIELD_SCOPE, FILED_FLAGS)
  SCOPE2FLAGS.put(StructureScope.METHOD_SCOPE, METHOD_FLAGS)
  SCOPE2FLAGS.put(StructureScope.INNER_CLASS_SCOPE, INNER_CLASS_FLAGS)

  def getFlags(scope: StructureScope, accessFlags: Int) =
    (for (flag <- SCOPE2FLAGS.get(scope).flags if (accessFlags & flag.code) == flag.code) yield flag).sortWith(_.code < _.code)

  type FlagAcceptor = (Flag) => Boolean

  def getVisibleFlags(scope: StructureScope, accessFlags: Int, accept: FlagAcceptor = null):List[Flag] = {
    val flags = getFlags(scope, accessFlags)
    flags.filter(flag => {
      !(flag == ACC_SUPER || flag == ACC_BRIDGE || flag == ACC_VARARGS || flag == ACC_SYNTHETIC || flag == ACC_ENUM ||
          (flag == ACC_FINAL && flags.contains(ACC_ENUM)) ||
          (flag == ACC_ABSTRACT && flags.contains(ACC_INTERFACE))
       ) && (accept == null || accept(flag))
    })
  }

  def isEnum(accessFlags: Int) = is(accessFlags, ACC_ENUM)

  def isAnnotation(accessFlags: Int) = is(accessFlags, ACC_ANNOTATION)

  def isStatic(accessFlags: Int): Boolean = is(accessFlags, ACC_STATIC)

  def isAbstract(accessFlags: Int): Boolean = is(accessFlags, ACC_ABSTRACT)

  def isSynthetic(accessFlags: Int): Boolean = is(accessFlags, ACC_SYNTHETIC)

  def isVarargs(accessFlags: Int): Boolean = is(accessFlags, ACC_VARARGS)

  def isInterface(accessFlags: Int): Boolean = is(accessFlags, ACC_INTERFACE)

  def not(accessFlags: Int, flag: Flag): Boolean = !is(accessFlags, flag)

  def is(accessFlags: Int, flag: Flag): Boolean = (accessFlags & flag.code) == flag.code

}

case class Flag(title: String, code: Int)
case class FlagGroup(scope: StructureScope, flags: List[Flag])

