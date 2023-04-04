package com.tigerobo.x.pai.api.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArtModifierDto {
    String name;
    String text;
    String classType;
    String imgUrl;
}
