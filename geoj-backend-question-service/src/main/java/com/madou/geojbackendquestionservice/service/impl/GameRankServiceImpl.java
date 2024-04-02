package com.madou.geojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.madou.geojmodel.entity.GameRank;
import com.madou.geojbackendquestionservice.service.GameRankService;
import com.madou.geojbackendquestionservice.mapper.GameRankMapper;
import org.springframework.stereotype.Service;

/**
* @author MA_dou
* @description 针对表【game_rank(竞赛排名关系表)】的数据库操作Service实现
* @createDate 2024-03-29 17:10:57
*/
@Service
public class GameRankServiceImpl extends ServiceImpl<GameRankMapper, GameRank>
    implements GameRankService{

}




