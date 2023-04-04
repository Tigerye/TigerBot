package com.tigerobo.x.pai.api.vo;

//import com.tigerobo.x.pai.api.auth.entity.Authorization;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

//import javax.validation.constraints.NotNull;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 文件请求类
 * @modified By:
 * @version: $
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "文件请求类")
public class FileReqVo extends RequestVo{
//    @NotNull
    private String fileName;
    private String postfix;
    private String contentType;
//    Authorization authorization;
}
