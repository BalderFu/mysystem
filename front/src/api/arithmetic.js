import request from '@/utils/request'


/**
 * 根据ID获取算法详情
 * @param {Object} params - 请求参数: {id: 算法ID}
 * @returns {Promise} - 请求结果
 */
export function getArithmeticById(params) {
  return request({
    url: '/arithmetic/getById',
    method: 'get',
    params
  })
}

/**
 * 新增算法
 * @param {Object} data - 算法信息
 * @returns {Promise} - 请求结果
 */
export function addArithmetic(data) {
  return request({
    url: '/arithmetic/add',
    method: 'post',
    data
  })
}

/**
 * 更新算法
 * @param {Object} data - 算法信息
 * @returns {Promise} - 请求结果
 */
export function updateArithmetic(data) {
  return request({
    url: '/arithmetic/update',
    method: 'post',
    data
  })
}

/**
 * 删除算法
 * @param {Object} params - 请求参数: {id: 算法ID}
 * @returns {Promise} - 请求结果
 */
export function deleteArithmetic(params) {
  return request({
    url: '/arithmetic/delete',
    method: 'delete',
    params
  })
}

/**
 * 获取算法统计数据
 * @returns {Promise} - 请求结果
 */
export function getArithmeticStats() {
  return request({
    url: '/statistic/stats',
    method: 'get'
  })
} 