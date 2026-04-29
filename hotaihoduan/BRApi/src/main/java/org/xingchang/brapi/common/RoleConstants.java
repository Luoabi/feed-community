package org.xingchang.brapi.common;

import java.util.Arrays;
import java.util.List;

/**
 * 角色常量
 * 
 * 角色ID对应关系（根据data.sql初始化数据）：
 * 1 - 超级管理员 (admin)
 * 2 - 普通管理员 (manager)
 * 3 - 内容创作者 (editor)
 * 4 - 普通用户 (user)
 */
public class RoleConstants {
    
    /**
     * 超级管理员角色ID
     */
    public static final Long ROLE_SUPER_ADMIN_ID = 1L;
    
    /**
     * 普通管理员角色ID
     */
    public static final Long ROLE_MANAGER_ID = 2L;
    
    /**
     * 内容创作者角色ID
     */
    public static final Long ROLE_EDITOR_ID = 3L;
    
    /**
     * 普通用户角色ID
     */
    public static final Long ROLE_USER_ID = 4L;
    
    /**
     * 超级管理员角色标识
     */
    public static final String ROLE_SUPER_ADMIN_KEY = "admin";
    
    /**
     * 普通管理员角色标识
     */
    public static final String ROLE_MANAGER_KEY = "manager";
    
    /**
     * 内容创作者角色标识
     */
    public static final String ROLE_EDITOR_KEY = "editor";
    
    /**
     * 普通用户角色标识
     */
    public static final String ROLE_USER_KEY = "user";
    
    /**
     * 超级管理员角色名称
     */
    public static final String ROLE_SUPER_ADMIN_NAME = "超级管理员";
    
    /**
     * 普通管理员角色名称
     */
    public static final String ROLE_MANAGER_NAME = "普通管理员";
    
    /**
     * 内容创作者角色名称
     */
    public static final String ROLE_EDITOR_NAME = "内容创作者";
    
    /**
     * 普通用户角色名称
     */
    public static final String ROLE_USER_NAME = "普通用户";
    
    /**
     * 默认角色ID列表（用于小程序注册新用户）
     * 普通用户角色ID = 4
     */
    public static final List<Long> DEFAULT_ROLE_ID_LIST = Arrays.asList(ROLE_USER_ID);
    
    /**
     * 默认角色名称（用于小程序注册新用户）
     */
    public static final String DEFAULT_ROLE_NAMES = ROLE_USER_NAME;
}
