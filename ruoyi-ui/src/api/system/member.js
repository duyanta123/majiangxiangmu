import request from '@/utils/request'

export function listMember(query) {
  return request({ url: '/system/member/list', method: 'get', params: query })
}

export function getMember(memberId) {
  return request({ url: '/system/member/' + memberId, method: 'get' })
}

export function addMember(data) {
  return request({ url: '/system/member', method: 'post', data: data })
}

export function updateMember(data) {
  return request({ url: '/system/member', method: 'put', data: data })
}

export function delMember(memberIds) {
  return request({ url: '/system/member/' + memberIds, method: 'delete' })
}

export function addPoints(memberId, points, description) {
  return request({ url: '/system/member/addPoints/' + memberId + '/' + points, method: 'post', params: { description } })
}

export function deductPoints(memberId, points, description) {
  return request({ url: '/system/member/deductPoints/' + memberId + '/' + points, method: 'post', params: { description } })
}
