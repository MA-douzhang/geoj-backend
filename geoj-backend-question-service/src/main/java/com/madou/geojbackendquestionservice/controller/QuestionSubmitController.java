package com.madou.geojbackendquestionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.madou.geojai.AiManager;
import com.madou.geojai.model.AnswerAi;
import com.madou.geojbackendquestionservice.service.QuestionService;
import com.madou.geojbackendquestionservice.service.QuestionSubmitService;
import com.madou.geojbackendserviceclient.service.JudgeFeignClient;
import com.madou.geojbackendserviceclient.service.UserFeignClient;
import com.madou.geojcommon.common.BaseResponse;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.common.ResultUtils;
import com.madou.geojcommon.constant.AiConstant;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.dto.questionRun.QuestionRunRequest;
import com.madou.geojmodel.dto.questionRun.QuestionRunResult;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitAddRequest;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.madou.geojmodel.entity.QuestionSubmit;
import com.madou.geojmodel.entity.User;
import com.madou.geojmodel.vo.QuestionSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目接口
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

    @Resource
    private QuestionService questionService;
    @Resource
    private AiManager aiManager;

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

    /**
     * 获取某次历史提交的详细信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get/submit")
    public BaseResponse<QuestionSubmitVO> getProblemSubmitVoById(Long id, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
        // 返回脱敏信息
        return ResultUtils.success(QuestionSubmitVO.objToVo(questionSubmit));
    }

    @PostMapping("/get/ai")
    public BaseResponse<AnswerAi> getAnswerAi(Long id, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
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
        return ResultUtils.success(strToAnswerAi(response));
    }

    /**
     * 查询用户通过对应难度数量
     *
     * @return
     */
    @GetMapping("/get/difficulty")
    public BaseResponse<Integer> getQuestionDifficultyByUserId(Integer difficulty, HttpServletRequest request) {
        if (difficulty == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        Integer questionCount = questionSubmitService.getQuestionDifficultyCountByUserId(difficulty, loginUser.getId());
        if (questionCount == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionCount);
    }

    public AnswerAi strToAnswerAi(String result) {
        String[] splits = result.split("【【【【【【");

        if (splits.length < 4) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        //todo 可以使用正则表达式保证数据准确性，防止中文出现
        String solutionIdea = splits[1].trim();
        String reason = splits[2].trim();
        String codeAi = splits[3].trim();

        AnswerAi answerAi = new AnswerAi();
        answerAi.setSolutionIdea(solutionIdea);
        answerAi.setReason(reason);
        answerAi.setCodeAi(codeAi);

        return answerAi;

    }
}
