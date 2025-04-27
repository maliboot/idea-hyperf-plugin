package io.maliboot.devkit.idea.lombok.index.externalizer

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.NullableDataExternalizer
import com.jetbrains.php.lang.psi.elements.PhpModifier
import io.maliboot.devkit.idea.common.psi.data.CustomClass
import io.maliboot.devkit.idea.common.psi.data.CustomField
import java.io.DataInput
import java.io.DataOutput

class FieldDataExternalizer : DataExternalizer<CustomField> {
    companion object {
        val INSTANCE = FieldDataExternalizer()
    }

    override fun save(out: DataOutput, value: CustomField) {
        EnumeratorStringDescriptor.INSTANCE.save(out, value.feature)
        EnumeratorStringDescriptor.INSTANCE.save(out, value.name)
        PhpTypeDataExternalizer.INSTANCE.save(out, value.returnType)
        EnumeratorStringDescriptor.INSTANCE.save(out, value.getFQN())
        EnumeratorStringDescriptor.INSTANCE.save(
            out,
            "${value.modifier.access.name}|${value.modifier.abstractness.name}|${value.modifier.state.name}"
        )
        NullableDataExternalizer(EnumeratorStringDescriptor.INSTANCE).save(
            out,
            value.defaultValue
        )
    }

    override fun read(input: DataInput): CustomField {
        val feature = EnumeratorStringDescriptor.INSTANCE.read(input)
        val name = EnumeratorStringDescriptor.INSTANCE.read(input)
        val returnType = PhpTypeDataExternalizer.INSTANCE.read(input)
        val fqn = EnumeratorStringDescriptor.INSTANCE.read(input)
        val modifierSource = EnumeratorStringDescriptor.INSTANCE.read(input).split("|")
        val defaultValue = NullableDataExternalizer(EnumeratorStringDescriptor.INSTANCE).read(input)

        return CustomField(
            feature,
            CustomClass(fqn.split('.').first()),
            name,
            returnType,
            defaultValue,
            PhpModifier.instance(
                PhpModifier.Access.valueOf(modifierSource[0]),
                PhpModifier.Abstractness.valueOf(modifierSource[1]),
                PhpModifier.State.valueOf(modifierSource[2]),
            )
        )
    }
}