package com.tigerobo.x.pai.api.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.tigerobo.x.pai.api.utils.Mapable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

//import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 内容查询基类，KV结构
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "内容查询基类，KV结构")
public class QueryVo extends RequestVo implements Mapable<String, Object> {
//    @NotNull
    @Builder.Default
    @ApiModelProperty(value = "查询参数", position = 11)
    @JSONField(name = "params")
    private Map<String, Object> params = new HashMap<>();

    @Override
    @JSONField(serialize = false, deserialize = false)
    public Map<String, Object> get() {
        if (this.params == null)
            this.params = new HashMap<>();
        return this.getParams();
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public void set(Map<String, Object> map) {
        this.setParams(map);
    }

    @Override
    public String toString() {
        return "RequestVo{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", orderBy=" + orderBy +
                ", params=" + params +
                '}';
    }
}
