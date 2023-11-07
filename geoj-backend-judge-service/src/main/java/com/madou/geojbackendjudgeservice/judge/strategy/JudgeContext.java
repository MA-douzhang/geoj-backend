package com.madou.geojbackendjudgeservice.judge.strategy;

import com.madou.geojmodel.codesandbox.ExecuteResult;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.dto.question.JudgeCase;
import com.madou.geojmodel.dto.question.JudgeConfig;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    //实际输入
    private List<String> inputList;
    //期望输出
    private List<String> expectedOutput;
    //判题配置
    private JudgeConfig judgeConfig;
    //提交信息
    private QuestionSubmit questionSubmit;
    //测试用例详细信息
    private List<ExecuteResult> results;
    //判题信息
    private JudgeInfo judgeInfo;

}
