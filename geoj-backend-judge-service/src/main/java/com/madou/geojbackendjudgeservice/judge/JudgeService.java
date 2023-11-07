package com.madou.geojbackendjudgeservice.judge;

import com.madou.geojmodel.dto.questionRun.QuestionRunRequest;
import com.madou.geojmodel.dto.questionRun.QuestionRunResult;
import com.madou.geojmodel.entity.QuestionSubmit;
import com.madou.geojmodel.entity.User;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

    /**
     * 案例测试
     * @param questionRunRequest
     * @return
     */
    QuestionRunResult doProblemRun(QuestionRunRequest questionRunRequest);
}
