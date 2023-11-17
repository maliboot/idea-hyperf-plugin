package io.maliboot.www.hyperf.lombok.annotation

import com.intellij.openapi.extensions.ExtensionPointName
import io.maliboot.www.hyperf.common.psi.data.MyCustomMember
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpAttribute
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpFieldNode
import kotlin.reflect.KClass

interface LombokAnnotationFactory {
    companion object {
        private val EP_NAME = ExtensionPointName<LombokAnnotationFactory>("io.maliboot.hyperf.lombokAnnotation")

        fun getAnnotationPClasses(): List<String> {
            val result: MutableList<String> = mutableListOf()
            EP_NAME.extensionList.forEach {
                result.addAll(it.getRegisterAttributes().keys)
            }
            return result
        }

        private fun getAnnotationKClasses(phpAttributes: List<PhpAttribute>): List<KClass<out AbstractMemberAnnotation>> {
            val annotations: MutableList<KClass<out AbstractMemberAnnotation>> = mutableListOf()

            EP_NAME.extensionList.forEach { lombokAnnotationFactory ->
                lombokAnnotationFactory.getRegisterAttributes().filter { annotationMap ->
                    phpAttributes.any { it.type == annotationMap.key }
                }.takeIf { it.isNotEmpty() }?.forEach {
                    annotations.addAll(it.value)
                }
            }

            return annotations
        }

        fun getMyCustomMembers(phpClassNode: PhpClassNode): MutableMap<String, MyCustomMember> {
            val result = mutableMapOf<String, MyCustomMember>()
            val annotations = getAnnotationKClasses(phpClassNode.attributes)

            EP_NAME.extensionList.forEach { lombokAnnotationFactory ->
                lombokAnnotationFactory.getClassAnnotationList(phpClassNode).filter {
                    annotations.contains(it::class)
                }.takeIf { it.isNotEmpty() }?.forEach { lombokClassAnnotator ->
                    lombokClassAnnotator.getCustomMembers().forEach {
                        result[it.key] = MyCustomMember(phpClassNode.type, "", it.value)
                    }
                }
            }

            phpClassNode.fields.forEach { phpFieldNode ->
                EP_NAME.extensionList.forEach { lombokAnnotationFactory ->
                    lombokAnnotationFactory.getFieldAnnotationList(phpClassNode, phpFieldNode).filter {
                        annotations.contains(it::class)
                    }.takeIf { it.isNotEmpty() }?.forEach { lombokFieldAnnotator ->
                        lombokFieldAnnotator.getCustomMembers().forEach {
                            result[it.key] =
                                MyCustomMember("${phpClassNode.type}.$${phpFieldNode.name}", "", it.value)
                        }
                    }
                }
            }

            return result
        }
    }

    fun getClassAnnotationList(phpClassNode: PhpClassNode): List<AbstractClassAnnotation>

    fun getFieldAnnotationList(phpClassNode: PhpClassNode, phpFieldNode: PhpFieldNode): List<AbstractFieldAnnotation>

    fun getRegisterAttributes(): Map<String, List<KClass<out AbstractMemberAnnotation>>>
}