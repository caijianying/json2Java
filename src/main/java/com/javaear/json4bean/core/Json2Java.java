package com.javaear.json4bean.core;

import com.intellij.ide.lightEdit.LightEditService;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.javaear.json4bean.exception.Json4BeanIOException;
import com.javaear.json4bean.util.ArrayUtils;
import com.javaear.json4bean.util.IOUtils;
import com.javaear.json4bean.util.MapUtils;
import com.javaear.json4bean.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author caijy
 * @description json转换为java
 * @date 2023/3/12 星期日 8:10 下午
 */
public class Json2Java {

    private boolean useLombok;

    private static final String DEFAULT_DEST_DIR = System.getProperty("user.dir");

    public static final Json2Java INSTANCE = new Json2Java();

    public Json2Java useLombok(Boolean useLombok) {
        this.useLombok = useLombok;
        return this;
    }

    private Boolean useLombok() {
        return this.useLombok;
    }

    public void writeBean(String jsonStr, String className, String packageName, String destDir) {
        try {
            if (destDir.equals(DEFAULT_DEST_DIR) && !StringUtils.isEmpty(packageName)) {
                destDir += File.separator + packageName.replace(".", File.separator);
            }
            if (!new File(destDir).exists()) {
                new File(destDir).mkdirs();
            }
            render(jsonStr, className, packageName, destDir);
        } catch (IOException e) {
            throw new Json4BeanIOException(e);
        }
    }

    private static void render(String jsonStr, String className, String packageName, String destDir) throws IOException {
        String classBody = JsonSerializer.serialize(new JsonObject(jsonStr).getJsonObj(), className, StringUtils.PREFIX_SPACE, INSTANCE.useLombok());
        if (StringUtils.isCreateMultiBean) {
            for (Map<String, String> multiBean : ArrayUtils.multiBeans) {
                renderBody(multiBean.get("classBody"), multiBean.get("className"), packageName, destDir);
            }
        } else {
            renderBody(classBody, className, packageName, destDir);
        }
    }

    private static void renderBody(String classBody, String className, String packageName, String destDir) throws IOException {
        final String javaBeanContent = StringUtils.LINE + (StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("class")) ? "" : StringUtils.LINE) + MapUtils.CODE_TEMPLATE_MAP.get("class") +
                classBody;
        String copyright = MapUtils.CODE_TEMPLATE_MAP.get("copyright") + (StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("copyright")) ? "" : StringUtils.LINE);
        String packageLine = (StringUtils.isEmpty(packageName) ? "" : "package " + packageName + ";" + StringUtils.LINE + StringUtils.LINE);
        String importLines = javaBeanContent.contains("private List<") ? "import java.util.List;" + StringUtils.LINE : "";
        importLines += INSTANCE.useLombok ? "import lombok.Setter;" + StringUtils.LINE + "import lombok.Getter;" + StringUtils.LINE : "";

        String filePath = destDir + File.separatorChar + className + ".java";
        IOUtils.write(new StringBuilder().append(copyright).append(packageLine).append(importLines).append(javaBeanContent).toString().trim(),
                filePath);
        try {
            infoOpenFile("java文件已生成！", filePath);
        } catch (Throwable ex) {

        }
    }

    public static void infoOpenFile(String message, String filePath) {
        Notification notification = new Notification("Print", "提示", message, NotificationType.INFORMATION);
        // 为顶层通知添加 Action，触发 Action 会弹出一个新的通知
        notification.addAction(new NotificationAction("打开文件") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(filePath);
                LightEditService.getInstance().openFile(virtualFile);
            }
        });
        Notifications.Bus.notify(notification, null);
    }
}
