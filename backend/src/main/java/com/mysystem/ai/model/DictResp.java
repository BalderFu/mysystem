package com.mysystem.ai.model;

import com.mysystem.ai.entity.HashDict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DictResp extends HashDict {
    private String username;
    private String arithmeticName;
}
