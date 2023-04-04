package com.tigerobo.x.pai.biz.biz.service;

import com.tigerobo.x.pai.dal.biz.dao.ContractInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebContractService {

    @Autowired
    private ContractInfoDao contractInfoDao;



}
