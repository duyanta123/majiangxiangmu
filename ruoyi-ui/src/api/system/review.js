import request from '@/utils/request'

export function listReview(query) {
  return request({ url: '/system/review/list', method: 'get', params: query })
}

export function getReview(reviewId) {
  return request({ url: '/system/review/' + reviewId, method: 'get' })
}

export function addReview(data) {
  return request({ url: '/system/review', method: 'post', data: data })
}

export function updateReview(data) {
  return request({ url: '/system/review', method: 'put', data: data })
}

export function delReview(reviewIds) {
  return request({ url: '/system/review/' + reviewIds, method: 'delete' })
}
