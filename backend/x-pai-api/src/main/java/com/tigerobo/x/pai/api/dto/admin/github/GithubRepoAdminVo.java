package com.tigerobo.x.pai.api.dto.admin.github;

import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import lombok.Data;

import java.util.List;

@Data
public class GithubRepoAdminVo extends GithubRepoDto {
    GithubUserDto githubUser;

    List<ModelCategoryDto> aiTagList;
}
