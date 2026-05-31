import request from '@/utils/request'

export function listTableType(query) {
  return request({ url: '/system/tableType/list', method: 'get', params: query })
}

export function getAllTypes() {
  return request({ url: '/system/tableType/all', method: 'get' })
}

export function getTableType(typeId) {
  return request({ url: '/system/tableType/' + typeId, method: 'get' })
}

export function addTableType(data) {
  return request({ url: '/system/tableType', method: 'post', data: data })
}

export function updateTableType(data) {
  return request({ url: '/system/tableType', method: 'put', data: data })
}

export function delTableType(typeIds) {
  return request({ url: '/system/tableType/' + typeIds, method: 'delete' })
}
