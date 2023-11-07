package com.madou.geojbackendquestionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.madou.geojbackendquestionservice.service.QuestionService;
import com.madou.geojbackendquestionservice.service.QuestionSubmitService;
import com.madou.geojbackendserviceclient.service.JudgeFeignClient;
import com.madou.geojbackendserviceclient.service.UserFeignClient;
import com.madou.geojcommon.annotation.AuthCheck;
import com.madou.geojcommon.common.BaseResponse;
import com.madou.geojcommon.common.DeleteRequest;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.common.ResultUtils;
import com.madou.geojcommon.constant.UserConstant;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojcommon.exception.ThrowUtils;
import com.madou.geojmodel.dto.question.*;
import com.madou.geojmodel.dto.questionRun.QuestionRunRequest;
import com.madou.geojmodel.dto.questionRun.QuestionRunResult;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitAddRequest;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.entity.QuestionSubmit;
import com.madou.geojmodel.entity.User;
import com.madou.geojmodel.vo.QuestionSubmitVO;
import com.madou.geojmodel.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 *

 */
@RestController
@RequestMapping("/submit")
@Slf4j
public class QuestionSubmitController {


    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private JudgeFeignClient judgeFeignClient;
    @Resource
    private QuestionSubmitService questionSubmitService;


    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录的 id
     */
    @PostMapping("/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userFeignClient.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    @PostMapping("/run")
    public BaseResponse<QuestionRunResult> doQuestionRun(@RequestBody QuestionRunRequest questionRunRequest,
                                                         HttpServletRequest request) {
        if (questionRunRequest == null || questionRunRequest.getInput().length() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userFeignClient.getLoginUser(request);
        QuestionRunResult questionRunResult = judgeFeignClient.doQuestionRun(questionRunRequest);
        return ResultUtils.success(questionRunResult);
    }
    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userFeignClient.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }



}
