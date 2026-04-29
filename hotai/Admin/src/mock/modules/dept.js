import Mock from 'mockjs'

// 部门列表（树形）
export const getDeptList = () => {
  return Mock.mock({
    code: 200,
    message: '成功',
    data: [
      {
        id: 1,
        parentId: 0,
        deptName: '总公司',
        leader: '@cname',
        phone: /^1[3-9]\d{9}$/,
        email: '@email',
        sort: 1,
        status: 1,
        createTime: '@datetime',
        updateTime: '@datetime',
        children: [
          {
            id: 11,
            parentId: 1,
            deptName: '技术部',
            leader: '@cname',
            phone: /^1[3-9]\d{9}$/,
            email: '@email',
            sort: 1,
            status: 1,
            createTime: '@datetime',
            updateTime: '@datetime',
            children: [
              {
                id: 111,
                parentId: 11,
                deptName: '前端组',
                leader: '@cname',
                phone: /^1[3-9]\d{9}$/,
                email: '@email',
                sort: 1,
                status: 1,
                createTime: '@datetime',
                updateTime: '@datetime'
              },
              {
                id: 112,
                parentId: 11,
                deptName: '后端组',
                leader: '@cname',
                phone: /^1[3-9]\d{9}$/,
                email: '@email',
                sort: 2,
                status: 1,
                createTime: '@datetime',
                updateTime: '@datetime'
              }
            ]
          },
          {
            id: 12,
            parentId: 1,
            deptName: '运营部',
            leader: '@cname',
            phone: /^1[3-9]\d{9}$/,
            email: '@email',
            sort: 2,
            status: 1,
            createTime: '@datetime',
            updateTime: '@datetime'
          },
          {
            id: 13,
            parentId: 1,
            deptName: '市场部',
            leader: '@cname',
            phone: /^1[3-9]\d{9}$/,
            email: '@email',
            sort: 3,
            status: 1,
            createTime: '@datetime',
            updateTime: '@datetime'
          }
        ]
      }
    ]
  })
}

// 新增部门
export const addDept = () => {
  return Mock.mock({
    code: 200,
    message: '新增成功',
    data: {
      id: '@increment'
    }
  })
}

// 更新部门
export const updateDept = () => {
  return Mock.mock({
    code: 200,
    message: '更新成功',
    data: null
  })
}

// 删除部门
export const deleteDept = () => {
  return Mock.mock({
    code: 200,
    message: '删除成功',
    data: null
  })
}

// 注册Mock接口
Mock.mock(/\/api\/dept\/list/, 'get', getDeptList)
Mock.mock(/\/api\/dept\/add/, 'post', addDept)
Mock.mock(/\/api\/dept\/update/, 'put', updateDept)
Mock.mock(/\/api\/dept\/delete/, 'delete', deleteDept)
