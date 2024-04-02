package com.madou.geojmodel.dto.game;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 竞赛-详情返回
 *

 */
@Data
public class GameInfoVO implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 公开类型 all所有人，part部分
     */
    private String publicType;

    /**
     * 题目分数
     */
    private List<Integer> questionFullScoreList;

    /**
     * 题目id列表
     */
    private List<Long> questionIdList;
    /**
     * 开启时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    private static final long serialVersionUID = 1L;
}
