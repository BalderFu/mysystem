import create from "../utils/request"

export function login(data) {
    return create({
        url: "user/login",
        method: "post",
        data: data
    })
}

export function registry(data) {
    return create({
        url: "user/registry",
        method: "post",
        data: data
    })
}

export function loginout() {
    return create({
        url: "user/loginout",
        method: "post",
        // data: data
    })
}

export function userInfo() {
    return create({
        url: "user/userInfo",
        method: "get",
    })
}

export function uploadPic(data) {
    return create({
        url: "/fileUpload",
        method: "post",
        data: data,
        headers: {
            "Content-Type": "multipart/form-data"
        }
    })
}

export function updateUserInfo(data) {
    return create({
        url: "/user/update",
        method: "put",
        data: data
    })
}

export function resetPassword(data) {
    return create({
        url: "/user/resetPws",
        method: "put",
        data: data
    })
}

export function sendValidateCode(params) {
    return create({
        url: "/user/sendValidateCode",
        method: "get",
        params: params
    })
}

export function loginWithPhone(data) {
    return create({
        url: "/user/loginWithPhone",
        method: "post",
        data: data
    })
}
