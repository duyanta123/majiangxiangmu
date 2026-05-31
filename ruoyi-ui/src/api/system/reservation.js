import request from '@/utils/request'

export function listReservation(query) {
  return request({ url: '/system/reservation/list', method: 'get', params: query })
}

export function getReservation(reservationId) {
  return request({ url: '/system/reservation/' + reservationId, method: 'get' })
}

export function addReservation(data) {
  return request({ url: '/system/reservation', method: 'post', data: data })
}

export function updateReservation(data) {
  return request({ url: '/system/reservation', method: 'put', data: data })
}

export function delReservation(reservationIds) {
  return request({ url: '/system/reservation/' + reservationIds, method: 'delete' })
}
