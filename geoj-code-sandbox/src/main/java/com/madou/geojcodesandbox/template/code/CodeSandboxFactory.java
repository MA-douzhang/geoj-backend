package com.madou.geojcodesandbox.template.code;


import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CodeSandboxFactory {
    @Resource
    private JavaNativeAcmSandbox javaNativeAcmSandbox;
    @Resource
    private CppNativeCodeSandbox cppNativeCodeSandbox;
    /**
     * 创建沙箱语言工厂
     *
     * @param language 沙箱类型
     * @return
     */
    public CodeSandboxTemplate newInstance(String language) {
        switch (language) {
            case "java":
                return javaNativeAcmSandbox;
            case "cpp":
                return cppNativeCodeSandbox;
            default:
                throw new RuntimeException("暂不支持");
        }
    }
}
