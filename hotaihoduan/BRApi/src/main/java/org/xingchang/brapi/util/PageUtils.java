package org.xingchang.brapi.util;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 分页工具类
 */
public class PageUtils {
    
    /**
     * 从参数中获取页码，支持 page 和 pageNum 两种参数名
     */
    public static Integer getPageNum(Map<String, Object> params) {
        if (params.get("page") != null) {
            return Integer.valueOf(params.get("page").toString());
        } else if (params.get("pageNum") != null) {
            return Integer.valueOf(params.get("pageNum").toString());
        }
        return 1; // 默认第1页
    }
    
    /**
     * 从参数中获取每页大小，支持 size 和 pageSize 两种参数名
     */
    public static Integer getPageSize(Map<String, Object> params) {
        if (params.get("size") != null) {
            return Integer.valueOf(params.get("size").toString());
        } else if (params.get("pageSize") != null) {
            return Integer.valueOf(params.get("pageSize").toString());
        }
        return 10; // 默认每页10条
    }
    
    /**
     * 从参数中获取整数值
     */
    public static Integer getInteger(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        // 处理空字符串
        String strValue = value.toString().trim();
        if (!StringUtils.hasText(strValue)) {
            return null;
        }
        try {
            if (value instanceof Integer) {
                return (Integer) value;
            }
            return Integer.valueOf(strValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 从参数中获取长整数值
     */
    public static Long getLong(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        // 处理空字符串
        String strValue = value.toString().trim();
        if (!StringUtils.hasText(strValue)) {
            return null;
        }
        try {
            if (value instanceof Long) {
                return (Long) value;
            }
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            }
            return Long.valueOf(strValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 从参数中获取字符串值
     */
    public static String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        String strValue = value.toString().trim();
        return StringUtils.hasText(strValue) ? strValue : null;
    }
    
    /**
     * 从参数中获取布尔值
     */
    public static Boolean getBoolean(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String strValue = value.toString().trim().toLowerCase();
        return "true".equals(strValue) || "1".equals(strValue);
    }
}
