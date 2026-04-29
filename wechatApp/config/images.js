/**
 * 图片资源配置
 * 提供默认图片的在线地址和本地路径
 */

// 使用在线占位图（开发阶段临时方案）
const ONLINE_IMAGES = {
  // 默认头像 - 灰色背景
  DEFAULT_AVATAR: 'https://via.placeholder.com/200/e0e0e0/999999?text=Avatar',
  
  // Logo
  DEFAULT_LOGO: 'https://via.placeholder.com/200/667eea/ffffff?text=Logo',
  
  // TabBar 图标（灰色 - 未选中）
  ICON_HOME: 'https://via.placeholder.com/81/999999/999999?text=H',
  ICON_COMMUNITY: 'https://via.placeholder.com/81/999999/999999?text=C',
  ICON_PUBLISH: 'https://via.placeholder.com/81/999999/999999?text=+',
  ICON_USER: 'https://via.placeholder.com/81/999999/999999?text=U',
  
  // TabBar 图标（蓝色 - 选中）
  ICON_HOME_ACTIVE: 'https://via.placeholder.com/81/1890ff/1890ff?text=H',
  ICON_COMMUNITY_ACTIVE: 'https://via.placeholder.com/81/1890ff/1890ff?text=C',
  ICON_PUBLISH_ACTIVE: 'https://via.placeholder.com/81/1890ff/1890ff?text=+',
  ICON_USER_ACTIVE: 'https://via.placeholder.com/81/1890ff/1890ff?text=U'
}

// 本地图片路径（生产环境使用）
const LOCAL_IMAGES = {
  DEFAULT_AVATAR: '/images/default-avatar.png',
  DEFAULT_LOGO: '/images/logo.png',
  ICON_HOME: '/images/home.png',
  ICON_HOME_ACTIVE: '/images/home-active.png',
  ICON_COMMUNITY: '/images/community.png',
  ICON_COMMUNITY_ACTIVE: '/images/community-active.png',
  ICON_PUBLISH: '/images/publish.png',
  ICON_PUBLISH_ACTIVE: '/images/publish-active.png',
  ICON_USER: '/images/user.png',
  ICON_USER_ACTIVE: '/images/user-active.png'
}

// 配置：true 使用在线图片，false 使用本地图片
const USE_ONLINE_IMAGES = true

// 导出当前使用的图片配置
const IMAGES = USE_ONLINE_IMAGES ? ONLINE_IMAGES : LOCAL_IMAGES

module.exports = {
  ...IMAGES,
  USE_ONLINE_IMAGES,
  ONLINE_IMAGES,
  LOCAL_IMAGES
}
