package io.maliboot.devkit.idea.hyperf.skeleton.php

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import javax.swing.JPanel

class SegmentedBtnComp : JPanel() {
    private lateinit var dialogPanel: DialogPanel

    private var selectedItem: String = ""

    private val propertyGraph = PropertyGraph()

    private val selectedEnv: GraphProperty<String> = propertyGraph.lazyProperty { "todo" }


    fun create(items: ArrayList<String>): SegmentedBtnComp {
        // 过滤空或空白字符串
        val validItems = items.filter { it.isNotBlank() }
        if (validItems.isEmpty()) {
            throw IllegalArgumentException("分段按钮至少需要一个非空选项")
        }

        // 初始化选中项为第一个有效选项
        selectedEnv.set(validItems[0])
        selectedItem = validItems[0]

        // 创建 UI 面板，包含 SegmentedButton
        this.dialogPanel = panel {
            row { // 添加标签
//                segmentedButton(validItems) { it }.bind(selectedEnv).gap(RightGap.SMALL)
                comboBox(validItems).bindItem(selectedEnv).gap(RightGap.SMALL)
            }
        }

        // 监听选中项变化
        selectedEnv.afterChange { newValue ->
            selectedItem = newValue
        }

        return this
    }

    fun getSelectedText(): String {
        return this.selectedItem
    }

    fun getDialogPanel(): DialogPanel {
        return this.dialogPanel
    }
}