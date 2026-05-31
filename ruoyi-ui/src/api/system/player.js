import request from '@/utils/request'

// 查询玩家列表
export function listPlayer(query) {
  return request({
    url: '/system/player/list',
    method: 'get',
    params: query
  })
}

// 查询玩家详细
export function getPlayer(playerId) {
  return request({
    url: '/system/player/' + playerId,
    method: 'get'
  })
}

// 新增玩家
export function addPlayer(data) {
  return request({
    url: '/system/player',
    method: 'post',
    data: data
  })
}

// 修改玩家
export function updatePlayer(data) {
  return request({
    url: '/system/player',
    method: 'put',
    data: data
  })
}

// 删除玩家
export function delPlayer(playerIds) {
  return request({
    url: '/system/player/' + playerIds,
    method: 'delete'
  })
}
