package com.tigerobo.x.pai.api.biz.service;

import com.tigerobo.x.pai.api.vo.biz.mine.MineCountVo;
import com.tigerobo.x.pai.api.vo.IndexVo;

public interface SummaryService {
    MineCountVo getMineCount();

    IndexVo homeIndices();
}
