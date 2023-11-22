package io.maliboot.devkit.idea.common

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

class LombokBundle {
    companion object {
        const val BUNDLE = "messages.LombokBundle"

        private val INSTANCE = DynamicBundle(LombokBundle::class.java, "messages.LombokBundle")

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