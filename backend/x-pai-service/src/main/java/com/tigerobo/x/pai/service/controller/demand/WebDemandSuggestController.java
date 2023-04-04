package com.tigerobo.x.pai.service.controller.demand;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.dto.DemandSuggest;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.biz.DemandSuggestServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/web/demand/suggest/")
@Api(value = "业务模块-前端服务-需求/建议", position = 2911, tags = "业务模块-前端服务-需求/建议")
public class WebDemandSuggestController {

    @Autowired
    private DemandSuggestServiceImpl demandSuggestService;

    @Autowired
    private UserDao userDao;
    @ApiOperation(value = "需求-添加建议", position = 2920)
    @PostMapping(path = {"add_suggest"}, consumes = "application/json", produces = "application/json")
    public ResultVO demandCreate(@RequestBody DemandSuggest demandSuggest) {
//        Authorization authorization = demandSuggest.getAuthorization();
//        userService.authorize(authorization);
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        UserDo userDo = userDao.getById(userId);

        demandSuggestService.addSuggest(demandSuggest,userDo);
        return ResultVO.success();
    }


    @ApiOperation(value = "需求-建议列表", position = 2921)
    @PostMapping(path = {"get_suggest_list"}, consumes = "application/json", produces = "application/json")
    public List<DemandSuggest> demandCreate(@Valid @RequestBody SuggestReq req) {
        Preconditions.checkArgument(req != null,"参数为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(req.getDemandUuid()),"id为空");
        return demandSuggestService.getDemandSuggestList(req.getDemandUuid());
    }


    @Data
    private static class SuggestReq{
        String demandUuid;
    }
}
