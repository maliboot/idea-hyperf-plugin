package io.maliboot.devkit.idea.lombok.index.externalizer

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import java.io.DataInput
import java.io.DataOutput

class PhpTypeDataExternalizer : DataExternalizer<PhpType> {
    companion object {
        val INSTANCE = PhpTypeDataExternalizer()
    }

    override fun save(out: DataOutput, value: PhpType) {
        var endValue = value.toString()

        if (!value.isComplete) {
            endValue = endValue.removeSuffix("|?")
        }

        EnumeratorStringDescriptor.INSTANCE.save(
            out,
            endValue
        )
    }

    override fun read(input: DataInput): PhpType {
        val phpType = PhpType()
        EnumeratorStringDescriptor.INSTANCE.read(input).split('|').forEach {
            phpType.add(it)
        }
        return phpType
    }
}