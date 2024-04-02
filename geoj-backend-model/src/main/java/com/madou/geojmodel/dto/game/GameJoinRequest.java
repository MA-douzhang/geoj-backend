package com.madou.geojmodel.dto.game;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 竞赛-用户加入请求
 *

 */
@Data
public class GameJoinRequest implements Serializable {

    private Long gameId;

    private static final long serialVersionUID = 1L;
}
