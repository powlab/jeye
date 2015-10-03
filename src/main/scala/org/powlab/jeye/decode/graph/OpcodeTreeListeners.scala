package org.powlab.jeye.decode.graph

object OpcodeTreeListeners {

  /** При создании новой группы id группы меняется и те кто указывали на старую группу,
   *  должны теперь указывать на новую
   */
  trait IdChanger {
    def change(oldId: String, newId: String)
  }

}