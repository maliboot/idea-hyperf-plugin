package io.maliboot.devkit.idea.hyperf.dic

import com.intellij.spellchecker.BundledDictionaryProvider

class HyperfDictionaryProvider: BundledDictionaryProvider {
    override fun getBundledDictionaries(): Array<String> {
        return arrayOf("hyperf.dic")
    }
}