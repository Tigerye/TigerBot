package com.tigerobo.x.pai.api.aml.service;

import com.tigerobo.x.pai.api.aml.dto.AmlInfoDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlCreateDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlStartTrainDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlUploadDataDto;

public interface AmlInfoService {
    void deleteAml(Integer id, Integer userId);

    AmlInfoDto getAmlInfo(Integer id, Integer userId);

    AmlInfoDto addAml(AmlCreateDto createDto, Integer userId);

    void importDataset(AmlUploadDataDto req, Integer userId);

    void startTrain(AmlStartTrainDto req, Integer userId);
}
