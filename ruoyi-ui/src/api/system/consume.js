import request from '@/utils/request'

export function listConsume(query) {
  return request({ url: '/system/consume/list', method: 'get', params: query })
}

export function getConsume(consumeId) {
  return request({ url: '/system/consume/' + consumeId, method: 'get' })
}

export function addConsume(data) {
  return request({ url: '/system/consume', method: 'post', data: data })
}

export function updateConsume(data) {
  return request({ url: '/system/consume', method: 'put', data: data })
}

export function delConsume(consumeIds) {
  return request({ url: '/system/consume/' + consumeIds, method: 'delete' })
}

export function exportConsume(query) {
  return request({ url: '/system/consume/export', method: 'get', params: query, responseType: 'blob' })
}
