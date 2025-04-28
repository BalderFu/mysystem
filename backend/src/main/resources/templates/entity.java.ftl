package ${package.Entity};

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
*   ${table.comment!""}
* </p>
*/
@Data
@TableName("`${table.name}`")
@ApiModel(value = "${table.entityName}对象", description = "${table.comment!''}")
public class ${table.entityName} implements Serializable {

private static final long serialVersionUID = 1L;

<#list table.fields as field>
    <#if field.keyFlag>
        @TableId(type = IdType.AUTO)
        private ${field.propertyType} ${field.propertyName};

    <#else>
        <#if field.comment??>
            @ApiModelProperty("${field.comment}")
        </#if>

        <#if field.propertyType == "LocalDateTime">
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        </#if>
        <#if field.propertyType == "Date">
            @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
        </#if>

        @TableField("`${field.name}`")
        private ${field.propertyType} ${field.propertyName};

    </#if>
</#list>
}