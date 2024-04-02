package com.madou.geojmodel.dto.game;

import com.madou.geojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GameQuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long gameId;



    private static final long serialVersionUID = 1L;
}
