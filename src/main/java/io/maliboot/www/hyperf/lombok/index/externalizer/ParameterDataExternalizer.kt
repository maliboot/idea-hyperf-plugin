package io.maliboot.www.hyperf.lombok.index.externalizer

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.NullableDataExternalizer
import io.maliboot.www.hyperf.common.psi.data.CustomParameter
import java.io.DataInput
import java.io.DataOutput

class ParameterDataExternalizer : DataExternalizer<CustomParameter> {
    companion object {
        val INSTANCE = ParameterDataExternalizer()
    }

    override fun save(out: DataOutput, value: CustomParameter) {
        EnumeratorStringDescriptor.INSTANCE.save(out, value.name)
        PhpTypeDataExternalizer.INSTANCE.save(out, value.returnType)
        NullableDataExternalizer(EnumeratorStringDescriptor.INSTANCE).save(
            out,
            value.defaultValue
        )
    }

    override fun read(input: DataInput): CustomParameter {
        val name = EnumeratorStringDescriptor.INSTANCE.read(input)
        val returnType = PhpTypeDataExternalizer.INSTANCE.read(input)
        val defaultValue = NullableDataExternalizer(EnumeratorStringDescriptor.INSTANCE).read(input)

        return CustomParameter(
            name,
            returnType,
            defaultValue
        )
    }
}