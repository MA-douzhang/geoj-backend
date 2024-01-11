package com.madou.geojmodel.vo;

import cn.hutool.json.JSONUtil;
import com.madou.geojmodel.dto.question.JudgeConfig;
import com.madou.geojmodel.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目难度数量封装类
 * @TableName question
 */
@Data
public class QuestionDifficultyVO implements Serializable {
    /**
     * 简单题目数
     */
    private Long briefnessNum;
    /**
     * 中等题目数
     */
    private Long mediumNum;
    /**
     * 困难题目数
     */
    private Long difficultyNum;


    private static final long serialVersionUID = 1L;
}
