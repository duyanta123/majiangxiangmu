import request from '@/utils/request'

export function getDashboard() {
  return request({ url: '/system/analysis/dashboard', method: 'get' })
}

export function getRevenueTrend(params) {
  return request({ url: '/system/analysis/revenue', method: 'get', params: params })
}

export function getMemberTrend(params) {
  return request({ url: '/system/analysis/member', method: 'get', params: params })
}

export function getStoreStats(storeId) {
  return request({ url: '/system/analysis/store/' + storeId, method: 'get' })
}

export function getMemberRanking(limit) {
  return request({ url: '/system/analysis/ranking', method: 'get', params: { limit } })
}

export function getTableTypeStats(storeId) {
  return request({ url: '/system/analysis/table', method: 'get', params: { storeId } })
}
