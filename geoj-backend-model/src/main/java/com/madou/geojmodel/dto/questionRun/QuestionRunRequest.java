package com.madou.geojmodel.dto.questionRun;

import lombok.Data;


@Data
public class QuestionRunRequest {
    //代码
    private String code;
    //输入
    private String input;
    //语言
    private String language;
}
