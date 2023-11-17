package io.maliboot.www.hyperf.cola

import io.maliboot.www.hyperf.cola.dto.IsPropertyInitAnnotation
import io.maliboot.www.hyperf.cola.dto.OfDOAnnotation
import io.maliboot.www.hyperf.cola.dto.QueryDTOAnnotation
import io.maliboot.www.hyperf.cola.dto.UserContextAnnotation
import io.maliboot.www.hyperf.lombok.LombokCollector
import io.maliboot.www.hyperf.lombok.annotation.AbstractMemberAnnotation
import io.maliboot.www.hyperf.lombok.annotation.register.DelegateAnnotation
import kotlin.reflect.KClass

class ColaAttribute {
    companion object {
        const val VO = "\\MaliBoot\\Dto\\Annotation\\ViewObject"

        const val DTO = "\\MaliBoot\\Dto\\Annotation\\DataTransferObject"

        const val DATABASE = "\\MaliBoot\\Cola\\Annotation\\Database"

        const val AGGREGATE_ROOT = "\\MaliBoot\\Cola\\Annotation\\AggregateRoot"

        const val ENTITY = "\\MaliBoot\\Cola\\Annotation\\Entity"

        const val VALUE_OBJECT = "\\MaliBoot\\Cola\\Annotation\\ValueObject"

        val structObject: List<KClass<out AbstractMemberAnnotation>> = LombokCollector.baseData + listOf(
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