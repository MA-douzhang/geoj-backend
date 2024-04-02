package com.madou.geojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.madou.geojmodel.dto.game.GameQueryRequest;
import com.madou.geojmodel.dto.game.GameQuestionSubmitRequest;
import com.madou.geojmodel.dto.game.GameInfoVO;
import com.madou.geojmodel.dto.game.GameVO;
import com.madou.geojmodel.entity.Game;
import com.baomidou.mybatisplus.extension.service.IService;
import com.madou.geojmodel.entity.User;

/**
* @author MA_dou
* @description 针对表【game(竞赛表)】的数据库操作Service
* @createDate 2024-03-29 17:10:34
*/
public interface GameService extends IService<Game> {
    /**
     * 竞赛提交
     *
     * @param gameQuestionSubmitRequest
     * @param loginUser
     * @return
     */
    Long questionSubmit(GameQuestionSubmitRequest gameQuestionSubmitRequest, User loginUser);

    /**
     * 获取竞赛详情信息
     * @param game
     * @return
     */
    GameInfoVO getGameById(Game game);

    /**
     * 条件查询
     * @param gameQueryRequest
     * @return
     */
    QueryWrapper<Game> getQueryWrapper(GameQueryRequest gameQueryRequest);

    /**
     * 竞赛脱敏分页列表
     * @param gamePage
     * @return
     */
    Page<GameVO> getGameVOPage(Page<Game> gamePage);

    /**
     * 竞赛脱敏
     * @param game
     * @return
     */
    GameVO getGameVO(Game game);
}
