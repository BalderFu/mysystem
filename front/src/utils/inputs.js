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

export function arithmeticPage(params) {
    return create({
        url: "/hashArithmetic/page",
        method: "get",
        params: params
    })
}

export function addArithmetic(data) {
    return create({
        url: "/hashArithmetic",
        method: "post",
        data: data
    })
}

export function updateArithmetic(data) {
    return create({
        url: "/hashArithmetic",
        method: "put",
        data: data
    })
}

export function deleteArithmetic(params) {
    return create({
        url: "/hashArithmetic",
        method: "delete",
        params: params
    })
}

export function getArithmeticById(params) {
    return create({
        url: "/hashArithmetic",
        method: "get",
        params: params
    })
}


export function getDictPage(params) {
    return create({
        url: "/dict/page",
        method: "get",
        params: params
    })
}

export function addArithmeticHouse(data) {
    return create({
        url: "/dict",
        method: "post",
        data: data
    })
}

export function updateArithmeticHouse(data) {
    return create({
        url: "/dict",
        method: "put",  
        data: data
    })
}

export function deleteArithmeticHouse(params) {
    return create({
        url: "/dict",
        method: "delete",
        params: params
    })
}

export function getArithmeticHouseById(params) {
    return create({
        url: "/dict",
        method: "get",
        params: params
    })
}

export function crackArithmetic(data) {
    return create({
        url: "/dict/crack",
        method: "post",
        data: data
    })
}

export function hashList(params) {
    return create({
        url: "/hashArithmetic/list",
        method: "get",
        params: params
    })
}