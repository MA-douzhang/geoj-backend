package com.madou.geojbackendjudgeservice.judge.strategy;

import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.dto.question.JudgeCase;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
