package com.tigerobo.x.pai.api.vo.github;

import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import lombok.Data;

import java.util.List;

@Data
public class GithubRepoVo extends GithubRepoDto {
    GithubUserDto githubUser;
    InteractVo interact;
    List<ModelCategoryDto> aiTagList;
}
