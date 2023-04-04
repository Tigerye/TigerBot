package com.tigerobo.x.pai.api.dto.ai;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ArtModifierModel {

    String classType;
    String classTypeName;
    List<ArtModifierDto> modifierDtos;
}
