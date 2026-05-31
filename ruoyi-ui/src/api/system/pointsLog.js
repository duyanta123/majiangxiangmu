import request from '@/utils/request'

export function listPointsLog(query) {
  return request({ url: '/system/pointsLog/list', method: 'get', params: query })
}

export function getPointsLog(logId) {
  return request({ url: '/system/pointsLog/' + logId, method: 'get' })
}

export function delPointsLog(logIds) {
  return request({ url: '/system/pointsLog/' + logIds, method: 'delete' })
}
