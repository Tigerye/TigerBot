package com.tigerobo.x.pai.api.vo.notify;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NotifyCountVo {

    Integer notifyType;
    String notifyTypeName;
    int count;
}
