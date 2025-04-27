package io.maliboot.devkit.idea.cola

import io.maliboot.devkit.idea.cola.dto.IsPropertyInitAnnotation
import io.maliboot.devkit.idea.cola.dto.OfDOAnnotation
import io.maliboot.devkit.idea.cola.dto.QueryDTOAnnotation
import io.maliboot.devkit.idea.cola.dto.UserContextAnnotation
import io.maliboot.devkit.idea.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.lombok.annotation.AbstractFieldAnnotation
import io.maliboot.devkit.idea.lombok.annotation.AbstractMemberAnnotation
import io.maliboot.devkit.idea.lombok.annotation.LombokAnnotationFactory
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpFieldNode
import kotlin.reflect.KClass

class ColaAnnotationRegister : LombokAnnotationFactory {
    override fun getClassAnnotationList(phpClassNode: PhpClassNode): List<AbstractClassAnnotation> {
        return listOf(
            OfEntityAnnotation(phpClassNode),
            ToEntityAnnotation(phpClassNode),
            IsPropertyInitAnnotation(phpClassNode),
            OfDOAnnotation(phpClassNode),
            QueryDTOAnnotation(phpClassNode),
            UserContextAnnotation(phpClassNode)
        )
    }

    override fun getFieldAnnotationList(
        phpClassNode: PhpClassNode,
        phpFieldNode: PhpFieldNode
    ): List<AbstractFieldAnnotation> {
        return listOf(
        )
    }

    override fun getRegisterAttributes(): Map<String, List<KClass<out AbstractMemberAnnotation>>> {
        return ColaAttribute.allData
    }
}