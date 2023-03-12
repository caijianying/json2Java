package com.tool.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.javaear.json4bean.core.Json2Java;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author caijy
 * @description 右键选择
 * @date 2022/11/5 星期六 10:41 下午
 */
public class Json2JavaUseLombokAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        String text = editor.getSelectionModel().getSelectedText();
        text = StringEscapeUtils.unescapeJava(text);
        Json2Java.INSTANCE.useLombok(true).writeBean(text, "RootBean", "Json2Java", event.getProject().getBasePath());
    }
}
