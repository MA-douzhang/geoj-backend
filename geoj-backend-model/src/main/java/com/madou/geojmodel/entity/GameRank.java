package com.madou.geojmodel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 竞赛排名关系表
 * @TableName game_rank
 */
@TableName(value ="game_rank")
@Data
public class GameRank implements Serializable {
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
     * 用户 id
     */
    private Long userId;

    /**
     * 总空间
     */
    private Long totalMemory;

    /**
     * 总空间
     */
    private Long totalTime;

    /**
     * 总空间
     */
    private Integer totalScore;

    /**
     * 竞赛详情
     */
    private String gameDetail;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
