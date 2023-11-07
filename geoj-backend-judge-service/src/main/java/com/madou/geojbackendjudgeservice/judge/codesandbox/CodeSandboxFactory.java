package com.madou.geojbackendjudgeservice.judge.codesandbox;

import com.madou.geojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.madou.geojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.madou.geojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
@Component
public class CodeSandboxFactory {

    @Resource
    private RemoteCodeSandbox remoteCodeSandbox;
    @Resource
    private ThirdPartyCodeSandbox thirdPartyCodeSandbox;
    @Resource
    private ExampleCodeSandbox exampleCodeSandbox;
    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return
     */
    public CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return exampleCodeSandbox;
            case "remote":
                return remoteCodeSandbox;
            case "thirdParty":
                return thirdPartyCodeSandbox;
            default:
                return exampleCodeSandbox;
        }
    }
}
