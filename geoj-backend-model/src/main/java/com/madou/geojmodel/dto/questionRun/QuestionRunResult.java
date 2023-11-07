package com.madou.geojmodel.dto.questionRun;

import lombok.Data;

@Data
public class QuestionRunResult {
    /**
     * 执行状态
     */
    private Integer status;
    /**
     * 输入
     */
    private String input;
    /**
     * 执行结果
     */
    private String output;

}
