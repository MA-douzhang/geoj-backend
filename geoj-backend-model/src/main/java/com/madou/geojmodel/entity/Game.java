package com.madou.geojmodel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 竞赛表
 * @TableName game
 */
@TableName(value ="game")
@Data
public class Game implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建用户 id
     */
    private Long createUserId;

    /**
     * 竞赛标题
     */
    private String gameTitle;

    /**
     * 赛事介绍
     */
    private String gameProfile;

    /**
     * 竞赛限制人数
     */
    private Integer gameTotalNum;

    /**
     * 竞赛类型
     */
    private String gameType;

    /**
     * 公开类型
     */
    private String publicType;

    /**
     * 开启时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

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
