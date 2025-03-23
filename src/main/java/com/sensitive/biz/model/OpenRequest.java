package com.sensitive.biz.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class OpenRequest {
    @NotBlank(message = "校验类型[text,file]不能为空")
    private String type; //text,file
    private String target; //

}
