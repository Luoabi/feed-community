package org.xingchang.brapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器和跨域
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final JwtInterceptor jwtInterceptor;
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要认证的路径
                .excludePathPatterns(
                        // ==================== 用户认证相关 ====================
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/logout",
                        "/user/login",
                        "/user/register",
                        "/user/logout",
                        
                        // ==================== 公开内容接口（无需登录）====================
                        // 首页 Feed 流
                        "/api/content/list",
                        "/content/list",
                        
                        // 内容详情
                        "/api/content/detail/**",
                        "/content/detail/**",
                        
                        // 社区帖子列表（使用 content/list，已在上面排除）
                        "/api/post/list",
                        "/post/list",
                        "/api/post/detail/**",
                        "/post/detail/**",
                        
                        // 分类列表
                        "/api/category/list",
                        "/category/list",
                        
                        // 标签列表
                        "/api/tag/list",
                        "/tag/list",
                        
                        // 评论列表（查看评论无需登录）
                        "/api/comment/list",
                        "/comment/list",
                        "/api/comment/tree",
                        "/comment/tree",
                        "/api/comment/replies/**",
                        "/comment/replies/**",
                        
                        // 检查评论点赞状态（无需登录）
                        "/api/comment/check-like-status",
                        "/comment/check-like-status",
                        
                        // 检查点赞收藏状态（无需登录，未登录返回 false）
                        "/api/interaction/check-status",
                        "/interaction/check-status",
                        
                        // Feed 流
                        "/api/feed/**",
                        "/feed/**",
                        
                        // ==================== 文件上传（需要登录）====================
                        // 注意：文件上传需要登录，所以不在排除列表中
                        
                        // ==================== Swagger文档 ====================
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        
                        // ==================== 静态资源 ====================
                        "/static/**",
                        "/public/**",
                        "/uploads/**",  // 上传文件访问路径
                        
                        // ==================== 错误页面 ====================
                        "/error"
                );
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    /**
     * 配置静态资源映射
     * 将 /uploads/** 映射到本地上传目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ 使用绝对路径或项目根目录
        String uploadLocation;
        if (uploadPath.startsWith("/") || uploadPath.contains(":")) {
            // 已经是绝对路径
            uploadLocation = "file:" + uploadPath + "/";
        } else {
            // 相对路径，使用项目根目录
            String projectPath = System.getProperty("user.dir");
            uploadLocation = "file:" + projectPath + "/" + uploadPath + "/";
        }
        
        // 上传文件访问路径映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}
