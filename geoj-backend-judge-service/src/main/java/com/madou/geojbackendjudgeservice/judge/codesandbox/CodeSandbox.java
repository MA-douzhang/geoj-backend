package com.madou.geojbackendjudgeservice.judge.codesandbox;

import com.madou.geojmodel.codesandbox.ExecuteCodeRequest;
import com.madou.geojmodel.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
