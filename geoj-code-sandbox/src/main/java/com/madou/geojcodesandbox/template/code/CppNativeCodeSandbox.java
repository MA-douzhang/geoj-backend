package com.madou.geojcodesandbox.template.code;

import com.madou.geojcodesandbox.model.CodeSandboxCmd;
import com.madou.geojcodesandbox.model.ExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;


/**
 * cpp本机代码沙箱
 *
 * @author 程崎
 * @since 2023/08/21
 */
@Slf4j
@Component
public class CppNativeCodeSandbox extends CodeSandboxTemplate {
    private static final String PREFIX = File.separator + "cpp";

    private static final String GLOBAL_CODE_DIR_PATH = File.separator + "tempCode";

    private static final String GLOBAL_CPP_NAME = File.separator + "main.cpp";

    public CppNativeCodeSandbox() {
        super.prefix = PREFIX;
        super.globalCodeDirPath = GLOBAL_CODE_DIR_PATH;
        super.globalCodeFileName = GLOBAL_CPP_NAME;
    }

    @Override
    public List<ExecuteResult> runFile(String userCodeFile, List<String> inputList) {
        return super.runFile(userCodeFile, inputList);
    }

    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(String.format("g++ -finput-charset=UTF-8 -fexec-charset=UTF-8 %s -o %s", userCodePath,userCodePath.substring(0,userCodePath.length()-4)))
                .runCmd(userCodeParentPath + File.separator + "main")
                .build();
    }
}
