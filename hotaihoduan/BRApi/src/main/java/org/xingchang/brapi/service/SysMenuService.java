package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xingchang.brapi.entity.SysMenu;
import org.xingchang.brapi.mapper.SysMenuMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单Service
 */
@Service
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {
    
    /**
     * 获取菜单树
     */
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = list();
        // 构建树形结构
        return allMenus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .peek(menu -> menu.setChildren(getChildren(menu.getId(), allMenus)))
                .collect(Collectors.toList());
    }
    
    /**
     * 递归获取子菜单
     */
    private List<SysMenu> getChildren(Long parentId, List<SysMenu> allMenus) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> menu.setChildren(getChildren(menu.getId(), allMenus)))
                .collect(Collectors.toList());
    }
}
