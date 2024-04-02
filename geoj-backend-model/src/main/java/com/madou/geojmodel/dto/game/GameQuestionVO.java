package com.madou.geojmodel.dto.game;

import cn.hutool.json.JSONUtil;
import com.madou.geojmodel.dto.question.JudgeConfig;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.vo.QuestionVO;
import com.madou.geojmodel.vo.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 * @TableName question
 */
@Data
public class GameQuestionVO implements Serializable {
    /**
     * 题目积分
     */
    private Integer fullScore;

    /**
     * 题目信息
     */
    private QuestionVO questionVO;

    private static final long serialVersionUID = 1L;
}
