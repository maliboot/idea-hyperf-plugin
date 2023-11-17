package io.maliboot.www.hyperf.common

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

class HyperfBundle {
    companion object {
        const val BUNDLE = "messages.HyperfBundle"

        private val INSTANCE = DynamicBundle(HyperfBundle::class.java, "messages.HyperfBundle")

        @Nls
        fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
            return INSTANCE.getMessage(key, *params)
        }

        fun messagePointer(
            @PropertyKey(resourceBundle = BUNDLE) key: String,
            vararg params: Any
        ): Supplier<String> {
            return INSTANCE.getLazyMessage(key, *params)
        }
    }
}