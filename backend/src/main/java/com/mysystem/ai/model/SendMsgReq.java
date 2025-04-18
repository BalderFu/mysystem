package com.mysystem.ai.model;

import lombok.Data;

@Data
public class SendMsgReq {

    private Long sessionId;
    private String content;
}
