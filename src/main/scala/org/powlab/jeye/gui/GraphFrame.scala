package org.powlab.jeye.gui

import java.awt.{AWTEvent, BorderLayout, Color, Component, Dimension, Graphics, Image, Toolkit}
import java.awt.event.{AWTEventListener, KeyEvent, MouseEvent}
import java.awt.image.BufferedImage
import java.io.{DataInputStream, File, FileInputStream}
import javax.swing.{JFrame, JPanel}
import org.powlab.jeye.core._
import org.powlab.jeye.core.SimpleStream
import org.powlab.jeye.core.parsing.ClassFileFormatParser
import org.powlab.jeye.decode.Formater
import org.powlab.jeye.decode.RuntimeOpcodes
import org.powlab.jeye.decode.RuntimeOpcodes._
import org.powlab.jeye.decode.decoders.MethodBodyDecoder
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.{OpcodeGraph, OpcodeTreeHelper}
import org.powlab.jeye.decode.sids.SidProcessor
import org.powlab.jeye.utils.{AttributeUtils, DecodeUtils}
import scala.collection.mutable.{ArrayBuffer, Buffer, Map}
import org.powlab.jeye.decode.decoders.ExtraInfo
import org.powlab.jeye.decode.ClassFacade
import org.powlab.jeye.decode.ClassFacades
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.expression.Expressions.EMPTY_EXPRESSION

object GraphFrame extends App {

  //TODO here: SET PROJECT ROOT HERE!
  val projectRoot = new File("").getAbsolutePath();
  //val testCompiledRoot = projectRoot + "/target/test-classes";
  val testCompiledRoot = projectRoot + "/target/classes";
  var classIndex: Int = 5;
  val CLASS_NAME = "Sample"
  val METHODS = Array("if", "switch", "synch", "try", "cycle", "if", "", "type", "any")
  var METHOD_NAME = METHODS(classIndex - 1);
  var methodIndex = 'f'
  var mode: Int = 1
  var compactTop: OpcodeNode = null
  var dSid: Boolean = false;

  val WIDTH = 1200
  val HEIGHT = 900
  val VERTEX_WIDTH_SHIFT = 70
  val VERTEX_HEIGHT_SHIFT = 30

  val toolkit = Toolkit.getDefaultToolkit()
  val screenSize = toolkit.getScreenSize()
  val startX = (screenSize.width - WIDTH) >> 1
  val startY = (screenSize.height - HEIGHT) >> 1

  private def createFrame() {
    val frame = new JFrame("Модель OpCode");
    frame.setLocation(0, startY);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    val contentPane = frame.getContentPane();
    val image = createImage
    val action = new KeyAction(image, frame);
    action.loadClass();
    action.process(methodIndex)
    val toolkit = Toolkit.getDefaultToolkit();
    toolkit.addAWTEventListener(action, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
    val holst = new ImagedPanel(image);
    //val button = new JButton("sample");
    contentPane.add(holst, BorderLayout.CENTER);

    frame.pack();

    frame.setSize(new Dimension(WIDTH, HEIGHT))
    frame.setVisible(true);
  }

  private class KeyAction(image: Image, comp: Component) extends AWTEventListener {
    var sourceFile: String = null
    var parsedClass: ClassFile = null

    def loadClass() {
      sourceFile = projectRoot + "/src/test/java/org/powlab/jeye/scenario/data/" + CLASS_NAME + classIndex + ".java"
      val sourceClass = testCompiledRoot + "/org/powlab/jeye/scenario/data/" + CLASS_NAME + classIndex + ".class"
      val stream = new SimpleStream(new DataInputStream(new FileInputStream(sourceClass)))
      parsedClass = ClassFileFormatParser.parse(stream)
      ClassFacades.registry(parsedClass , false)
    }

    loadClass()

    def eventDispatched(awtEvent: AWTEvent) {
      if (awtEvent.isInstanceOf[KeyEvent]) {
        val keyEvent = awtEvent.asInstanceOf[KeyEvent];
        if (keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.isActionKey() && keyEvent.getKeyCode() > 111 && keyEvent.getKeyCode() < 122) {
          val index = (keyEvent.getKeyCode() - 111)
          classIndex = index
          loadClass()
          METHOD_NAME = METHODS(index - 1)
          process('1')
          comp.repaint()
        } else if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
          val char = keyEvent.getKeyChar()
          if (char == 'z' || char == 'x' || char == 'Z' || char == 'X' || char == 'c' || char == 'C') {
            mode = if (char == 'z' || char == 'Z') 0 else if (char == 'x' || char == 'X') 1 else 2
          } else if (char == 'v' || char == 'V') {
            dSid = !dSid
          } else {
            methodIndex = char
          }

          process(methodIndex)
          comp.repaint()
        }
      } else if (awtEvent.isInstanceOf[MouseEvent]) {
        val mouseEvent = awtEvent.asInstanceOf[MouseEvent];
        val x = mouseEvent.getX
        val y = mouseEvent.getY
      }
    }

    def process(char: Char) {
      val methods = parsedClass.methods
      val methodName = METHOD_NAME + char
      methods.foreach(method => {
        val cpUtils = parsedClass.constantPoolUtils
        val name = cpUtils.getUtf8(method.name_index)
        if (name == methodName) {
          val codeAttribute = AttributeUtils.get[CodeAttribute](method.attributes)
          val runtimeOpcodes = RuntimeOpcodes.parseRuntimeOpcodes(codeAttribute.code)
          var roText = ""
          runtimeOpcodes.foreach(runtimeOpcode => roText += runtimeOpcodeToString(runtimeOpcode, cpUtils, 2) + "\n")
          val methodDecoder = new MethodBodyDecoder(parsedClass)
          val extendedContext = new ExtraInfo(ClassFacades.get(parsedClass))
          val expr = methodDecoder.decode(method, extendedContext)
          println(Formater.format(expr, EMPTY_EXPRESSION))
          val graph = methodDecoder.graph
          drawGraph(graph, image)
          drawMethodText(image, parseMethodText(methodName), roText)
        }
      })
    }

    private def parseMethodText(methodName: String): String = {
      val source = scala.io.Source.fromFile(sourceFile)
      val lines = source.mkString
      source.close
      val methodStartIndex = lines.indexOf(methodName);
      val methodLineStart = lines.lastIndexOf("\n", methodStartIndex) + 1;
      var index = lines.indexOf("{", methodStartIndex) + 1;
      var depth = 1;
      while (depth != 0 && index < lines.length()) {
        val symbol = lines.charAt(index);
        if (symbol == '{') {
          depth += 1
        } else if (symbol == '}') {
          depth -= 1
        }
        index += 1
      }
      lines.substring(methodLineStart, index)
    }
  }

  private class ImagedPanel(image: Image) extends JPanel {
    override def paintComponent(g: Graphics) {
      super.paintComponent(g);
      if (image != null) {
        g.drawImage(image, 0, 0, this);
      }
    }
  }

  // TODO here: сделать масштабирование
  private def createImage(): Image = {
    val image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
    //Draw into it.
    val g = image.getGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, WIDTH, HEIGHT);

    drawHead(g);
    //Clean up.
    g.dispose();
    image
  }

  private def drawHead(g: Graphics) {
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, WIDTH >> 1, 40);
    g.setColor(Color.WHITE);
    g.drawString("Current Class: " + CLASS_NAME + classIndex, 10, 15);
    g.drawString("Select class[F1, F2, F3, F4, F5]", 10, 35);
    g.drawString("Select method[1, 2, 3, 4, 5, 6, 7, 8, 9, 0, q]", 10, 55);
    g.drawString("Select mode[z, x, c, v]", 10, 75);
  }

  private def drawMethodText(image: Image, methodText: String, runtimeOpocdes: String) {
    val g = image.getGraphics();
    val heightStep = g.getFont().getSize() + 5
    val x = 10
    var y = 95
    def drawText(line: String) {
      g.drawString(line, x, y)
      y += heightStep
    }
    g.setColor(Color.RED);
    g.drawString("Исходный текст: ", x, y)
    y += heightStep
    g.setColor(new Color(210, 210, 210));
    methodText.split("\n").foreach(drawText)
    drawText("\n")

    //runtimeOpocdes.split("\n").foreach(drawText)

    //Clean up.
    g.dispose();
  }

  private def drawGraph(graph: OpcodeGraph, image: Image) {
    val tree = if (mode == 0) graph.getNativeTree else graph.getResultTree
    if (mode == 0) {
      tree.selector = SidProcessor.draftSelector(tree)
    }
    val numberToNode = Map[String, OpcodeNode]()
    val numberToIndex = Map[String, Int]()
    val numberToVertex = Map[String, Vertex]()
    val vertexes = new ArrayBuffer[Vertex]()
    val edges = new ArrayBuffer[Edge]()

    OpcodeTreeHelper.scanTree(tree, (node => numberToNode.put(node.id, node)))
    var index = 0;
    numberToNode.keySet.toList.sortWith((left, right) => {
      new Idable(left).less(new Idable(right))
    }).foreach(number => {
      numberToIndex.put(number, index)
      index += 1
    })

    def getDisplayedText(node: OpcodeNode): String = {
      def getText(): String = {
        if (mode == 2) {
          return if (tree.details(node).expression == null) "null" else tree.details(node).expression.view(EMPTY_EXPRESSION)
        }
        if (!node.isInstanceOf[GroupOpcodeNode]) {
          return DecodeUtils.pad(node.id, 2) + " " + node.runtimeOpcode.opcode.name
        }
        val groupNode = node.asInstanceOf[GroupOpcodeNode]
        var nodes = new ArrayBuffer[OpcodeNode]
        nodes ++= groupNode.opcodes
        nodes.map(node => {
          DecodeUtils.pad(node.id, 2) + " " + node.runtimeOpcode.opcode.name
        }).mkString(", ")
      }
      var dText = getText
      if (dSid) {
        dText += " (" + tree.details(node).sid + ")"
      }
      dText
    }
    def buildVertex(node: OpcodeNode, x: Var): Vertex = {
      val number = node.id
      var pointY: Int = (numberToIndex(number) + 1) * VERTEX_HEIGHT_SHIFT
      val text = getDisplayedText(node)
      var vertex = new Vertex(text, x, pointY)
      vertex
    }
    val keepNodesMap = Map[String, OpcodeNode]()
    def buildNumberToVertex(top: OpcodeNode, x: Var, keepNodes: ArrayBuffer[OpcodeNode]) {

      def acceptNodes(nodes: Buffer[OpcodeNode]): Boolean = {
        return true;
      }

      var node = top
      while (node != null) {
        val number = node.id
        val walked = numberToVertex.getOrElse(number, null)
        if (walked != null) {
          //walked.x.change(x.getInt - walked.x.getInt)
          return
        }
        if (keepNodes != null && tree.previewCount(node) > 1 && acceptNodes(tree.previews(node))) {
          var keeped = keepNodesMap.getOrElse(number, null)
          if (keeped == null) {
            keepNodesMap.put(number, node)
            keepNodes += node
          }
          return
        }
        var vertex = buildVertex(node, x /*new Var(x, x.cat)*/);
        vertexes += vertex
        numberToVertex.put(number, vertex)
        if (node.branchy) {
          var step = VERTEX_WIDTH_SHIFT
          val nexCount = tree.nextCount(node)
          var shift = -(nexCount / 2) * VERTEX_WIDTH_SHIFT
          if (x.isRight) {
            x.change(VERTEX_WIDTH_SHIFT)
            if (nexCount == 1) {
              step = -step
              shift = -shift
            }
          }
          if (x.isLeft) {
            x.change(-VERTEX_WIDTH_SHIFT)
          }
          var localKeepNodes = new ArrayBuffer[OpcodeNode]()
          type nodeToVar = (OpcodeNode, Var)
          tree.nexts(node).foreach(childNode => {
            if (shift == 0) {
              shift += step
            }
            val leftX = new Var(x, shift)
            leftX.change(shift)
            buildNumberToVertex(childNode, leftX, localKeepNodes)
            shift += step
          })
          if (!localKeepNodes.isEmpty) {
            localKeepNodes.foreach(localKeepNode => {
              var minY = vertex.y
              var findX: Var = null;
              tree.previews(localKeepNode).foreach(owner => {
                val ownerNumber = owner.id
                val ownerVertex = numberToVertex.getOrElse(ownerNumber, null)
                if (ownerVertex != null && ownerVertex.y < minY) {
                  minY = ownerVertex.y
                  findX = ownerVertex.x
                }
              })
              if (findX == null) {
                findX = x;
              }
              buildNumberToVertex(localKeepNode, findX, null)
            })
          }
          return
        }
        node = tree.next(node)
      }
    }
    buildNumberToVertex(tree.head, new Var((WIDTH >> 1) + 0, 0), null)
    def addEdge(node: OpcodeNode) {
      val vertex = numberToVertex(node.id)
      if (tree.previewCount(node) == 1 && numberToVertex.contains(tree.preview(node).id)) {
        val vertexP = numberToVertex(tree.preview(node).id);
        edges += new Edge(vertexP, vertex)
      } else if (tree.previewCount(node) > 1) {
        tree.previews(node).foreach(preview => {
          val vertexP = numberToVertex.getOrElse(preview.id, null);
          if (vertexP != null) {
            val edge = new Edge(vertexP, vertex)
            edges += edge
          }
        })
      }
    }
    OpcodeTreeHelper.scanTree(tree, node => addEdge(node))

    val g = image.getGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, WIDTH, HEIGHT);

    drawHead(g);
    edges.foreach(edge => edge.draw(g))
    vertexes.foreach(vertex => vertex.draw(g))

    //Clean up.
    g.dispose();
  }

  case class Var(data: Any, cat: Int) {
    private var offset = 0

    def getInt(): Int = {
      if (data.isInstanceOf[Int]) offset + data.asInstanceOf[Int]
      else offset + data.asInstanceOf[Var].getInt
    }

    def change(value: Int) {
      offset += value
    }

    def isLeft() = {
      cat < 0
    }

    def isRight() = {
      cat > 0
    }
  }

  private class Vertex(text: String, var x: Var, val y: Int) {
    def draw(g: Graphics) {
      val font = g.getFont()
      val metrics = toolkit.getFontMetrics(font)
      val textWidth: Int = metrics.stringWidth(text)
      val textHeight: Int = font.getSize()
      val width: Int = (1.4 * textWidth).toInt
      val height: Int = (1.4 * textHeight).toInt
      val startX = x.getInt - (width >> 1)
      val startY = y - (height >> 1)
      g.setColor(Color.GRAY);
      g.fillOval(startX, startY, width, height)
      g.setColor(Color.RED);
      g.drawOval(startX, startY, width, height)
      g.setColor(Color.WHITE);
      g.drawString(text, startX + (width - textWidth) / 2, startY + textHeight + (height - textHeight) / 2)
    }

    override def toString() = {
      (text, x, y).toString
    }
  }

  private val UP_COLOR = new Color(150, 150, 255)

  private class Edge(from: Vertex, to: Vertex) {
    def draw(g: Graphics) {
      g.setColor(Color.YELLOW);
      if (from.y > to.y) {
        g.setColor(UP_COLOR);
      }
      g.drawLine(from.x.getInt, from.y, to.x.getInt, to.y)
    }

    override def toString() = {
      (from.x.getInt, from.y, to.x.getInt, to.y).toString
    }
  }

  private class Leafs(val fromLeft: Int, val fromRight: Int) {
    override def toString() = {
      "fromLeft = " + fromLeft + ", fromRight = " + fromRight
    }
  }

  private class Idable(id: String) {
    val index = id.indexOf("#")
    val number = (if (index != -1) id.substring(0, index) else id).toInt
    val subNumber = if (index != -1) id.substring(0, index).toInt else 0

    def less(idable: Idable): Boolean = {
      number < idable.number || (number == idable.number && subNumber > idable.subNumber)
    }
  }

  def launch() {
    // sart
    javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
      def run() {
        createFrame();
      }
    });
  }

  launch()

}
