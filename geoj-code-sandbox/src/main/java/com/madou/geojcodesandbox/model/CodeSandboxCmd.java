package com.madou.geojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 沙盒cmd类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeSandboxCmd {
    /**
     * 编译的cmd
     */
    private String compileCmd;
    /**
     * 运行的cmd
     */
    private String runCmd;
}
