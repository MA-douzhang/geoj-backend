package com.madou.geojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.madou.geojmodel.codesandbox.ExecuteResult;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.dto.question.JudgeCase;
import com.madou.geojmodel.dto.question.JudgeConfig;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.enums.JudgeInfoMessageEnum;
import com.madou.geojmodel.enums.QuestionSubmitStatusEnum;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Java 程序的判题策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeConfig judgeConfig = judgeContext.getJudgeConfig();
        List<String> inputList = judgeContext.getInputList();
        List<String> expectedOutput = judgeContext.getExpectedOutput();
        List<ExecuteResult> results = judgeContext.getResults();
        List<String> outputList = results.stream().map(ExecuteResult::getOutput).collect(Collectors.toList());
        JudgeInfo judgeInfoResponse = judgeContext.getJudgeInfo();
        int total = inputList.size();
        //设置通过的测试用例
        int pass = 0;
        //设置最大实行时间
        long maxTime = Long.MIN_VALUE;
        for (int i = 0; i < total; i++) {
            //判断执行时间
            Long time = results.get(i).getTime();
            if (time > maxTime) {
                maxTime = time;
            }
            if (expectedOutput.get(i).equals(outputList.get(i))) {
                //超时
                if (maxTime > judgeConfig.getTimeLimit()) {
                    judgeInfoResponse.setTime(maxTime);
                    judgeInfoResponse.setPass(pass);
                    judgeInfoResponse.setStatus(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
                    judgeInfoResponse.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText());
                    break;
                } else {
                    pass++;
                }
            } else {
                //遇到了一个没通过的
                judgeInfoResponse.setPass(pass);
                judgeInfoResponse.setTime(maxTime);
                judgeInfoResponse.setStatus(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
                //设置输出和预期输出信息
                judgeInfoResponse.setInput(inputList.get(i));
                judgeInfoResponse.setOutput(outputList.get(i));
                judgeInfoResponse.setExpectedOutput(expectedOutput.get(i));
                break;
            }
        }
        if (pass == total) {
            judgeInfoResponse.setPass(total);
            judgeInfoResponse.setTime(maxTime);
            judgeInfoResponse.setStatus(JudgeInfoMessageEnum.ACCEPTED.getValue());
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        }
        return judgeInfoResponse;
    }


}
