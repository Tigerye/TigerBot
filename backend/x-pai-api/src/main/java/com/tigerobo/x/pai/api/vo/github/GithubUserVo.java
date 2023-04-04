package com.tigerobo.x.pai.api.vo.github;

import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import lombok.Data;

@Data
public class GithubUserVo extends GithubUserDto {
    boolean follow;
}
