package io.maliboot.www.hyperf.lombok.index.externalizer

import com.intellij.util.io.DataExternalizer
import java.io.DataInput
import java.io.DataOutput

class MapDataExternalizer<K, V>(
    private val keyExternalizer: DataExternalizer<K>,
    private val valExternalizer: DataExternalizer<V>
) : DataExternalizer<Map<K, V>> {
    override fun save(out: DataOutput, value: Map<K, V>) {
        out.writeInt(value.size)
        value.forEach {
            keyExternalizer.save(out, it.key)
            valExternalizer.save(out, it.value)
        }
    }

    override fun read(input: DataInput): Map<K, V> {
        val size = input.readInt()
        val myMap = mutableMapOf<K, V>()

        repeat(size) {
            myMap[keyExternalizer.read(input)] = valExternalizer.read(input)
        }

        return myMap
    }
}