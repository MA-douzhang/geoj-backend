package com.madou.geojmodel.dto.game;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 竞赛表
 * @TableName game
 */
@TableName(value ="game")
@Data
public class GameVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 创建用户 id
     */
    private String createUserName;

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


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
