import request from '@/utils/request'

export function generateSchedule(storeId, date) {
  return request({ url: '/system/ai/schedule', method: 'post', params: { storeId, date } })
}

export function getSchedule(storeId, date) {
  return request({ url: '/system/ai/schedule', method: 'get', params: { storeId, date } })
}

export function getScheduleHistory(storeId) {
  return request({ url: '/system/ai/schedule', method: 'get', params: { storeId } })
}

export function updateSchedule(data) {
  return request({ url: '/system/ai/schedule', method: 'put', data: data })
}

export function deleteSchedule(scheduleId) {
  return request({ url: '/system/ai/schedule/' + scheduleId, method: 'delete' })
}

export function getRecommendations(playerId) {
  return request({ url: '/system/ai/recommend/' + playerId, method: 'get' })
}

export function generateRecommendations(playerId) {
  return request({ url: '/system/ai/recommend/generate', method: 'post', data: { playerId } })
}

export function chat(data) {
  return request({ url: '/system/ai/chat', method: 'post', data: data })
}

export function getChatHistory(playerId) {
  return request({ url: '/system/ai/chat/' + playerId, method: 'get' })
}

export function analyzeData(storeId) {
  return request({ url: '/system/ai/analyze/' + storeId, method: 'get' })
}
