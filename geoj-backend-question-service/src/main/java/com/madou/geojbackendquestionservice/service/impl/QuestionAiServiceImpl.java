package com.madou.geojbackendquestionservice.service.impl;

import com.madou.geojai.AiManager;
import com.madou.geojai.model.AnswerAi;
import com.madou.geojbackendquestionservice.service.QuestionAiService;
import com.madou.geojbackendquestionservice.service.QuestionService;
import com.madou.geojbackendquestionservice.service.QuestionSubmitService;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.common.ResultUtils;
import com.madou.geojcommon.constant.AiConstant;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojcommon.utils.AiUtils;
import com.madou.geojmodel.entity.QuestionSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description
 * @date 2023/11/15 23:00:55
 */
@Service
@Slf4j
public class QuestionAiServiceImpl implements QuestionAiService {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Override
    public AnswerAi getAnswerAi(Long submitId) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(submitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交信息为空");
        }
        String questionContent = questionService.getById(questionSubmit.getQuestionId()).getContent();

        //构造用户输入
        StringBuilder requestAi = new StringBuilder();
        requestAi.append("分析算法题目内容：").append("\n");
        // 拼接分析目标
        requestAi.append(questionContent).append("\n");
        requestAi.append("我的解题代码：").append("\n");
        requestAi.append(questionSubmit.getCode()).append("\n");
        String response = aiManager.doChat(requestAi.toString(), AiConstant.MODEL_ID);
        return strToAnswerAi(response);
    }
    public AnswerAi strToAnswerAi(String result) {
        log.info("ai生成"+result);
        String[] splits = AiUtils.strToAnswerAi(result);
        String solutionIdea= splits[0].trim();
        String reason = splits[1].trim();
        String codeAi = splits[2].trim();
        //封装返回现象
        AnswerAi answerAi = new AnswerAi();
        answerAi.setSolutionIdea(solutionIdea);
        answerAi.setReason(reason);
        answerAi.setCodeAi(codeAi);
        return answerAi;

    }
}
