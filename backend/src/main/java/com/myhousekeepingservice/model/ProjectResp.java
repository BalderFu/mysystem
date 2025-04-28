package com.myhousekeepingservice.model;

import com.myhousekeepingservice.entity.Project;
import lombok.Data;

@Data
public class ProjectResp extends Project {

    private String companyName;
    private String serviceName;
    private Long companyId;
    private Long serviceId;
}
