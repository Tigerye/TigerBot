package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.IndexDocObjectDto;
import com.tigerobo.pai.search.api.dto.IndexDocTaskDto;
import com.tigerobo.pai.search.api.dto.UserIndexDto;
import com.tigerobo.pai.search.api.dto.doc.IndexDocObjectsDto;
import com.tigerobo.pai.search.api.req.UserAddIndexReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReq;
import com.tigerobo.pai.search.api.req.base.UserReqPage;
import com.tigerobo.pai.search.api.req.index.IndexDocFileListReq;
import com.tigerobo.pai.search.api.req.index.IndexDocFileReq;
import com.tigerobo.x.pai.biz.utils.JacksonUtil;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SearchIndexDocService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pai.search.hostUrl:}")
    String searchUrl = "http://pai-test.tigerobo.com/search-service";


    public Map<String,Object> addDocFile(@RequestBody IndexDocFileReq req) {


        String url =searchUrl+ "/indexDoc/addDocTask";
        req.setUser(buildUserBase());

        Integer id = restTemplate.postForObject(url, req, Integer.class);
        Validate.isTrue(id!=null&&id>0,"添加数据失败");

        Map<String,Object> map = new HashMap<>();

        map.put("id",id);
        return map;

    }

    public Map<String,Object> addDocFileList(@RequestBody IndexDocFileListReq req) {


        String url =searchUrl+ "/indexDoc/addDocListTask";
        req.setUser(buildUserBase());

        Integer id = restTemplate.postForObject(url, req, Integer.class);
        Validate.isTrue(id!=null&&id>0,"添加数据失败");

        Map<String,Object> map = new HashMap<>();

        map.put("id",id);
        return map;

    }



    public IndexDocTaskDto getDocTaskDetail(IdUserReq req) {
        String url =searchUrl+ "/indexDoc/getDocTaskDetail";

        req.setUser(buildUserBase());

        return restTemplate.postForObject(url, req, IndexDocTaskDto.class);

    }


    public String addDocObject(IndexDocObjectsDto req,Integer userId) {
        String url =searchUrl+ "/indexDoc/addDocObjects";

        req.setUser(buildUserBase(userId));


        return restTemplate.postForObject(url, req, String.class);

    }


    public String addDocObjects(IndexDocObjectsDto req, Integer userId) {
        String url =searchUrl+ "/indexDoc/addDocObjects";

        req.setUser(buildUserBase(userId));


        return restTemplate.postForObject(url, req, String.class);

    }



    public PageVo getUserDocList(UserReqPage req) {
        String url =searchUrl+ "/indexDoc/getUserDocList";

        req.setUser(buildUserBase());
        PageVo pageVo = restTemplate.postForObject(url, req, PageVo.class);

        return pageVo;

    }

    private UserBase buildUserBase(){
        final Integer userId = ThreadLocalHolder.getUserId();

        return buildUserBase(userId);
    }

    private UserBase buildUserBase(Integer userId) {
        if (userId !=null&& userId >0){
            return new UserBase(userId);
        }
        return null;
    }
}
