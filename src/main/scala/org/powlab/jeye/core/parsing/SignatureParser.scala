package org.powlab.jeye.core.parsing

import org.powlab.jeye.core._
import org.powlab.jeye.core.Constants._
import org.powlab.jeye.core.Signatures._
import org.powlab.jeye.core.Types._

import scala.collection.mutable.ArrayBuffer

//@TODO Некорректно парсятся выражения вида int[] int1(и другие массивы примитивов), нужно поправить
object SignatureParser {

  private class SymbolParser(signature: String) {
    val stops = Array[Char](':', '>', '<', '.', ';', '\0')
    var position = 0

    def valid(): Boolean = {
      (position < signature.length())
    }

    def getSymbol(): Char = {
      valid() match {
        case true => signature.charAt(position)
        case false => '\0'
      }
    }

    def getSignature(from: Int): String = {
      signature.substring(from, position)
    }

    def moveOne() = {
      position += 1
      position
    }

    def getPosition(): Int = {
      position
    }

    def moveToStop(): Char = {
      while (!isStopSymbol) {
        moveOne()
      }
      getSymbol()
    }

    def skipIfMatch(symbol: Char): Boolean = {
      if (getSymbol == symbol) {
        moveOne
        return true
      }
      false
    }

    def moveUntil(symbol: Char): Int = {
      while (getSymbol == symbol) {
        moveOne()
      }
      position
    }

    def isStopSymbol(): Boolean = {
      stops.contains(getSymbol)
    }

  }

  def parseSignature(signature: String, scope: StructureScope): IType = {
    val parser = new SymbolParser(signature)

    def parseIdentifier(): String = {
      val start = parser.getPosition()
      val stopBy = parser.moveToStop()
      val identifier = parser.getSignature(start)
      parser.skipIfMatch(';')
      identifier
    }

    def parseTypeArgument(): TypeArgument = {
      val start = parser.getPosition()
      val wildcard = parser.getSymbol()
      if (parser.skipIfMatch(WILDCARD_ASTERISK)) {
        return new TypeArgument(wildcard, null, wildcard.toString())
      } else {
        parser.skipIfMatch(WILDCARD_MINUS)
        parser.skipIfMatch(WILDCARD_PLUS)
        return new TypeArgument(wildcard, parseFieldTypeSignature(), wildcard.toString())
      }
    }

    def parseTypeArguments(): Array[TypeArgument] = {
      val argTypes = ArrayBuffer[TypeArgument]()
      if (parser.skipIfMatch('<')) {
        while (parser.valid() && !parser.skipIfMatch('>')) {
          argTypes += parseTypeArgument()
        }
        parser.moveOne(); // skip ';'
      }
      argTypes.toArray
    }

    def parseClassTypeSignatureSuffixes(): Array[ClassTypeSignatureSuffix] = {
      val suffixes = ArrayBuffer[ClassTypeSignatureSuffix]()
      while (parser.skipIfMatch('.')) {
        val start = parser.getPosition
        // skip '.'
        val identifier = parseIdentifier()
        val typeArguments = parseTypeArguments()
        val suffix = new ClassTypeSignatureSuffix(identifier, typeArguments, parser.getSignature(start))
        suffixes += suffix
      }
      suffixes.toArray
    }

    def parseClassTypeSignature(): ClassTypeSignature = {
      val start = parser.moveOne()
      // skip 'L'
      val identifier = parseIdentifier()
      val typeArguments = parseTypeArguments()
      val suffixes = parseClassTypeSignatureSuffixes()
      new ClassTypeSignature(identifier, typeArguments, suffixes, parser.getSignature(start))
    }

    def parseArrayTypeSignature(): ArrayTypeSignature = {
      val start = parser.getPosition()
      val dimension = parser.moveUntil(TYPE_ARRAY_CHAR) - start
      val componentType = parseFieldType()
      new ArrayTypeSignature(dimension, componentType, parser.getSignature(start))
    }

    def parseTypeSignature(): IType = {
      val symbol = parser.getSymbol()
      symbol match {
        case TYPE_REFERENCE_CHAR
             | TYPE_ARRAY_CHAR
             | TYPE_IDENTIFIER_CHAR => parseFieldType()
        case TYPE_ARRAY_CHAR
             | TYPE_IDENTIFIER_CHAR
             | TYPE_BOOLEAN_CHAR
             | TYPE_CHAR_CHAR
             | TYPE_BYTE_CHAR
             | TYPE_SHORT_CHAR
             | TYPE_INT_CHAR
             | TYPE_LONG_CHAR
             | TYPE_FLOAT_CHAR
             | TYPE_DOUBLE_CHAR => parseBaseType()
        case _ => throw new IllegalStateException("It's not a type signature start symbol: " + symbol);
      }
    }

    def parseFieldType(): IType = {
      val symbol = parser.getSymbol()
      symbol match {
        case TYPE_REFERENCE_CHAR => parseClassTypeSignature()
        case TYPE_ARRAY_CHAR => parseArrayTypeSignature()
        case TYPE_IDENTIFIER_CHAR => parseTypeVariableSignature()
        case _ => throw new IllegalStateException("It's not a field type start symbol: " + symbol);
      }
    }

    def parseBaseType(): BaseType = {
      val symbol = parser.getSymbol()
      parser.moveOne()
      symbol match {
        case TYPE_BOOLEAN_CHAR => TYPE_BOOLEAN
        case TYPE_CHAR_CHAR => TYPE_CHAR
        case TYPE_BYTE_CHAR => TYPE_BYTE
        case TYPE_SHORT_CHAR => TYPE_SHORT
        case TYPE_INT_CHAR => TYPE_INT
        case TYPE_LONG_CHAR => TYPE_LONG
        case TYPE_FLOAT_CHAR => TYPE_FLOAT
        case TYPE_DOUBLE_CHAR => TYPE_DOUBLE
        case _ => throw new IllegalStateException("It's not a base type: " + symbol);
      }
    }

    def parseTypeVariableSignature(): TypeVariableSignature = {
      val start = parser.moveOne()
      val identifier = parseIdentifier()
      new TypeVariableSignature(identifier, parser.getSignature(start))
    }


    def parseFieldTypeSignature(): FieldTypeSignature = {
      val start = parser.getPosition()
      val fieldType = parseFieldType()
      return new FieldTypeSignature(fieldType, parser.getSignature(start))
    }

    def parseClassBound(): FieldTypeSignature = {
      (parser.skipIfMatch(':') && !parser.isStopSymbol()) match {
        case true => parseFieldTypeSignature
        case false => null
      }
    }

    def parseInterfacesBound(): Array[FieldTypeSignature] = {
      val fieldTypeSignatures = ArrayBuffer[FieldTypeSignature]()
      var fieldTypeSignature: FieldTypeSignature = parseClassBound
      while (fieldTypeSignature != null) {
        fieldTypeSignatures += fieldTypeSignature
        fieldTypeSignature = parseClassBound
      }
      fieldTypeSignatures.toArray
    }

    def parseFormalTypeParameter(): FormalTypeParameter = {
      val start = parser.getPosition
      val identifier = parseIdentifier()
      val classBound = parseClassBound()
      val interfacesBound = parseInterfacesBound()
      val formalSignature = parser.getSignature(start)
      return new FormalTypeParameter(identifier, classBound, interfacesBound, formalSignature)
    }

    def parseFormalTypeParameters(): Array[FormalTypeParameter] = {
      val parameters = ArrayBuffer[FormalTypeParameter]()
      if (parser.skipIfMatch('<')) {
        val start = parser.getPosition
        while (!parser.skipIfMatch('>')) {
          var formalTypeParameter = parseFormalTypeParameter
          parameters += formalTypeParameter
          if (start == parser.getPosition) {
            throw new IllegalStateException("It's not a formal type parameter: " + parser.getSignature(start))
          }
        }
      }
      return parameters.toArray
    }

    def parseSuperInterfaceSignatures(): Array[ClassTypeSignature] = {
      val superInterfaces = ArrayBuffer[ClassTypeSignature]()
      while (parser.valid) {
        superInterfaces += parseClassTypeSignature()
      }
      superInterfaces.toArray
    }

    def parseClassSignature(): ClassSignature = {
      val formalTypeParameters = parseFormalTypeParameters()
      val superClassSignature = parseClassTypeSignature()
      val superInterfaceSignatures = parseSuperInterfaceSignatures()
      return new ClassSignature(formalTypeParameters, superClassSignature, superInterfaceSignatures, signature)
    }

    def parseTypeSignatures(): Array[IType] = {
      val types = ArrayBuffer[IType]()
      if (parser.skipIfMatch('(')) {
        while (!parser.skipIfMatch(')')) {
          types += parseTypeSignature()
        }
      }
      types.toArray
    }

    def parseReturnType(): IType = {
      if (parser.skipIfMatch(TYPE_VOID_CHAR)) {
        return TYPE_VOID
      }
      parseTypeSignature
    }

    def parseThrowSignature(): IType = {
      val symbol = parser.getSymbol
      symbol match {
        case TYPE_IDENTIFIER_CHAR => parseTypeVariableSignature
        case TYPE_REFERENCE_CHAR => parseClassTypeSignature
        case _ => throw new IllegalStateException("It's not a throw signature start symbol: " + symbol)
      }
    }

    def parseThrowsSignature(): Array[IType] = {
      val throwses = ArrayBuffer[IType]()
      while (parser.skipIfMatch('^')) {
        throwses += parseThrowSignature
      }
      throwses.toArray
    }

    def parseMethodTypeSignature(): MethodTypeSignature = {
      val formalTypeParameters = parseFormalTypeParameters()
      val typesSignatures = parseTypeSignatures()
      val returnType = parseReturnType()
      val throwsSignature = parseThrowsSignature()
      return new MethodTypeSignature(formalTypeParameters, typesSignatures, returnType, throwsSignature, signature)
    }

    if (scope == StructureScope.CLASS_SCOPE) {
      parseClassSignature
    } else if (scope == StructureScope.METHOD_SCOPE) {
      parseMethodTypeSignature
    } else if (scope == StructureScope.FIELD_SCOPE) {
      parseFieldTypeSignature
    } else {
      throw new IllegalStateException("Scope is unknown " + scope + " for signature: " + signature)
    }
  }

}
