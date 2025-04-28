package ${package.Entity};

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * ${table.comment!""}
    * </p>
*
* @author ${author}
* @since ${date}
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
    @TableField("`${field.name}`")
    private ${field.propertyType} ${field.propertyName};

    </#if>
</#list>
}
