package com.madou.geojmodel.dto.game;

import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitAddRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *

 */
@Data
public class GameQuestionSubmitRequest implements Serializable {

    /**
     * 编程id
     */
    private Long gameId;

    /**
     * 题目提交请求
     */
    private QuestionSubmitAddRequest questionSubmitAddRequest;

    private static final long serialVersionUID = 1L;
}
