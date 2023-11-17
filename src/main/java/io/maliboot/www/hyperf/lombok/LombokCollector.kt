package io.maliboot.www.hyperf.lombok

import io.maliboot.www.hyperf.lombok.annotation.AbstractMemberAnnotation
import io.maliboot.www.hyperf.lombok.annotation.register.*
import io.maliboot.www.hyperf.lombok.annotation.register.GetterAnnotation
import kotlin.reflect.KClass

class LombokCollector {
    companion object {
        const val LOGGER = "\\MaliBoot\\Lombok\\Annotation\\Logger"

        const val SETTER = "\\MaliBoot\\Lombok\\Annotation\\Setter"

        const val GETTER = "\\MaliBoot\\Lombok\\Annotation\\Getter"

        const val GETTER_SETTER = "\\MaliBoot\\Lombok\\Annotation\\GetterSetter"

        const val LOMBOK = "\\MaliBoot\\Lombok\\Annotation\\Lombok"

        const val TO_ARRAY = "\\MaliBoot\\Lombok\\Annotation\\ToArray"

        const val TO_COLLECTION = "\\MaliBoot\\Lombok\\Annotation\\ToCollection"

        const val OF = "\\MaliBoot\\Lombok\\Annotation\\Of"

        const val DELEGATE = "\\MaliBoot\\Lombok\\Annotation\\Delegate"

        val baseData: List<KClass<out AbstractMemberAnnotation>> = listOf(
            SetterAnnotation::class,
            GetterAnnotation::class,
            ToArrayAnnotation::class,
            ToCollectionAnnotation::class,
            OfAnnotation::class,
            LoggerAnnotation::class,
        )

        val allData: MutableMap<String, List<KClass<out AbstractMemberAnnotation>>> = mutableMapOf(
            LOGGER to listOf(LoggerAnnotation::class),
            SETTER to listOf(
                SetterAnnotation::class,
            ),
            GETTER to listOf(
                GetterAnnotation::class,
            ),
            GETTER_SETTER to listOf(
                SetterAnnotation::class,
                GetterAnnotation::class,
            ),
            TO_ARRAY to listOf(
                ToArrayAnnotation::class,
            ),
            TO_COLLECTION to listOf(
                ToCollectionAnnotation::class,
            ),
            OF to listOf(
                OfAnnotation::class,
            ),
            DELEGATE to listOf(
                DelegateAnnotation::class,
            ),
            LOMBOK to baseData,
        )
    }
}