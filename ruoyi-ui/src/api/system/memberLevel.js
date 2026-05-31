import request from '@/utils/request'

export function listMemberLevel(query) {
  return request({ url: '/system/memberLevel/list', method: 'get', params: query })
}

export function getAllLevels() {
  return request({ url: '/system/memberLevel/all', method: 'get' })
}

export function getMemberLevel(levelId) {
  return request({ url: '/system/memberLevel/' + levelId, method: 'get' })
}

export function addMemberLevel(data) {
  return request({ url: '/system/memberLevel', method: 'post', data: data })
}

export function updateMemberLevel(data) {
  return request({ url: '/system/memberLevel', method: 'put', data: data })
}

export function delMemberLevel(levelIds) {
  return request({ url: '/system/memberLevel/' + levelIds, method: 'delete' })
}
