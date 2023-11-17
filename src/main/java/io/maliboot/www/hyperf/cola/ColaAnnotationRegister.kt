package io.maliboot.www.hyperf.cola

import io.maliboot.www.hyperf.cola.dto.IsPropertyInitAnnotation
import io.maliboot.www.hyperf.cola.dto.OfDOAnnotation
import io.maliboot.www.hyperf.cola.dto.QueryDTOAnnotation
import io.maliboot.www.hyperf.cola.dto.UserContextAnnotation
import io.maliboot.www.hyperf.lombok.annotation.AbstractClassAnnotation
import io.maliboot.www.hyperf.lombok.annotation.AbstractFieldAnnotation
import io.maliboot.www.hyperf.lombok.annotation.AbstractMemberAnnotation
import io.maliboot.www.hyperf.lombok.annotation.LombokAnnotationFactory
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpFieldNode
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