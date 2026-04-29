package org.xingchang.brapi.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xingchang.brapi.annotation.OperationLog;
import org.xingchang.brapi.entity.SysLog;
import org.xingchang.brapi.service.SysLogService;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 自动记录带有 @OperationLog 注解的方法的操作日志
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final SysLogService logService;
    private final ObjectMapper objectMapper;
    
    @Pointcut("@annotation(org.xingchang.brapi.annotation.OperationLog)")
    public void logPointcut() {
    }
    
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 创建日志对象
        SysLog sysLog = new SysLog();
        sysLog.setCreateTime(LocalDateTime.now());
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        if (operationLog != null) {
            sysLog.setModule(operationLog.module());
            sysLog.setOperationType(operationLog.operationType());
            sysLog.setDescription(operationLog.description());
        }
        
        // 获取请求信息
        if (request != null) {
            sysLog.setMethod(request.getMethod());
            sysLog.setUrl(request.getRequestURI());
            sysLog.setIp(getIpAddress(request));
            sysLog.setBrowser(getBrowser(request));
            sysLog.setOs(getOs(request));
            
            // 获取请求参数
            try {
                Object[] args = point.getArgs();
                if (args != null && args.length > 0) {
                    String params = objectMapper.writeValueAsString(args);
                    // 限制参数长度
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000) + "...";
                    }
                    sysLog.setParams(params);
                }
            } catch (Exception e) {
                log.error("记录请求参数失败", e);
            }
        }
        
        // 执行方法
        Object result = null;
        try {
            result = point.proceed();
            sysLog.setStatus(1); // 成功
        } catch (Exception e) {
            sysLog.setStatus(0); // 失败
            sysLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时长
            long duration = System.currentTimeMillis() - beginTime;
            sysLog.setDuration(duration);
            
            // 异步保存日志
            try {
                logService.save(sysLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
        
        return result;
    }
    
    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    /**
     * 获取浏览器信息
     */
    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        
        if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "IE";
        }
        return "Other";
    }
    
    /**
     * 获取操作系统信息
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        return "Other";
    }
}
