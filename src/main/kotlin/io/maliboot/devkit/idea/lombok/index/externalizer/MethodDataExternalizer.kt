package io.maliboot.devkit.idea.lombok.index.externalizer

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.jetbrains.php.lang.psi.elements.PhpModifier
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomMethod
import java.io.DataInput
import java.io.DataOutput

class MethodDataExternalizer : DataExternalizer<CustomMethod> {
    companion object {
        val INSTANCE = MethodDataExternalizer()
    }

    override fun save(out: DataOutput, value: CustomMethod) {
        EnumeratorStringDescriptor.INSTANCE.save(out, value.feature)
        EnumeratorStringDescriptor.INSTANCE.save(out, value.name)
        PhpTypeDataExternalizer.INSTANCE.save(out, value.returnType)
        EnumeratorStringDescriptor.INSTANCE.save(out, value.getFQN())
        EnumeratorStringDescriptor.INSTANCE.save(
            out,
            "${value.modifier.access.name}|${value.modifier.abstractness.name}|${value.modifier.state.name}"
        )
        ListDataExternalizer(ParameterDataExternalizer.INSTANCE).save(
            out,
            value.parameters
        )
    }

    override fun read(input: DataInput): CustomMethod {
        val feature = EnumeratorStringDescriptor.INSTANCE.read(input)
        val name = EnumeratorStringDescriptor.INSTANCE.read(input)
        val returnType = PhpTypeDataExternalizer.INSTANCE.read(input)
        val fqn = EnumeratorStringDescriptor.INSTANCE.read(input)
        val modifierSource = EnumeratorStringDescriptor.INSTANCE.read(input).split("|")
        val parameters = ListDataExternalizer(ParameterDataExternalizer.INSTANCE).read(input)

        return CustomMethod(
            feature,
            CustomClass(fqn.split('.').first()),
            name,
            returnType,
            parameters,
            PhpModifier.instance(
                PhpModifier.Access.valueOf(modifierSource[0]),
                PhpModifier.Abstractness.valueOf(modifierSource[1]),
                PhpModifier.State.valueOf(modifierSource[2]),
            )
        )
    }
}