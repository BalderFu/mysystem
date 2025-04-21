package com.mysystem.ai.model.base;

import lombok.Data;

@Data
public class TotalCountResp {
    private Integer algorithmCount;
    private Integer crackTaskCount;
    private Integer userCount;
    private Integer todayVisits;

}
