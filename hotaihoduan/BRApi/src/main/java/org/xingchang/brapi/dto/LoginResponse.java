package org.xingchang.brapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.xingchang.brapi.entity.SysUser;

import java.util.List;

/**
 * 登录响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private SysUser userInfo;
    private List<String> roles;
}
