/**
 * 图片处理工具函数
 */

// 默认占位图
const DEFAULT_PLACEHOLDER = '/images/placeholder.svg';
const DEFAULT_AVATAR = '/images/default-avatar.svg';

/**
 * 处理图片 URL
 * @param {string} url - 图片 URL
 * @param {string} type - 图片类型 'avatar' | 'cover' | 'content'
 * @returns {string} 处理后的图片 URL
 */
function handleImageUrl(url, type = 'content') {
  // 如果没有 URL，返回默认图
  if (!url) {
    return type === 'avatar' ? DEFAULT_AVATAR : DEFAULT_PLACEHOLDER;
  }

  // 如果是本地图片，直接返回
  if (url.startsWith('/') || url.startsWith('./')) {
    return url;
  }

  // 如果是远程图片（http/https），直接返回，让小程序加载
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }

  // 如果是临时文件路径（微信小程序临时文件）
  if (url.startsWith('wxfile://') || url.startsWith('http://tmp/')) {
    return url;
  }

  // 其他情况返回原 URL
  return url;
}

/**
 * 处理图片数组
 * @param {Array} images - 图片 URL 数组
 * @returns {Array} 处理后的图片 URL 数组
 */
function handleImageList(images) {
  if (!Array.isArray(images)) {
    return [];
  }
  return images.map(url => handleImageUrl(url, 'content'));
}

/**
 * 处理头像 URL
 * @param {string} url - 头像 URL
 * @returns {string} 处理后的头像 URL
 */
function handleAvatarUrl(url) {
  return handleImageUrl(url, 'avatar');
}

/**
 * 处理封面图 URL
 * @param {string} url - 封面图 URL
 * @returns {string} 处理后的封面图 URL
 */
function handleCoverUrl(url) {
  return handleImageUrl(url, 'cover');
}

module.exports = {
  handleImageUrl,
  handleImageList,
  handleAvatarUrl,
  handleCoverUrl
};
