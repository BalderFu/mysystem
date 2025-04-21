import create from "./request"

export function getUserList() {
    return create({
        url: "/user/list",
        method: "get"
    })
} 