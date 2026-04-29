package org.xingchang.brapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xingchang.brapi.common.Result;
import org.xingchang.brapi.util.JwtUtil;

/**
 * JWT拦截器
 * 拦截需要认证的请求，验证Token
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 获取Token
        String token = getTokenFromRequest(request);
        
        // Token为空
        if (!StringUtils.hasText(token)) {
            sendErrorResponse(response, 401, "未登录，请先登录");
            return false;
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            sendErrorResponse(response, 401, "Token无效或已过期，请重新登录");
            return false;
        }
        
        // 从Token中获取用户信息，存入request
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        
        return true;
    }
    
    /**
     * 从请求中获取Token
     * 支持两种方式：
     * 1. Header: Authorization: Bearer <token>
     * 2. Header: token: <token>
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 方式1: Authorization: Bearer <token>
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 方式2: token: <token>
        String token = request.getHeader("token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        
        return null;
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(200); // HTTP状态码仍为200
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Void> result = Result.error(code, message);
        String json = objectMapper.writeValueAsString(result);
        
        response.getWriter().write(json);
    }
}
