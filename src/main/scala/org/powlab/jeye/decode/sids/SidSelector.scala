package org.powlab.jeye.decode.sids

import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.OpcodeTree
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import org.powlab.jeye.decode.graph.OpcodeNode

/**
 * Связь сида и инструкции
 */
class SidSelector(sid2Node: Map[String, OpcodeNode]) {

  def current(sid: String): OpcodeNode = {
    sid2Node.getOrElse(sid, null)
  }

  def contains(sid: String): Boolean = sid2Node.contains(sid)

  def next(currentSid: String): OpcodeNode = {
    val sid = new Sid(currentSid)
    sid2Node.getOrElse(sid.nextId, null)
  }

  def clear(currentSid: String): Boolean = {
    sid2Node.remove(currentSid).isDefined
  }

  def preview(currentSid: String): OpcodeNode = {
    val sid = new Sid(currentSid)
    sid2Node.getOrElse(sid.previewId, null)
  }

  def last(currentSid: String): OpcodeNode = {
    var lastNode: OpcodeNode = null
    if (contains(currentSid)) {
      var sid = new Sid(currentSid)
      while (contains(sid.sid)) {
        lastNode = sid2Node(sid.sid)
        sid = sid.nextSid
      }
    }
    lastNode
  }

}