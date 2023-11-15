package com.madou.geojbackendquestionservice.controller;

import com.madou.geojai.model.AnswerAi;
import com.madou.geojbackendquestionservice.service.QuestionAiService;
import com.madou.geojbackendserviceclient.service.UserFeignClient;
import com.madou.geojcommon.common.BaseResponse;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.common.ResultUtils;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目Ai接口
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class QuestionAiController {


    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionAiService questionAiService;


    /**
     * 获取提交代码的智能分析结果
     * @param id
     * @param request
     * @return AnswerAi
     */
    @PostMapping("/get/submit/ai")
    public BaseResponse<AnswerAi> getAnswerAi(Long id, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        AnswerAi answerAi = questionAiService.getAnswerAi(id);
        return ResultUtils.success(answerAi);
    }


}
