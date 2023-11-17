package io.maliboot.www.hyperf.extension.dic

import com.intellij.spellchecker.BundledDictionaryProvider

class HyperfDictionaryProvider: BundledDictionaryProvider {
    override fun getBundledDictionaries(): Array<String> {
        return arrayOf("magento.dic")
    }
}