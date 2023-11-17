package com.madou.geojcodesandbox.template.code;

import com.madou.geojcodesandbox.model.CodeSandboxCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-code-sandbox
 * @description 原生代码沙箱实现
 * @date 2023/10/23 22:01:00
 */
@Component
@Slf4j
public class JavaNativeAcmSandbox extends CodeSandboxTemplate {

    private static final String PREFIX = File.separator + "java";

    private static final String GLOBAL_CODE_DIR_PATH = File.separator + "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = File.separator + "Main.java";


    public JavaNativeAcmSandbox() {
        super.prefix = PREFIX;
        super.globalCodeDirPath = GLOBAL_CODE_DIR_PATH;
        super.globalCodeFileName = GLOBAL_JAVA_CLASS_NAME;
    }

    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(String.format("javac -encoding utf-8 %s", userCodePath))
                .runCmd(String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentPath))
                .build();
    }

}
