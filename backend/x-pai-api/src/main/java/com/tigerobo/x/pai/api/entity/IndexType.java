package com.tigerobo.x.pai.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Getter
@AllArgsConstructor
public enum IndexType {
    UNKNOWN(0, "其他"),

    USER_TOTAL(1010, "用户-总数"),
    DEVELOPER_TOTAL(1011, "开发者-总数"),
    GROUP_TOTAL(1020, "用户组-总数"),

    DEMAND_VIEW_TOTAL(2100, "需求-浏览-总体统计"),
    DEMAND_VIEW_BY_MONTH(2101, "需求-浏览-月度统计"),

    DEMAND_TOTAL(2100, "需求-总数"),

    TASK_TOTAL(2200, "任务-总数"),
    TASK_VIEW_TOTAL(2210, "任务-浏览-总体统计"),
    TASK_VIEW_BY_MONTH(2211, "任务-浏览-月度统计"),

    MODEL_TOTAL(2300, "模型-总数"),
    MODEL_VIEW_TOTAL(2310, "模型-浏览-总体统计"),
    MODEL_VIEW_BY_MONTH(2311, "模型-浏览-月度统计"),
    MODEL_DOWNLOAD_TOTAL(2320, "模型-下载-总体统计"),
    MODEL_DOWNLOAD_BY_MONTH(2321, "模型-下载-月度统计"),

    DATASET_TOTAL(2400, "需求-总数"),
    DATASET_VIEW_TOTAL(2410, "数据集-浏览-总体统计"),
    DATASET_VIEW_BY_MONTH(2411, "数据集-浏览-月度统计"),
    DATASET_DOWNLOAD_TOTAL(2420, "数据集-下载-总体统计"),
    DATASET_DOWNLOAD_BY_MONTH(2421, "数据集-下载-月度统计"),

    APPLICATION_TOTAL(2900, "应用-浏览-总体统计"),
    APPLICATION_VIEW_TOTAL(2910, "应用-浏览-总体统计"),
    APPLICATION_VIEW_BY_MONTH(2911, "应用-浏览-月度统计"),
    APPLICATION_DOWNLOAD_TOTAL(2920, "应用-下载-总体统计"),
    APPLICATION_DOWNLOAD_BY_MONTH(2921, "应用-下载-月度统计"),

    API_TOTAL(3500, "API-总数"),
    API_CALL_TOTAL(3510, "API-调用-总体统计"),
    API_CALL_BY_MONTH(3511, "API-调用-月度统计"),

    BLOG_TOTAL(4001,"blog更新总数"),
    BLOG_COUNT(4001,"blog总数")
    ;

    private final int val;
    private final String name;
}
