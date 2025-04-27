package io.maliboot.devkit.idea.cola.dto

import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import io.maliboot.devkit.idea.cola.ColaAttribute
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import io.maliboot.devkit.idea.common.psi.data.CustomParameter
import io.maliboot.devkit.idea.lombok.annotation.AbstractClassAnnotation
import io.maliboot.devkit.idea.lombok.annotation.lightNode.PhpClassNode

class QueryDTOAnnotation(classNode: PhpClassNode) : AbstractClassAnnotation(classNode) {
    companion object {
        const val FEATURE = "_queryDTO"
    }

    override fun getCustomField(): List<CustomField> {
        val customClazz = CustomClass(phpClassNode.type)
        return listOf(
            CustomField(FEATURE, customClazz, "DEFAULT_PAGE_SIE", PhpType.INT, "10", PhpModifier.PUBLIC_FINAL_STATIC),
            CustomField(FEATURE, customClazz, "pageSize", PhpType.INT, "10", PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC),
            CustomField(FEATURE, customClazz, "pageIndex", PhpType.INT, "1", PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC),
            CustomField(
                FEATURE,
                customClazz,
                "orderBy",
                PhpType().add(PhpType.INT).add(PhpType.ARRAY),
                "",
                PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC
            ),
            CustomField(FEATURE, customClazz, "groupBy", PhpType.STRING, null, PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC),
            CustomField(
                FEATURE,
                customClazz,
                "needTotalCount",
                PhpType.BOOLEAN,
                "true",
                PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC
            ),
            CustomField(FEATURE, customClazz, "filters", PhpType.ARRAY, "[]", PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC),
            CustomField(
                FEATURE,
                customClazz,
                "columns",
                PhpType.ARRAY,
                "['*']",
                PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC
            ),
        )
    }

    override fun getCustomMethod(): List<CustomMethod> {
        val customClazz = CustomClass(phpClassNode.type)
        val clazzType = PhpType().add(phpClassNode.type)
        return listOf(
            CustomMethod(FEATURE, customClazz, "getPageSize", PhpType.INT, listOf()),
            CustomMethod(
                FEATURE,
                customClazz,
                "setPageSize",
                clazzType,
                listOf(CustomParameter("pageSize", PhpType.INT))
            ),
            CustomMethod(FEATURE, customClazz, "getPageIndex", PhpType.INT, listOf()),
            CustomMethod(
                FEATURE,
                customClazz,
                "setPageIndex",
                clazzType,
                listOf(CustomParameter("pageIndex", PhpType.INT))
            ),
            CustomMethod(
                FEATURE,
                customClazz,
                "getOrderBy",
                PhpType().add(PhpType.STRING).add(PhpType.ARRAY),
                listOf()
            ),
            CustomMethod(
                FEATURE,
                customClazz,
                "setOrderBy",
                clazzType,
                listOf(CustomParameter("orderBy", PhpType().add(PhpType.STRING).add(PhpType.ARRAY)))
            ),
            CustomMethod(FEATURE, customClazz, "getGroupBy", PhpType.STRING, listOf()),
            CustomMethod(
                FEATURE,
                customClazz,
                "setGroupBy",
                clazzType,
                listOf(CustomParameter("groupBy", PhpType.STRING))
            ),
            CustomMethod(FEATURE, customClazz, "isNeedTotalCount", PhpType.BOOLEAN, listOf()),
            CustomMethod(
                FEATURE,
                customClazz,
                "setNeedTotalCount",
                clazzType,
                listOf(CustomParameter("needTotalCount", PhpType.BOOLEAN))
            ),
            CustomMethod(FEATURE, customClazz, "getOffset", PhpType.INT, listOf()),
            CustomMethod(FEATURE, customClazz, "getFilters", PhpType.ARRAY, listOf()),
            CustomMethod(
                FEATURE,
                customClazz,
                "setFilters",
                clazzType,
                listOf(CustomParameter("filters", PhpType.ARRAY))
            ),
            CustomMethod(
                FEATURE,
                customClazz,
                "addFilter",
                clazzType,
                listOf(CustomParameter("filters", PhpType.ARRAY))
            ),
            CustomMethod(FEATURE, customClazz, "getColumns", PhpType.ARRAY, listOf()),
            CustomMethod(
                FEATURE,
                customClazz,
                "setColumns",
                clazzType,
                listOf(CustomParameter("columns", PhpType.ARRAY))
            ),
        )
    }

    override fun enable(): Boolean {
        val attrs = phpClassNode.attributes.filter { it.type == ColaAttribute.DTO }
        if (attrs.isEmpty()) {
            return false
        }
        attrs.first().args.filter {
            it.contains("type:")
        }.takeIf {
            it.isNotEmpty()
        }?.first()?.contains("query-page").let {
            return it == true
        }
    }
}