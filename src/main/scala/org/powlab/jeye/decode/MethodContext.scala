package org.powlab.jeye.decode

import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression._
import scala.collection.mutable.ListBuffer
import org.powlab.jeye.decode.LocalVariableStore.LocalVariableStore
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.decoders.ExtraInfo

case class MethodContext(classFile: ClassFile, method: MemberInfo,
                         frames: FrameStack, localVariables: LocalVariableStore, namer: Namer,
                         tree: OpcodeTree, draft: Boolean,
                         extra: ExtraInfo, trace: LocalVariableTrace) {
}