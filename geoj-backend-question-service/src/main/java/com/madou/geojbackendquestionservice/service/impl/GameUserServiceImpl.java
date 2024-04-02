package com.madou.geojbackendquestionservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.madou.geojbackendquestionservice.mapper.GameMapper;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.entity.Game;
import com.madou.geojmodel.entity.GameUser;
import com.madou.geojbackendquestionservice.service.GameUserService;
import com.madou.geojbackendquestionservice.mapper.GameUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author MA_dou
* @description 针对表【game_user(竞赛用户关系表)】的数据库操作Service实现
* @createDate 2024-03-29 17:11:01
*/
@Service
public class GameUserServiceImpl extends ServiceImpl<GameUserMapper, GameUser>
    implements GameUserService{

    @Resource
    private GameMapper gameMapper;
    @Override
    public Boolean joinGame(Long gameId, Long userId) {
        if (BeanUtil.isEmpty(gameId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "竞赛Id不能为空");
        }
        Game game = gameMapper.selectById(gameId);
        if (BeanUtil.isEmpty(game)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "竞赛不存在");
        }
        Integer gameTotalNum = game.getGameTotalNum();
        QueryWrapper<GameUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("gameId",gameId);
        int size = list(queryWrapper).size();
        if (gameTotalNum<=size){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "人数已经到达上限");
        }
        //查询是已经加入竞赛
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.eq("gameId",gameId);
        GameUser gameUser = getOne(queryWrapper);
        if (BeanUtil.isNotEmpty(gameUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入");
        }
        gameUser = new GameUser();
        gameUser.setGameId(gameId);
        gameUser.setUserId(userId);
        return save(gameUser);
    }
}




