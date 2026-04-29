package org.xingchang.brapi.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 操作类型：create-新增, update-修改, delete-删除, query-查询, login-登录, logout-登出
     */
    String operationType() default "";
    
    /**
     * 操作描述
     */
    String description() default "";
}
