package com.madou.geojmodel.dto.game;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.madou.geojmodel.dto.question.JudgeCase;
import com.madou.geojmodel.dto.question.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 竞赛-创建请求
 *

 */
@Data
public class GameQuestionAddRequest implements Serializable {

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
