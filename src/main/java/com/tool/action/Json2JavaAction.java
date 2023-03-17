package com.tool.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.javaear.json4bean.core.Json2Java;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author caijy
 * @description 右键选择
 * @date 2022/11/5 星期六 10:41 下午
 */
public class Json2JavaAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        String text = editor.getSelectionModel().getSelectedText();
        boolean isJson = text.startsWith("[") || text.startsWith("{");
        if (StringUtils.isBlank(text) || !isJson) {
            Notification notification = new Notification("Print", "提示", "请选择合适的JSON文本！", NotificationType.INFORMATION);
            Notifications.Bus.notify(notification, null);
            return;
        }
        text = StringEscapeUtils.unescapeJava(text);
        Json2Java.INSTANCE.useLombok(false).writeBean(text, "RootBean", "Json2Java", event.getProject().getBasePath());
    }
}
