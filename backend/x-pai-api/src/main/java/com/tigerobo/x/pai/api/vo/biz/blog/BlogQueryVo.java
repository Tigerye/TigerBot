package com.tigerobo.x.pai.api.vo.biz.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BlogQueryVo extends PageReqVo {

    String keyword;

    String tabType;//hot,new,choose
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date startPublishDate;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date endPublishDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date startCreateDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date endCreateDate;

    Integer userId;

    Integer id;

    List<Integer> siteIdList;

    List<Integer> bigShotIdList;

    List<Integer> sourceFromList;

    Integer onlineStatus;

    String categoryName;

    List<Integer> followSiteIds;
    List<Integer> followUserIds;
    List<Integer> followBigShotIds;

    boolean hasFollow;
    boolean viewTab;

    Integer categoryId;

    List<Integer> vipList;

    Integer startId;
    Integer endId;
}
