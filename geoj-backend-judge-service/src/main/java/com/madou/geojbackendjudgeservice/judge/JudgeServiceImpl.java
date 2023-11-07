package com.madou.geojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.madou.geojbackendjudgeservice.judge.JudgeManager;
import com.madou.geojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.madou.geojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.madou.geojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.madou.geojbackendjudgeservice.judge.JudgeService;
import com.madou.geojbackendjudgeservice.judge.strategy.JudgeContext;
import com.madou.geojbackendserviceclient.service.QuestionFeignClient;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.codesandbox.ExecuteCodeRequest;
import com.madou.geojmodel.codesandbox.ExecuteCodeResponse;
import com.madou.geojmodel.codesandbox.ExecuteResult;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.dto.question.JudgeCase;
import com.madou.geojmodel.dto.question.JudgeConfig;
import com.madou.geojmodel.dto.questionRun.QuestionRunRequest;
import com.madou.geojmodel.dto.questionRun.QuestionRunResult;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.entity.QuestionSubmit;
import com.madou.geojmodel.enums.JudgeInfoMessageEnum;
import com.madou.geojmodel.enums.QuestionSubmitStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private CodeSandboxFactory codeSandboxFactory;
    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }

        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeInfo judgeInfo = new JudgeInfo();
        int total = judgeCaseList.size();
        judgeInfo.setTotal(total);
        // 代码运行状态 2成功 3失败
        Integer codeResponseStatus = executeCodeResponse.getStatus();
        if (codeResponseStatus.equals(QuestionSubmitStatusEnum.SUCCEED.getValue())) {
            //期望输出
            List<String> expectedOutput = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
            //测试用例详细信息
            List<ExecuteResult> results = executeCodeResponse.getResults();
            //判题配置
            JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
            //使用上下文传输
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setJudgeConfig(judgeConfig);
            judgeContext.setExpectedOutput(expectedOutput);
            judgeContext.setQuestionSubmit(questionSubmit);
            judgeContext.setInputList(inputList);
            judgeContext.setResults(results);
            judgeContext.setJudgeInfo(judgeInfo);
            judgeInfo = judgeManager.doJudge(judgeContext);
        }else if(executeCodeResponse.getStatus().equals(QuestionSubmitStatusEnum.FAILED.getValue())){
            judgeInfo.setPass(0);
            judgeInfo.setStatus(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
            judgeInfo.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getText() + executeCodeResponse.getMessage());
        } else if(executeCodeResponse.getStatus().equals(QuestionSubmitStatusEnum.COMPILE_FAILED.getValue())){
            judgeInfo.setPass(0);
            judgeInfo.setStatus(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            judgeInfo.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getText() + executeCodeResponse.getMessage());
        }

        // 5、修改数据库中的判题结果
        boolean judgeResult = judgeInfo.getStatus().equals(JudgeInfoMessageEnum.ACCEPTED.getValue());

        questionSubmitUpdate.setStatus(judgeResult ?
                QuestionSubmitStatusEnum.SUCCEED.getValue() :
                QuestionSubmitStatusEnum.FAILED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

//         6、修改题目的通过数
         if(judgeResult){
             //将problem的通过数+1
             questionFeignClient.updateQuestionAcceptedById(questionId);
         }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }

    @Override
    public QuestionRunResult doProblemRun(QuestionRunRequest questionRunRequest) {

        String code = questionRunRequest.getCode();
        String language = questionRunRequest.getLanguage();
        List<String> inputList = Collections.singletonList(questionRunRequest.getInput());
        // 执行判题服务
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance(type);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse response = codeSandbox.executeCode(executeCodeRequest);

        return getProblemRunVo(questionRunRequest.getInput(), response);

    }

    private static QuestionRunResult getProblemRunVo(String input, ExecuteCodeResponse response) {
        QuestionRunResult problemRunResult = new QuestionRunResult();
        problemRunResult.setInput(input);
        //执行成功
        if(response.getStatus().equals(QuestionSubmitStatusEnum.SUCCEED.getValue())){
            problemRunResult.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            problemRunResult.setOutput(response.getResults().get(0).getOutput());
        } else if(response.getStatus().equals(QuestionSubmitStatusEnum.FAILED.getValue())){
            problemRunResult.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            problemRunResult.setOutput(response.getMessage());
        } else if(response.getStatus().equals(QuestionSubmitStatusEnum.COMPILE_FAILED.getValue())){
            problemRunResult.setStatus(QuestionSubmitStatusEnum.COMPILE_FAILED.getValue());
            problemRunResult.setOutput(response.getMessage());
        }
        return problemRunResult;
    }
}
