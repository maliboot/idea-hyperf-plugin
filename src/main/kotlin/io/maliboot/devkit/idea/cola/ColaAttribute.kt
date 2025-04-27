package io.maliboot.devkit.idea.cola

import io.maliboot.devkit.idea.cola.dto.IsPropertyInitAnnotation
import io.maliboot.devkit.idea.cola.dto.OfDOAnnotation
import io.maliboot.devkit.idea.cola.dto.QueryDTOAnnotation
import io.maliboot.devkit.idea.cola.dto.UserContextAnnotation
import io.maliboot.devkit.idea.lombok.annotation.AbstractMemberAnnotation
import io.maliboot.devkit.idea.lombok.annotation.register.DelegateAnnotation
import kotlin.reflect.KClass

class ColaAttribute {
    companion object {
        const val VO = "\\MaliBoot\\Dto\\Annotation\\ViewObject"

        const val DTO = "\\MaliBoot\\Dto\\Annotation\\DataTransferObject"

        const val DATABASE = "\\MaliBoot\\Cola\\Annotation\\Database"

        const val AGGREGATE_ROOT = "\\MaliBoot\\Cola\\Annotation\\AggregateRoot"

        const val ENTITY = "\\MaliBoot\\Cola\\Annotation\\Entity"

        const val VALUE_OBJECT = "\\MaliBoot\\Cola\\Annotation\\ValueObject"

        val structObject: List<KClass<out AbstractMemberAnnotation>> = io.maliboot.devkit.idea.lombok.LombokCollector.baseData + listOf(
            IsPropertyInitAnnotation::class,
        )

        val baseDTO: List<KClass<out AbstractMemberAnnotation>> = structObject + listOf(UserContextAnnotation::class)

        val allData: MutableMap<String, List<KClass<out AbstractMemberAnnotation>>> = mutableMapOf(
            VO to baseDTO + listOf(
                OfDOAnnotation::class,
            ),
            DTO to baseDTO + listOf(
                OfDOAnnotation::class,
                QueryDTOAnnotation::class,
            ),
            DATABASE to structObject + listOf(
                ToEntityAnnotation::class,
                OfEntityAnnotation::class,
                DelegateAnnotation::class,
            ),
            AGGREGATE_ROOT to structObject,
            ENTITY to structObject,
            VALUE_OBJECT to structObject,
        )
    }
}