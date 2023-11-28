package io.maliboot.devkit.idea.hyperf.lombok.index.externalizer

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorIntegerDescriptor
import com.intellij.util.io.EnumeratorStringDescriptor
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomClass
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomField
import io.maliboot.devkit.idea.hyperf.tool.psi.data.CustomMethod
import io.maliboot.devkit.idea.hyperf.tool.psi.data.MyCustomMember
import java.io.DataInput
import java.io.DataOutput

class ClassMemberDataExternalizer : DataExternalizer<MyCustomMember> {
    companion object {
        val INSTANCE = ClassMemberDataExternalizer()
    }

    override fun save(out: DataOutput, value: MyCustomMember) {
        EnumeratorStringDescriptor.INSTANCE.save(out, value.fromFQN)
        EnumeratorStringDescriptor.INSTANCE.save(out, value.fromFile)
        // field
        if (value.member is CustomField) {
            EnumeratorIntegerDescriptor.INSTANCE.save(out, 0)
            FieldDataExternalizer.INSTANCE.save(out, value.member)
        }

        // method
        if (value.member is CustomMethod) {
            EnumeratorIntegerDescriptor.INSTANCE.save(out, 1)
            MethodDataExternalizer.INSTANCE.save(out, value.member)
        }

        // class
        if (value.member is CustomClass) {
            EnumeratorIntegerDescriptor.INSTANCE.save(out, 2)
            EnumeratorStringDescriptor.INSTANCE.save(out, value.member.fqn)
        }
    }

    override fun read(input: DataInput): MyCustomMember? {
        val fromFQN = EnumeratorStringDescriptor.INSTANCE.read(input)
        val fromFile = EnumeratorStringDescriptor.INSTANCE.read(input)
        return when (EnumeratorIntegerDescriptor.INSTANCE.read(input)) {
            0 -> MyCustomMember(fromFQN, fromFile, FieldDataExternalizer.INSTANCE.read(input))
            1 -> MyCustomMember(fromFQN, fromFile, MethodDataExternalizer.INSTANCE.read(input))
            2 -> MyCustomMember(fromFQN, fromFile, CustomClass(EnumeratorStringDescriptor.INSTANCE.read(input)))
            else -> null
        }
    }
}