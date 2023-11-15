package com.madou.geojai.model;

import lombok.Data;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description ai回复返回类
 * @date 2023/11/15 22:24:11
 */
@Data
public class AnswerAi {
    /**
     * 解题思路
     */
    private String solutionIdea;

    /**
     * 原因
     */
    private String reason;

    /**
     * ai返回的代码
     */
    private String codeAi;
}
