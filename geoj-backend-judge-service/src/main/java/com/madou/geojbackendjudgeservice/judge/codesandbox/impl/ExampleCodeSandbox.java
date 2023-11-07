package com.madou.geojbackendjudgeservice.judge.codesandbox.impl;

import com.madou.geojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.madou.geojmodel.codesandbox.ExecuteCodeRequest;
import com.madou.geojmodel.codesandbox.ExecuteCodeResponse;
import com.madou.geojmodel.codesandbox.ExecuteResult;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.enums.JudgeInfoMessageEnum;
import com.madou.geojmodel.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 */
@Slf4j
@Component
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        List<ExecuteResult> executeResult = new ArrayList<>();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        executeCodeResponse.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        ExecuteResult executeResult1 = new ExecuteResult();
        executeResult1.setTime(100L);
        executeResult1.setMemory(100L);
        executeResult.add(executeResult1);
        executeCodeResponse.setResults(executeResult);
        return executeCodeResponse;
    }
}
