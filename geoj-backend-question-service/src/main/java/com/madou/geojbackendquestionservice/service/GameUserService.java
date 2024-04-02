package com.madou.geojbackendquestionservice.service;

import com.madou.geojmodel.entity.GameUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author MA_dou
* @description 针对表【game_user(竞赛用户关系表)】的数据库操作Service
* @createDate 2024-03-29 17:11:01
*/
public interface GameUserService extends IService<GameUser> {

    /**
     * 加入竞赛
     * @param gameId
     * @param userId
     * @return
     */
    Boolean joinGame(Long gameId, Long userId);
}
