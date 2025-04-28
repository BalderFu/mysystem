import request from '@/utils/request';

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  });
}

export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  });
}

export function getInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  });
}

export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  });
}

export function uploadPic(data) {
  return request({
    url: '/fileUpload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

export function resetPassword(data) {
  return request({
    url: '/user/resetPws',
    method: 'put',
    data
  });
} 