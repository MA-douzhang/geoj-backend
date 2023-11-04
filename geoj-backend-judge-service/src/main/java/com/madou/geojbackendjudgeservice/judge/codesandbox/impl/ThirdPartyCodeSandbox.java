package com.madou.geojbackendjudgeservice.judge.codesandbox.impl;

import com.madou.geojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.madou.geojmodel.codesandbox.ExecuteCodeRequest;
import com.madou.geojmodel.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
