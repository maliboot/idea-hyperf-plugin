package io.maliboot.www.hyperf.lombok.annotation.register

import io.maliboot.www.hyperf.lombok.LombokCollector
import io.maliboot.www.hyperf.lombok.annotation.AbstractClassAnnotation
import io.maliboot.www.hyperf.lombok.annotation.AbstractFieldAnnotation
import io.maliboot.www.hyperf.lombok.annotation.AbstractMemberAnnotation
import io.maliboot.www.hyperf.lombok.annotation.LombokAnnotationFactory
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpClassNode
import io.maliboot.www.hyperf.lombok.annotation.lightNode.PhpFieldNode
import kotlin.reflect.KClass

class LombokAnnotationRegister : LombokAnnotationFactory {
    override fun getClassAnnotationList(phpClassNode: PhpClassNode): List<AbstractClassAnnotation> {
        return listOf(
            LoggerAnnotation(phpClassNode),
            OfAnnotation(phpClassNode),
            ToArrayAnnotation(phpClassNode),
            ToCollectionAnnotation(phpClassNode),
            DelegateAnnotation(phpClassNode),
        )
    }

    override fun getFieldAnnotationList(
        phpClassNode: PhpClassNode,
        phpFieldNode: PhpFieldNode
    ): List<AbstractFieldAnnotation> {
        return listOf(
            SetterAnnotation(phpClassNode, phpFieldNode),
            GetterAnnotation(phpClassNode, phpFieldNode),
        )
    }

    override fun getRegisterAttributes(): Map<String, List<KClass<out AbstractMemberAnnotation>>> {
        return LombokCollector.allData
    }
}