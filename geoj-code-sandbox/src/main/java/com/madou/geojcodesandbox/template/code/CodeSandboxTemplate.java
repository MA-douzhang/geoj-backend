package com.madou.geojcodesandbox.template.code;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.madou.geojcodesandbox.model.*;
import com.madou.geojcodesandbox.model.enums.JudgeInfoMessageEnum;
import com.madou.geojcodesandbox.model.enums.QuestionSubmitStatusEnum;
import com.madou.geojcodesandbox.template.CodeSandbox;
import com.madou.geojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-code-sandbox
 * @description 代码沙箱模板设计模式
 * @date 2023/10/23 21:53:11
 */
@Slf4j
public abstract class CodeSandboxTemplate implements CodeSandbox {

    /**
     * 代码语言类java，cpp
     */
    String prefix;

    /**
     * 代码文件文件夹
     */
    String globalCodeDirPath;

    /**
     * 代码文件名
     */
    String globalCodeFileName;


    private static final long TIME_OUT = 5000L;


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        File userCodeFile;
//        1. 把用户的代码保存为文件
        try {
            userCodeFile = saveCodeToFile(code);
        } catch (Exception e) {
            return ExecuteCodeResponse.builder()
                    .status(QuestionSubmitStatusEnum.FAILED.getValue())
                    .message(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue())
                    .build();
        }
        //获取保存后的代码文件
        String userCodePath = userCodeFile.getAbsolutePath();
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        CodeSandboxCmd cmdFromLanguage = getCmd(userCodeParentPath, userCodePath);
        String compileCmd = cmdFromLanguage.getCompileCmd();
        String runCmd = cmdFromLanguage.getRunCmd();
//        2. 编译代码，得到 class 文件
        try {
            ExecuteResult compileFileExecuteMessage = compileFile(compileCmd);
            System.out.println(compileFileExecuteMessage);
            //编译已经失败了
            if (compileFileExecuteMessage.getExitValue() != 0) {
                deleteFile(userCodeParentPath);
                return ExecuteCodeResponse.builder()
                        .status(QuestionSubmitStatusEnum.COMPILE_FAILED.getValue())
                        .message(compileFileExecuteMessage.getErrorOutput())
                        .build();
            }
        } catch (Exception e) {
            deleteFile(userCodeParentPath);
            return ExecuteCodeResponse.builder()
                    .status(QuestionSubmitStatusEnum.COMPILE_FAILED.getValue())
                    .message(e.toString())
                    .build();
        }

        // 3. 执行代码，得到输出结果
        List<ExecuteResult> executeMessageList = null;
        try {
            executeMessageList = runFile(runCmd, inputList);
            ExecuteResult executeMessage = executeMessageList.get(executeMessageList.size() - 1);
            //执行报错
            if (StringUtils.isNotBlank(executeMessage.getErrorOutput())) {
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
        boolean b = deleteFile(userCodeParentPath);
        if (!b) {
            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }
        return outputResponse;
    }


    /**
     * 每个实现类必须实现编译以及运行的cmd
     *
     * @param userCodeParentPath 代码所在的父目录
     * @param userCodePath       代码所在目录
     * @return {@link CodeSandboxCmd}
     */
    public abstract CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath);

    /**
     * 1. 把用户的代码保存为文件
     *
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + globalCodeDirPath;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + prefix + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + globalCodeFileName;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2、编译代码
     *
     * @param compileCmd
     * @return
     */
    public ExecuteResult compileFile(String compileCmd) {
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
     * @param runCmd
     * @param inputList
     * @return
     */
    public List<ExecuteResult> runFile(String runCmd, List<String> inputList) {
        // 3. 执行代码，得到输出结果
        List<ExecuteResult> executeResults = new ArrayList<>();
        for (String input : inputList) {
            //Linux下的命令
//            String runCmd = String.format("/software/jdk1.8.0_301/bin/java -Xmx256m -Dfile.encoding=UTF-8 -cp %s:%s -Djava.security.manager=%s Main", dir, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            //Windows下的命令
            // String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main", dir, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        //超时了
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();

                ExecuteResult executeResult = null;
                try {
                    executeResult = ProcessUtils.runProcessAcmAndGetMessage(runProcess, input);
                } catch (IOException e) {
                    log.error("执行出错: {}", e.toString());
                }
                stopWatch.stop();
                if (!thread.isAlive()) {
                    executeResult = new ExecuteResult();
                    executeResult.setTime(stopWatch.getLastTaskTimeMillis());
                    executeResult.setErrorOutput("超出时间限制");
                }
                executeResults.add(executeResult);

                //已经有用例失败了
                if (StrUtil.isNotBlank(Objects.requireNonNull(executeResult).getErrorOutput())) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return executeResults;
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
     * @param userCodeParentPath
     * @return
     */
    public boolean deleteFile(String userCodeParentPath) {
        if (userCodeParentPath != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }


}
