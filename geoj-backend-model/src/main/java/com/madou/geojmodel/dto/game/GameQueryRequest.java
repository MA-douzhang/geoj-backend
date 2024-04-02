package com.madou.geojmodel.dto.game;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.madou.geojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GameQueryRequest extends PageRequest implements Serializable {

    /**
     * 竞赛标题
     */
    private String gameTitle;


    /**
     * 竞赛类型
     */
    private String gameType;

    /**
     * 竞赛状态 0为正在进行，1为已结束，2为未开始
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}
