package org.powlab.jeye.utils

object StringWordParser {

  type WordHandler = String => Boolean

  def parse(value: String, delims: Array[Character], handler: WordHandler) {
    if (value == null || value.isEmpty) {
      return;
    }
    var preview = 0;
    var index = 0;
    while (index < value.length) {
      val symbol = value(index)
      if (delims.contains(symbol)) {
        if (preview != index && handler(value.substring(preview, index))) {
          return
        }
        preview = index + 1
      }
      index += 1
    }
    if (preview != index) {
      handler(value.substring(preview, index))
    }
  }

  def parseBack(value: String, delims: Array[Character], handler: WordHandler) {
    if (value == null || value.isEmpty) {
      return;
    }
    var preview = value.length - 1;
    var index = preview;
    while (index >= 0) {
      val symbol = value(index)
      if (delims.contains(symbol)) {
        if (preview != index && handler(value.substring(index + 1, preview + 1))) {
          return
        }
        preview = index - 1
      }
      index -= 1
    }
    if (preview != index) {
      handler(value.substring(index + 1, preview + 1))
    }
  }
}
