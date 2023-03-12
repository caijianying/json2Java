//package com.tool.utils;
//
//import cn.hutool.json.JSONUtil;
//import com.intellij.openapi.actionSystem.DataContext;
//import com.intellij.openapi.editor.Editor;
//import com.javaear.json4bean.JSON;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.NotNull;
//
///**
// * @author caijy
// * @description TODO
// * @date 2022/11/5 星期六 10:43 下午
// */
//public class JsonUtils {
//
//    public static void executeJsonToJava(@NotNull Editor editor, DataContext dataContext, String text) {
//        try {
//            // 内容为空不处理
//            if (StringUtils.isBlank(text)) {
//                return;
//            }
//            // 若为json，则转成JAVA文件
//            if (JSONUtil.isJson(text)) {
//                JSON.writeBean(text,"com.json2Java.RootBean");
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return;
//        } finally {
//
//        }
//    }
//}
