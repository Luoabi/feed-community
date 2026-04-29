package org.xingchang.brapi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 JSON 字符串列表类型处理器
 * 用于处理 MySQL JSON 类型字段与 Java List<String> 的转换
 * 支持 VARCHAR, LONGVARCHAR, BLOB 等多种 JDBC 类型
 */
@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.LONGVARCHAR, JdbcType.BLOB})
public class JsonStringListTypeHandler extends BaseTypeHandler<List<String>> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            // 根据 JDBC 类型选择合适的设置方法
            if (jdbcType == JdbcType.BLOB) {
                ps.setBlob(i, new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
            } else {
                ps.setString(i, json);
            }
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting List to JSON string", e);
        }
    }
    
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(getJsonString(rs, columnName));
    }
    
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(getJsonString(rs, columnIndex));
    }
    
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(getJsonString(cs, columnIndex));
    }
    
    /**
     * 从 ResultSet 获取 JSON 字符串
     * 支持 VARCHAR 和 BLOB 类型
     */
    private String getJsonString(ResultSet rs, String columnName) throws SQLException {
        // 先尝试作为字符串读取
        String value = rs.getString(columnName);
        if (value != null) {
            return value;
        }
        
        // 如果字符串为 null，尝试作为 BLOB 读取
        Blob blob = rs.getBlob(columnName);
        if (blob != null) {
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return new String(bytes, StandardCharsets.UTF_8);
        }
        
        return null;
    }
    
    /**
     * 从 ResultSet 获取 JSON 字符串（通过索引）
     */
    private String getJsonString(ResultSet rs, int columnIndex) throws SQLException {
        // 先尝试作为字符串读取
        String value = rs.getString(columnIndex);
        if (value != null) {
            return value;
        }
        
        // 如果字符串为 null，尝试作为 BLOB 读取
        Blob blob = rs.getBlob(columnIndex);
        if (blob != null) {
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return new String(bytes, StandardCharsets.UTF_8);
        }
        
        return null;
    }
    
    /**
     * 从 CallableStatement 获取 JSON 字符串
     */
    private String getJsonString(CallableStatement cs, int columnIndex) throws SQLException {
        // 先尝试作为字符串读取
        String value = cs.getString(columnIndex);
        if (value != null) {
            return value;
        }
        
        // 如果字符串为 null，尝试作为 BLOB 读取
        Blob blob = cs.getBlob(columnIndex);
        if (blob != null) {
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return new String(bytes, StandardCharsets.UTF_8);
        }
        
        return null;
    }
    
    /**
     * 解析 JSON 字符串为 List<String>
     */
    private List<String> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 尝试解析为 List<String>
            List<String> result = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            System.out.println("✅ 成功解析 JSON: " + json + " -> " + result);
            return result;
        } catch (JsonProcessingException e) {
            System.err.println("❌ 解析 JSON 失败: " + json);
            e.printStackTrace();
            // 返回空列表而不是 null，避免前端出错
            return new ArrayList<>();
        }
    }
}
