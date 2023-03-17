package com.javaear.json4bean.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.javaear.json4bean.util.ArrayUtils;
import com.javaear.json4bean.util.MapUtils;
import com.javaear.json4bean.util.StringUtils;

/**
 * @author caijy
 */
abstract class JsonSerializer implements Serializable {

    /**
     * 校验json
     *
     * @param json json
     */
    private static void validate(Object json) {
        if (json == null || !(json instanceof LinkedHashMap)) {
            throw new IllegalArgumentException("jsonStr validate exception");
        }
    }

    /**
     * 序列化json对象
     *
     * @param jsonObj          jsonObj
     * @param className        className
     * @param finalPrefixSpace 前置空间
     * @param useLombok        是否视同Lombok
     * @return bean content内容
     */
    public static String serialize(Object jsonObj, String className, final String finalPrefixSpace, Boolean useLombok) {
        validate(jsonObj);
        StringBuilder stringBuilder = new StringBuilder(StringUtils.LINE);
        StringBuilder methodBuilder = new StringBuilder();
        StringBuilder innerBuilder = new StringBuilder();
        final String prefixSpace = StringUtils.isCreateMultiBean ? StringUtils.PREFIX_SPACE : finalPrefixSpace;
        if (useLombok) {
            stringBuilder.append(prefixSpace.substring(4)).append("@Setter").append(StringUtils.LINE);
            stringBuilder.append(prefixSpace.substring(4)).append("@Getter").append(StringUtils.LINE);
        }
        stringBuilder.append(prefixSpace.substring(4)).append("public ").append(
            StringUtils.PREFIX_SPACE.equals(finalPrefixSpace) ? "" : "static ").append("class ").append(className)
            .append(" {");
        ((LinkedHashMap<String, ?>)jsonObj).entrySet().stream().forEach(e -> {
            String key = StringUtils.upperCaseFirstChar(e.getKey());
            Object value = e.getValue();
            //list集合
            if (value instanceof ArrayList) {
                if (((ArrayList)value).size() < 1) {
                    throw new IllegalArgumentException("jsonListNode maybe null! nodekey is ".concat(key));
                }
                Object fieldFirst = ((ArrayList)value).get(0);
                String fieldType = fieldFirst.getClass().getSimpleName();
                if (fieldFirst instanceof ArrayList) {
                    fieldType = "List";
                }
                if (fieldFirst instanceof LinkedHashMap) {
                    fieldType = key;
                }
                stringBuilder.append(newLine(prefixSpace)).append("@JSONField(name=\"").append(e.getKey()).append(
                    "\")");
                stringBuilder.append(newLine(prefixSpace)).append("private ").append("List<").append(fieldType).append(
                    ">").append(" ").append(StringUtils.underlineToCamel(e.getKey())).append(";");
                if (!useLombok) {
                    appendGetterSetter(methodBuilder, prefixSpace, "List<" + fieldType + ">", e.getKey());
                }
                if (fieldFirst instanceof ArrayList || fieldFirst instanceof LinkedHashMap) {
                    innerBuilder.append(StringUtils.LINE).append(
                        serialize(fieldFirst, key, prefixSpace + prefixSpace, useLombok));
                }
            } else if (value instanceof LinkedHashMap) {
                stringBuilder.append(newLine(prefixSpace)).append("@JSONField(name=\"").append(e.getKey()).append(
                    "\")");
                stringBuilder.append(newLine(prefixSpace)).append("private ").append(key).append(" ").append(
                    StringUtils.underlineToCamel(StringUtils.underlineToCamel(e.getKey()))).append(";");
                if (!useLombok) {
                    appendGetterSetter(methodBuilder, prefixSpace, key, StringUtils.underlineToCamel(e.getKey()));
                }
                innerBuilder.append(StringUtils.LINE).append(
                    serialize(value, key, prefixSpace + prefixSpace, useLombok));
            } else {
                stringBuilder.append(newLine(prefixSpace)).append("@JSONField(name=\"").append(e.getKey()).append(
                    "\")");
                stringBuilder.append(newLine(prefixSpace)).append("private ").append(
                    value == null ? Object.class.getSimpleName() : value.getClass().getSimpleName()).append(" ").append(
                    StringUtils.underlineToCamel(e.getKey())).append(";");
                if (!useLombok) {
                    appendGetterSetter(methodBuilder, prefixSpace, key, StringUtils.underlineToCamel(e.getKey()));
                }
            }
        });
        if (StringUtils.isCreateMultiBean) {
            ArrayUtils.multiBeans.add(new HashMap<String, String>() {{
                put("className", className);
                put("classBody", stringBuilder.append(methodBuilder).append(newLine(prefixSpace.substring(4)))
                    .append("}").toString());
            }});
        }
        stringBuilder.append(innerBuilder).append(methodBuilder).append(newLine(prefixSpace.substring(4))).append("}");
        return stringBuilder.toString();
    }

    /**
     * 追加getter setter
     *
     * @param sourceBuilder sourceStr 源字符串
     * @param prefixSpace   前置空间
     * @param fieldType     字段类型
     * @param fieldName     字段名称
     */
    private static void appendGetterSetter(StringBuilder sourceBuilder, String prefixSpace, String fieldType,
        String fieldName) {
        sourceBuilder
            .append(StringUtils.LINE)
            .append(newLine(prefixSpace))
            .append(MapUtils.CODE_TEMPLATE_MAP.get("getter").replace("${field_name}", fieldName)
                .replace("\r\n", "\r\n" + prefixSpace))
            .append((StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("getter")) ? "" : newLine(prefixSpace)))
            .append("public ").append(fieldType).append(" get").append(StringUtils.upperCaseFirstChar(fieldName))
            .append("() {").append(newLine(prefixSpace + StringUtils.PREFIX_SPACE)).append("return this.").append(
            fieldName).append(";").append(newLine(prefixSpace)).append("}")
            .append(StringUtils.LINE)
            .append(newLine(prefixSpace))
            .append(MapUtils.CODE_TEMPLATE_MAP.get("setter").replace("${field_name}", fieldName)
                .replace("\r\n", "\r\n" + prefixSpace))
            .append((StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("setter")) ? "" : newLine(prefixSpace)))
            .append("public void set").append(StringUtils.upperCaseFirstChar(fieldName)).append("(").append(fieldType)
            .append(" ").append(fieldName).append(") {").append(newLine(prefixSpace + StringUtils.PREFIX_SPACE)).append(
            "this.").append(fieldName).append(" = ").append(fieldName).append(";").append(newLine(prefixSpace)).append(
            "}");
    }

    /**
     * 新行 前置空间
     *
     * @param prefixSpace prefixSpace
     * @return newLineStr
     */
    private static String newLine(String prefixSpace) {
        return StringUtils.LINE + prefixSpace;
    }

    /**
     * 序列化json对象
     *
     * @param jsonObj jsonObj
     * @return json content内容
     */
    static String serialize(Object jsonObj) {
        validate(jsonObj);
        StringBuilder stringBuilder = new StringBuilder("{");
        ((LinkedHashMap<?, ?>)jsonObj).entrySet().stream().forEach(e -> {
            String key = StringUtils.upperCaseFirstChar((String)e.getKey());
            Object value = e.getValue();
            //list集合
            if (value instanceof ArrayList) {
                //StringBuilder listBuilder=new StringBuilder();
                String str = (String)((ArrayList)value).stream().reduce((v1, v2) -> serialize(v1).concat(",")
                    .concat(serialize(v2))).get();
                stringBuilder.append(key).append(":").append("[").append(str).append("]");
            } else if (value instanceof LinkedHashMap) {
                stringBuilder.append(key).append(":").append(serialize(value)).append(",");
            } else {
                appendEntry(stringBuilder, e);
            }
        });
        return stringBuilder.toString().endsWith(",") ?
            stringBuilder.substring(0, stringBuilder.toString().length() - 1).concat("}")
            : stringBuilder.toString().concat("}");
    }

    private static void appendEntry(StringBuilder stringBuilder, Map.Entry<?, ?> entry) {
        stringBuilder.append("\"")
            .append(entry.getKey()).append(":")
            .append("\"")
            .append(entry.getValue() instanceof String ? "\"" : "")
            .append(entry.getValue())
            .append(entry.getValue() instanceof String ? "\"" : "")
            .append(",");
    }
}
