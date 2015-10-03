package org.powlab.jeye.core.parsing

import org.powlab.jeye.core.Signatures._
import org.powlab.jeye.core.{Constants, Types}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.powlab.jeye.core.StructureScope

@RunWith(classOf[JUnitRunner])
class SignatureParserTest extends Specification {
  override def is = s2""" ${"Specification for parsing signature".title}

      Signature can either be: $endp
        1. $aFieldSignature
        2. $aMethodSignature
        3. $aClassSignature
      """

  def aFieldSignature = s2"""
    Field parsing signature should
      1. check identifier ${fieldSignature().t1}
      2. check wildcard argument ${fieldSignature().t2}
      3. check argument type ${fieldSignature().t3} """

  case class fieldSignature() {
    val fieldSignature = "Ljava/util/List<-Ljavax/xml/stream/FactoryConfigurationError;>;"
    val fs = SignatureParser.parseSignature(fieldSignature, StructureScope.FIELD_SCOPE).asInstanceOf[FieldTypeSignature]
    val classTypeSignature = fs.fieldType.asInstanceOf[ClassTypeSignature]
    val typeArgument = classTypeSignature.typeArguments(0)

    def t1 = "java/util/List" must_== classTypeSignature.identifier

    def t2 = '-' must_== typeArgument.wildCardIndicator

    def t3 = {
      val typeArgumentClassType = typeArgument.fieldTypeSignature.fieldType.asInstanceOf[ClassTypeSignature]
      "javax/xml/stream/FactoryConfigurationError" must_== typeArgumentClassType.identifier
    }
  }

  def aMethodSignature = s2"""
    Method parsing signature should
      1. check methodSignature1 ${methodSignature().test1}
      2. check methodSignature2 ${methodSignature().test2} """

  case class methodSignature() {

    def test1 = s2"""
      First method signature should
        1. check formalTypeParameter identifier ${methodSignature1().t1}
        2. check classBound ${methodSignature1().t2}
        3. check typeSignatures ${methodSignature1().t3}
        4. check returnType ${methodSignature1().t4}
        5. check throwSignature ${methodSignature1().t5} """

    def test2 = s2"""
      Second method signature should
        1. check formalTypeParameter identifier ${methodSignature2().t1}
        2. check classBound ${methodSignature2().t2}
        3. check typeSignatures ${methodSignature2().t3}
        4. check returnType ${methodSignature2().t4}
        5. check throwSignature ${methodSignature2().t5} """

    case class methodSignature1() {
      val methodSignature = "<T:Ljava/lang/Enum<TT;>;>(Ljava/lang/Class<TT;>;Ljava/lang/String;Ljava/util/Comparator<-Ljava/lang/String;>;)TT;^TK;";
      val mts = SignatureParser.parseSignature(methodSignature, StructureScope.METHOD_SCOPE).asInstanceOf[MethodTypeSignature]
      val formalTypeParameters = mts.formalTypeParameters

      def t1 = "T" must_== formalTypeParameters(0).identifier

      def t2 = {
        val ft = formalTypeParameters(0).classBound.fieldType.asInstanceOf[ClassTypeSignature]
        "java/lang/Enum" must_== ft.identifier
        val typeArgument = ft.typeArguments(0).fieldTypeSignature.fieldType.asInstanceOf[TypeVariableSignature]
        "T" must_== typeArgument.identifier
      }

      def t3 = {
        val ts1 = mts.typesSignatures(0).asInstanceOf[ClassTypeSignature]
        "java/lang/Class" must_== ts1.identifier
        "T" must_== ts1.typeArguments(0).fieldTypeSignature.fieldType.asInstanceOf[TypeVariableSignature].identifier
        val ts2 = mts.typesSignatures(1).asInstanceOf[ClassTypeSignature]
        "java/lang/String" must_== ts2.identifier
        val ts3 = mts.typesSignatures(2).asInstanceOf[ClassTypeSignature]
        "java/util/Comparator" must_== ts3.identifier
        "java/lang/String" must_== ts3.typeArguments(0).fieldTypeSignature.fieldType.asInstanceOf[ClassTypeSignature].identifier
        '-' must_== ts3.typeArguments(0).wildCardIndicator
      }

      def t4 = "T" must_== mts.returnType.asInstanceOf[TypeVariableSignature].identifier

      def t5 = "K" must_== mts.throwsSignature(0).asInstanceOf[TypeVariableSignature].identifier
    }

    case class methodSignature2() {
      val methodSignature = "<T:Ljava/lang/Object;>(TT;)V";
      val mts = SignatureParser.parseSignature(methodSignature, StructureScope.METHOD_SCOPE).asInstanceOf[MethodTypeSignature]
      val formalTypeParameter = mts.formalTypeParameters(0);

      def t1 = "T" must_== formalTypeParameter.identifier

      def t2 = {
        val ft = formalTypeParameter.classBound.fieldType.asInstanceOf[ClassTypeSignature]
        "java/lang/Object" must_== ft.identifier
      }

      def t3 = {
        val ts1 = mts.typesSignatures(0).asInstanceOf[TypeVariableSignature]
        "T" must_== ts1.identifier
      }

      def t4 = Types.TYPE_VOID must_== mts.returnType

      def t5 = mts.throwsSignature must beEmpty
    }

  }

  def aClassSignature = s2"""
      Class parsing signature should
        1. check first formalTypeParameter ${classSignature().t1}
        2. check second formalTypeParameter ${classSignature().t2}
        3. check third formalTypeParameter ${classSignature().t3}
        4. check fourth formalTypeParameter ${classSignature().t4} """

  case class classSignature() {
    val classSignatureText = "<Bi::Ljava/io/Serializable;:Ljavax/xml/stream/Location;Ci::Ljava/lang/Runnable;Di::Lorg/powlab/jeye/scenario/data/IMarkable<Ljava/lang/Thread;>;M:Ljava/lang/Object;>Lorg/powlab/jeye/scenario/data/Simple;Ljava/awt/event/ActionListener;Ljava/util/Comparator<Ljava/lang/String;>;"
    var classSignature = SignatureParser.parseSignature(classSignatureText, StructureScope.CLASS_SCOPE).asInstanceOf[ClassSignature]
    var formalTypeParameters = classSignature.formalTypeParameters

    def t1 = {
      val ftp = formalTypeParameters(0)
      "Bi" must_== ftp.identifier
      ftp.classBound must beNull
      "java/io/Serializable" must_== ftp.interfacesBound(0).fieldType.asInstanceOf[ClassTypeSignature].identifier
      "javax/xml/stream/Location" must_== ftp.interfacesBound(1).fieldType.asInstanceOf[ClassTypeSignature].identifier
    }

    def t2 = {
      val ftp = formalTypeParameters(1)
      "Ci" must_== ftp.identifier
      ftp.classBound must beNull
      "java/lang/Runnable" must_== ftp.interfacesBound(0).fieldType.asInstanceOf[ClassTypeSignature].identifier
    }

    def t3 = {
      val ftp = formalTypeParameters(2)
      "Di" must_== ftp.identifier
      ftp.classBound must beNull
      val interfaceBound = ftp.interfacesBound(0)
      "org/powlab/jeye/scenario/data/IMarkable" must_== interfaceBound.fieldType.asInstanceOf[ClassTypeSignature].identifier
      val typeArgument = interfaceBound.fieldType.asInstanceOf[ClassTypeSignature].typeArguments(0)
      "java/lang/Thread" must_== typeArgument.fieldTypeSignature.fieldType.asInstanceOf[ClassTypeSignature].identifier
    }

    def t4 = {
      val ftp = formalTypeParameters(3)
      "M" must_== ftp.identifier
      "java/lang/Object" must_== ftp.classBound.fieldType.asInstanceOf[ClassTypeSignature].identifier
      ftp.interfacesBound must beEmpty
    }
  }

}
