package org.xingchang.brapi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xingchang.brapi.entity.SysDept;
import org.xingchang.brapi.mapper.SysDeptMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门Service
 */
@Service
public class SysDeptService extends ServiceImpl<SysDeptMapper, SysDept> {
    
    /**
     * 获取部门树
     */
    public List<SysDept> getDeptTree() {
        List<SysDept> allDepts = list();
        // 构建树形结构
        return allDepts.stream()
                .filter(dept -> dept.getParentId() == 0)
                .peek(dept -> dept.setChildren(getChildren(dept.getId(), allDepts)))
                .collect(Collectors.toList());
    }
    
    /**
     * 递归获取子部门
     */
    private List<SysDept> getChildren(Long parentId, List<SysDept> allDepts) {
        return allDepts.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .peek(dept -> dept.setChildren(getChildren(dept.getId(), allDepts)))
                .collect(Collectors.toList());
    }
}
