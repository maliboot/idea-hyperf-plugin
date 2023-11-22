package io.maliboot.devkit.idea.lombok

import io.maliboot.devkit.idea.lombok.annotation.AbstractMemberAnnotation
import io.maliboot.devkit.idea.lombok.annotation.register.*
import io.maliboot.devkit.idea.lombok.annotation.register.GetterAnnotation
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
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.LOGGER to listOf(LoggerAnnotation::class),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.SETTER to listOf(
                SetterAnnotation::class,
            ),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.GETTER to listOf(
                GetterAnnotation::class,
            ),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.GETTER_SETTER to listOf(
                SetterAnnotation::class,
                GetterAnnotation::class,
            ),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.TO_ARRAY to listOf(
                ToArrayAnnotation::class,
            ),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.TO_COLLECTION to listOf(
                ToCollectionAnnotation::class,
            ),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.OF to listOf(
                OfAnnotation::class,
            ),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.DELEGATE to listOf(
                DelegateAnnotation::class,
            ),
            io.maliboot.devkit.idea.lombok.LombokCollector.Companion.LOMBOK to io.maliboot.devkit.idea.lombok.LombokCollector.Companion.baseData,
        )
    }
}