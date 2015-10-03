package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.Constants._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.Signatures._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.core.parsing.DescriptorParser._
import org.powlab.jeye.core.parsing.SignatureParser._
import org.powlab.jeye.decode.expression.{BlockExpression, IExpression, ImportExpression}
import org.powlab.jeye.utils.Clazz

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object ImportsDecoder extends IDecoder {

  def decode(clazz: Clazz): IExpression = {

    val classFile = clazz.classFile
    val cpUtils = classFile.constantPoolUtils
    var imports = ListBuffer[ImportExpression]()
    val hashes = mutable.Map[String, String]()
    val thisPackageName = new ClassNameManager(cpUtils.thisClass.name).packageName

    // 1. ConstantPool
    classFile.constant_pool.foreach(cpi =>
      if (cpi != null) {
        cpi match {
          case item if item.tag == CONSTANT_Class => processClassInfo(item.asInstanceOf[ConstantClassInfo])
          case item if item.tag == CONSTANT_NameAndType => processDescriptor(cpUtils.getUtf8(item.asInstanceOf[ConstantNameAndTypeInfo].descriptor_index))
          case item if item.tag == CONSTANT_MethodType => processDescriptor(cpUtils.getUtf8(item.asInstanceOf[ConstantMethodTypeInfo].descriptor_index))
          case _ =>
        }
      }
    )

    def getClassNameFromMeta(meta: String) = meta.substring(1, meta.length() - 1)

    def acceptClassName(aClassNameManager: ClassNameManager) = aClassNameManager.hasPackage &&
      aClassNameManager.packageName != "java.lang" &&
      thisPackageName != aClassNameManager.packageName && !hashes.get(aClassNameManager.className).isDefined

    def addClassName(aClassName: String) {
      val classNameManager = new ClassNameManager(aClassName)
      if (acceptClassName(classNameManager)) {
        hashes(classNameManager.className) = classNameManager.className
        imports += new ImportExpression(classNameManager.className)
      }
    }

    def processClassInfo(cpClassInfo: ConstantClassInfo) = {
      val classname = classFile.constantPoolUtils.getUtf8(cpClassInfo.name_index)
      if (isArrayType(classname.charAt(0))) {
        processParamType(parseFieldDescriptor(classname))
      } else {
        addClassName(classname)
      }
    }

    def processParamType(paramType: ParameterDescriptor) = {
      if (paramType.baseType == TYPE_REFERENCE) {
        addClassName(getClassNameFromMeta(paramType.meta))
      } else if (paramType.baseType == TYPE_ARRAY) {
        val componentType = paramType.asInstanceOf[ArrayDescriptor].componentType
        if (componentType.baseType == TYPE_REFERENCE) {
          addClassName(getClassNameFromMeta(componentType.meta))
        }
      }
    }

    def processMethodDescriptor(aDescriptor: String) = {
      val methodDescriptor = parseMethodDescriptor(aDescriptor)
      methodDescriptor.parameters.foreach(processParamType)
      processParamType(methodDescriptor.returnType)
    }

    def processDescriptor(aDescriptor: String) = if (aDescriptor.indexOf("(") != -1) {
      processMethodDescriptor(aDescriptor)
    } else {
      processParamType(parseFieldDescriptor(aDescriptor))
    }

    // 2. Attributes
    def processAnnotation(aAnnotation: Annotation) {
      processParamType(parseFieldDescriptor(cpUtils.getUtf8(aAnnotation.type_index)))
      def processElementValuePairs(aElementValuePairs: AnyRef) {
        def processElementValue(elementValue: ElementValue) = {
          elementValue match {
            case value: ConstValueIndex => // Игнорим примитивы
            case value: EnumConstValue => processParamType(parseFieldDescriptor(cpUtils.getUtf8(value.type_name_index)))
            case value: ClassInfoIndex => processParamType(parseFieldDescriptor(cpUtils.getUtf8(value.class_info_index)))
            case value: Annotation => processAnnotation(value)
            case value: ArrayValue => value.values.map(processElementValuePairs)
          }
        }
        aElementValuePairs match {
          case elem: ElementValuePairs => processElementValue(elem.value)
          case elem: ElementValue => processElementValue(elem)
        }
      }
      aAnnotation.element_value_pairs.foreach(processElementValuePairs)
    }

    def processRuntimeAnnotationAttribute(aAnnotationAttribute: RuntimeAnnotationsAttribute) =
      aAnnotationAttribute.annotations.foreach(processAnnotation)

    def processParamAnnotationAttribute(aAnnotationAttribute: RuntimeParameterAnnotationsAttribute) {
      def processParamAnnotation(aParamAnnotation: ParameterAnnotations) {
        aParamAnnotation.annotations.foreach(processAnnotation)
      }
      aAnnotationAttribute.parameter_annotations.foreach(processParamAnnotation)
    }

    def processSignatureAttribute(aSignatureAttribute: SignatureAttribute, scope: StructureScope) {
      val signature = cpUtils.getUtf8(aSignatureAttribute.signature_index)

      def processClassSignature(aClassSignature: ClassSignature) {
        aClassSignature.formalTypeParameters.foreach(processFormalTypeParameter)
      }

      def processMethodTypeSignature(aMethodTypeSignature: MethodTypeSignature) {
        aMethodTypeSignature.formalTypeParameters.foreach(processFormalTypeParameter)
        aMethodTypeSignature.typesSignatures.foreach(processTypeSignature)
        processTypeSignature(aMethodTypeSignature.returnType)
        aMethodTypeSignature.throwsSignature.foreach {
          case aClassTypeSignature: ClassTypeSignature => processClassTypeSignature(aClassTypeSignature)
          case signature: TypeVariableSignature => //@TODO доделать обработку если будет нужно
        }
      }

      def processFieldTypeSignature(aFieldTypeSignature: FieldTypeSignature) {
        val fieldType = aFieldTypeSignature.fieldType
        fieldType match {
          case aClassTypeSignature: ClassTypeSignature =>
            processClassTypeSignature(aClassTypeSignature)
          case signature1: ArrayTypeSignature if fieldType.isInstanceOf[ArrayTypeSignature] && signature1.isClassType =>
            processClassTypeSignature(signature1.componentType.asInstanceOf[ClassTypeSignature])
          case skip => println(s"skip $skip")
        }
      }

      def processTypeSignature(typeSignature: IType) {
        typeSignature match {
          case aFieldTypeSignature: FieldTypeSignature =>
            processFieldTypeSignature(aFieldTypeSignature)
          case _ =>
        }
      }

      def processClassTypeSignature(aClassTypeSignature: ClassTypeSignature) {
        //println("aClassTypeSignature = " + aClassTypeSignature.getIdentifier());
        addClassName(aClassTypeSignature.identifier)
      }

      def processFormalTypeParameter(aFormalTypeParameter: FormalTypeParameter) {
        if (aFormalTypeParameter.classBound != null) {
          processFieldTypeSignature(aFormalTypeParameter.classBound)
        }
        aFormalTypeParameter.interfacesBound.foreach(processFieldTypeSignature)
      }

      val signatureType = parseSignature(signature, scope)
      if (scope == StructureScope.CLASS_SCOPE) {
        processClassSignature(signatureType.asInstanceOf[ClassSignature])
      } else if (scope == StructureScope.METHOD_SCOPE) {
        processMethodTypeSignature(signatureType.asInstanceOf[MethodTypeSignature])
      } else {
        processFieldTypeSignature(signatureType.asInstanceOf[FieldTypeSignature])
      }

    }

    def processAttribute(attribute: AttributeBaseInfo, scope: StructureScope) = {
      val tag = cpUtils.getUtf8(attribute.attribute_name_index)
      if (tag == ATTR_RuntimeVisibleAnnotations || tag == ATTR_RuntimeInvisibleAnnotations) {
        processRuntimeAnnotationAttribute(attribute.asInstanceOf[RuntimeAnnotationsAttribute])
      } else if (tag == ATTR_RuntimeVisibleParameterAnnotations || tag == ATTR_RuntimeInvisibleParameterAnnotations) {
        processParamAnnotationAttribute(attribute.asInstanceOf[RuntimeParameterAnnotationsAttribute])
      } else if (tag == ATTR_Signature) {
        processSignatureAttribute(attribute.asInstanceOf[SignatureAttribute], scope)
      }
    }

    // 3. Fields And Methods
    def proccessMember(aMember: MemberInfo, scope: StructureScope) {
      processDescriptor(cpUtils.getUtf8(aMember.descriptor_index))
      aMember.attributes.foreach(processAttribute(_, scope))
    }

    classFile.fields.foreach(proccessMember(_, StructureScope.FIELD_SCOPE))
    classFile.methods.foreach(proccessMember(_, StructureScope.METHOD_SCOPE))

    // 4. ClassFile
    classFile.attributes.foreach(processAttribute(_, StructureScope.CLASS_SCOPE))

    val expressions = clazz.innerClasses.map(this.decode)

    // 5. Sort Results And Return
    new BlockExpression ++= imports.toList.filter(_.className != "java.lang.annotation.Annotation")
      .sortBy(_.className) ++= expressions
  }

  case class ClassNameManager(aClassName: String) {
    val fullClassName = aClassName.replace("/", ".")
    val lastDotIndex = fullClassName.lastIndexOf(".")
    val innerClassIndex = fullClassName.indexOf("$", lastDotIndex)
    var packageName: String = null
    val className = if (innerClassIndex != -1) fullClassName.substring(0, innerClassIndex) else fullClassName
    if (lastDotIndex != -1) {
      packageName = className.substring(0, lastDotIndex)
    }

    def hasPackage = packageName != null
  }

}
