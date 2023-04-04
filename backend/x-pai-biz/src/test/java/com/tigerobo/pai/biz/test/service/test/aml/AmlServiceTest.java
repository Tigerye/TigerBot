package com.tigerobo.pai.biz.test.service.test.aml;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.aml.dto.AmlInfoDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlCreateDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlUploadDataDto;
import com.tigerobo.x.pai.api.aml.service.AmlInfoService;
import com.tigerobo.x.pai.api.aml.vo.MyAmlQueryVo;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.aml.service.AmlLakeService;
import com.tigerobo.x.pai.biz.aml.service.AmlViewServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class AmlServiceTest extends BaseTest {

    @Autowired
    private AmlInfoService amlInfoService;

    @Autowired
    private AmlViewServiceImpl amlViewService;
    @Autowired
    private UserService userService;

    @Autowired
    private AmlLakeService amlLakeService;

    @Test
    public void incPathTest(){
        Integer modelId = 610875;
        String modelArea = "hupu";
        final String modelPath = amlLakeService.getModelPath(modelId, modelArea);
        System.out.println(modelPath);
    }

    @Test
    public void getMyModelTest(){

        int id = 81036;
        String uuid = "06c1f0693ce6173cbba8384012c43022";
        Integer userId = userService.getIdByUuId(uuid);
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "没有登录", null);
        }
        amlViewService.userGetModelById(id,userId);
    }

    @Test
    public void getMyAmlListTest(){

        MyAmlQueryVo queryVo = new MyAmlQueryVo();
        PageInfo<AmlInfoDto> myAmlList = amlViewService.getMyAmlList(queryVo, "18");
        System.out.println(JSON.toJSONString(myAmlList));
    }

    @Test
    public void amlInfoTest(){

        AmlInfoDto amlInfo = amlViewService.getAmlInfo(430806, 3);
        System.out.println(JSON.toJSONString(amlInfo));
    }

    @Test
    public void delTest(){

        amlInfoService.deleteAml(20073,73);
    }

    @Test
    public void addAmlTest(){


        AmlCreateDto createDto = new AmlCreateDto();

        createDto.setBaseModelId(1);
        createDto.setParentModelId(610875);
        createDto.setName("自主训练");
        Object amlId = amlInfoService.addAml(createDto, 11);
        System.out.println("amlId"+amlId);
    }

    @Test
    public void uploadDataTest(){
        AmlUploadDataDto uploadDataDto = new AmlUploadDataDto();
        uploadDataDto.setAmlId(20005);
        FileData fileData = new FileData();
        fileData.setFilePath("http://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/risk-1630314544519.csv");
        fileData.setName("risk-1630314544512");
        uploadDataDto.setDatasetList(Arrays.asList(fileData));
        uploadDataDto.setFileType("CSV");


        amlInfoService.importDataset(uploadDataDto, 3);

    }
}
