package com.madou.geojmodel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 竞赛题目关系表
 * @TableName game_question
 */
@TableName(value ="game_question")
@Data
public class GameQuestion implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 竞赛 id
     */
    private Long gameId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目积分
     */
    private Integer questionScore;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
