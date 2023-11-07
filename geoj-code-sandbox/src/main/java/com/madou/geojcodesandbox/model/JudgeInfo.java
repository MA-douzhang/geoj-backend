package com.madou.geojcodesandbox.model;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    //通过用例数
    private Integer pass;
    //总用例数
    private Integer total;
    /**
     * 消耗内存
     */
    private Long memory;
    /**
     * 消耗时间（KB）
     */
    private Long time;

    //状态
    private String status;

    // 输入
    private String input;
    // 输出
    private String output;
    // 期望输出
    private String expectedOutput;
}
