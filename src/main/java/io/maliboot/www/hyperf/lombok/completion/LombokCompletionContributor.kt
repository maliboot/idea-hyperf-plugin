package io.maliboot.www.hyperf.lombok.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.completion.PhpVariantsUtil
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpModifier
import io.maliboot.www.hyperf.common.psi.data.CustomField
import io.maliboot.www.hyperf.common.psi.data.CustomMethod
import io.maliboot.www.hyperf.lombok.extend.getPhpClass
import io.maliboot.www.hyperf.lombok.index.CustomMemberPsiGist
import io.maliboot.www.hyperf.lombok.psi.FakePsiPhpField
import io.maliboot.www.hyperf.lombok.psi.FakePsiPhpMethod

class LombokCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val reference = parameters.position.parent as? MemberReference ?: return

//        ReadAction.compute<Any?, RuntimeException> {
        val phpClass =
            reference.classReference?.globalType?.getPhpClass(reference.project)
                ?: return
        val positionClass = parameters.position.parentOfType<PhpClass>()
        val members = CustomMemberPsiGist.getCustomMembers(phpClass.containingFile)
        if (members.isEmpty()) {
            return
        }

        for (member in members) {
            val isShow = when (member.value.member.getModifierCS()?.access) {
                PhpModifier.Access.PUBLIC -> true
                PhpModifier.Access.PROTECTED -> {
                    positionClass?.fqn == phpClass.fqn || positionClass?.superFQN == phpClass.fqn
                }

                PhpModifier.Access.PRIVATE -> {
                    positionClass?.fqn == phpClass.fqn
                }

                else -> false
            }
            if (!isShow) {
                continue
            }
            if (member.value.member is CustomField) {
                result.addElement(
                    PhpVariantsUtil.getLookupItem(
                        FakePsiPhpField(
                            member.value.member as CustomField,
                            phpClass
                        ), null
                    )
                )
            }

            if (member.value.member is CustomMethod) {
                result.addElement(
                    PhpVariantsUtil.getLookupItem(
                        FakePsiPhpMethod(
                            member.value.member as CustomMethod,
                            phpClass
                        ), null
                    )
                )
            }
        }
//        }
    }
}