package com.madou.geojbackendjudgeservice.controller.inner;

import com.madou.geojbackendjudgeservice.judge.JudgeService;
import com.madou.geojbackendserviceclient.service.JudgeFeignClient;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.dto.questionRun.QuestionRunRequest;
import com.madou.geojmodel.dto.questionRun.QuestionRunResult;
import com.madou.geojmodel.entity.QuestionSubmit;
import com.madou.geojmodel.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }

    @Override
    public QuestionRunResult doQuestionRun(QuestionRunRequest questionRunRequest) {
        if (questionRunRequest == null || questionRunRequest.getInput().length() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
       return judgeService.doProblemRun(questionRunRequest);
    }
}
