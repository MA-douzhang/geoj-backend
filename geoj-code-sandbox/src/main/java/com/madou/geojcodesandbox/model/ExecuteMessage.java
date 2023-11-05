package com.madou.geojcodesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage {
    //退出状态码
    private Integer exitValue;
    //返回正常信息
    private String message;
    //返回错误信息
    private String errorMessage;
    //运行时间
    private Long time;
    //运行内存
    private Long memory;
}
