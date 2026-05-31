import request from '@/utils/request'

export function listTableInfo(query) {
  return request({ url: '/system/tableInfo/list', method: 'get', params: query })
}

export function getAllTables() {
  return request({ url: '/system/tableInfo/all', method: 'get' })
}

export function getTableInfo(tableId) {
  return request({ url: '/system/tableInfo/' + tableId, method: 'get' })
}

export function addTableInfo(data) {
  return request({ url: '/system/tableInfo', method: 'post', data: data })
}

export function updateTableInfo(data) {
  return request({ url: '/system/tableInfo', method: 'put', data: data })
}

export function delTableInfo(tableIds) {
  return request({ url: '/system/tableInfo/' + tableIds, method: 'delete' })
}

export function updateTableStatus(tableId, tableStatus) {
  return request({ url: '/system/tableInfo/status/' + tableId + '/' + tableStatus, method: 'put' })
}
