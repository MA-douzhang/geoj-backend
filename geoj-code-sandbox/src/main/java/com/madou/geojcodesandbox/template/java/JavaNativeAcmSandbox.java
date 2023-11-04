package com.madou.geojcodesandbox.template.java;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.madou.geojcodesandbox.model.ExecuteCodeRequest;
import com.madou.geojcodesandbox.model.ExecuteCodeResponse;
import com.madou.geojcodesandbox.model.ExecuteMessage;
import com.madou.geojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-code-sandbox
 * @description 原生代码沙箱实现
 * @date 2023/10/23 22:01:00
 */
@Component
@Slf4j
public class JavaNativeAcmSandbox extends JavaCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;
    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        // 3. 执行代码，得到输出结果
        List<ExecuteMessage> executeResults = new ArrayList<>();
        for (String input : inputList) {
            //Linux下的命令
//            String runCmd = String.format("/software/jdk1.8.0_301/bin/java -Xmx256m -Dfile.encoding=UTF-8 -cp %s:%s -Djava.security.manager=%s Main", dir, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            //Windows下的命令
            // String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main", dir, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentPath);
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

                ExecuteMessage executeResult = null;
                try {
                    executeResult = ProcessUtils.runProcessAcmAndGetMessage(runProcess, input);
                } catch (IOException e) {
                    log.error("执行出错: {}", e.toString());
                }
                stopWatch.stop();
                if (!thread.isAlive()) {
                    executeResult = new ExecuteMessage();
                    executeResult.setTime(stopWatch.getLastTaskTimeMillis());
                    executeResult.setErrorMessage("超出时间限制");
                }
                executeResults.add(executeResult);

                //已经有用例失败了
                if (StrUtil.isNotBlank(executeResult.getErrorMessage())) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return executeResults;
    }
}
