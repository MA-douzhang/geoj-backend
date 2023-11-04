package com.madou.geojcodesandbox.template.java;

import com.madou.geojcodesandbox.model.ExecuteCodeRequest;
import com.madou.geojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-code-sandbox
 * @description 原生代码沙箱实现
 * @date 2023/10/23 22:01:00
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate{
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
