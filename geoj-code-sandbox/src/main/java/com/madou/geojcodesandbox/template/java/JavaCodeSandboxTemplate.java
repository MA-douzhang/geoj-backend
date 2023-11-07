package com.madou.geojcodesandbox.template.java;

import cn.hutool.core.io.FileUtil;
import com.madou.geojcodesandbox.model.*;
import com.madou.geojcodesandbox.model.enums.JudgeInfoMessageEnum;
import com.madou.geojcodesandbox.model.enums.QuestionSubmitStatusEnum;
import com.madou.geojcodesandbox.template.CodeSandbox;
import com.madou.geojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-code-sandbox
 * @description 代码沙箱模板设计模式
 * @date 2023/10/23 21:53:11
 */
@Slf4j
public class JavaCodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        File userCodeFile;
//        1. 把用户的代码保存为文件
        try {
            userCodeFile  = saveCodeToFile(code);
        } catch (Exception e) {
            return ExecuteCodeResponse.builder()
                    .status(QuestionSubmitStatusEnum.FAILED.getValue())
                    .message(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue())
                    .build();
        }

//        2. 编译代码，得到 class 文件
        try {
            ExecuteResult compileFileExecuteMessage = compileFile(userCodeFile);
            System.out.println(compileFileExecuteMessage);
            //编译已经失败了
            if(compileFileExecuteMessage.getExitValue() != 0){
                return ExecuteCodeResponse.builder()
                        .status(QuestionSubmitStatusEnum.COMPILE_FAILED.getValue())
                        .message(compileFileExecuteMessage.getErrorOutput())
                        .build();
            }
        } catch (Exception e) {
            return ExecuteCodeResponse.builder()
                    .status(QuestionSubmitStatusEnum.COMPILE_FAILED.getValue())
                    .message(e.toString())
                    .build();
        }

        // 3. 执行代码，得到输出结果
        List<ExecuteResult> executeMessageList = null;
        try {
            executeMessageList = runFile(userCodeFile, inputList);
            ExecuteResult executeMessage = executeMessageList.get(executeMessageList.size() - 1);
            //执行报错
            if (StringUtils.isNotBlank(executeMessage.getErrorOutput())){
                return ExecuteCodeResponse.builder()
                        .status(QuestionSubmitStatusEnum.FAILED.getValue())
                        .message(executeMessage.getErrorOutput())
                        .build();
            }
        } catch (Exception e) {
            return ExecuteCodeResponse.builder()
                    .status(QuestionSubmitStatusEnum.FAILED.getValue())
                    .message(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue())
                    .build();
        }

//        4. 收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

//        5. 文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }
        return outputResponse;
    }

    /**
     * 1. 把用户的代码保存为文件
     *
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2、编译代码
     *
     * @param userCodeFile
     * @return
     */
    public ExecuteResult compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteResult executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (Exception e) {
            throw new RuntimeException("编译错误");
        }
    }

    /**
     * 3、执行文件，获得执行结果列表
     *
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteResult> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        List<ExecuteResult> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteResult executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }

    /**
     * 4、获取输出结果
     *
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteResult> executeMessageList) {
        return ExecuteCodeResponse.builder()
                .status(QuestionSubmitStatusEnum.SUCCEED.getValue())
                .message(JudgeInfoMessageEnum.ACCEPTED.getValue())
                .results(executeMessageList).build();
    }

    /**
     * 5、删除文件
     *
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }


}
