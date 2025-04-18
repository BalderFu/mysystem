import create from "../utils/request"

export function mingancis(params) {
    return create({
        url: "minganci/pager",
        method: "get",
        params: params
    })
}

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

export function addMinganci(data) {
    return create({
        url: "minganci/add",
        method: "post",
        data: data
    })
}

export function updateMinganci(data) {
    return create({
        url: "minganci/update",
        method: "put",
        data: data
    })
}


export function daleteMinganci(params) {
    return create({
        url: "minganci/delete",
        method: "delete",
        params: params
    })
}

export function checkMinganci(data) {
    return create({
        url: "minganci/check",
        method: "post",
        data: data
    })
}

export function lishipager(params) {
    return create({
        url: "lishi/pagger",
        method: "get",
        params: params
    })
}

export function preview() {
    return create({
        url: "preview",
        method: "post",
        data: {}
    })
}

export function uploadPic(data) {
    return create({
        url: "/user/upload",
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

export function getChatSessions(params) {
    return create({
        url: "/chat/sessionPage",
        method: "get",
        params: params
    })
}

export function chatPage(params) {
    return create({
        url: "/chat/chatPage",
        method: "get",
        params: params
    })
}