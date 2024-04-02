package com.madou.geojbackendquestionservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.madou.geojbackendquestionservice.mapper.GameQuestionMapper;
import com.madou.geojbackendquestionservice.mapper.GameRankMapper;
import com.madou.geojbackendquestionservice.mapper.GameUserMapper;
import com.madou.geojbackendquestionservice.service.QuestionService;
import com.madou.geojbackendquestionservice.service.QuestionSubmitService;
import com.madou.geojbackendserviceclient.service.UserFeignClient;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.constant.CommonConstant;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojcommon.utils.SqlUtils;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.dto.game.*;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitAddRequest;
import com.madou.geojmodel.entity.*;
import com.madou.geojbackendquestionservice.service.GameService;
import com.madou.geojbackendquestionservice.mapper.GameMapper;
import com.madou.geojmodel.enums.QuestionSubmitStatusEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author MA_dou
 * @description 针对表【game(竞赛表)】的数据库操作Service实现
 * @createDate 2024-03-29 17:10:34
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
        implements GameService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private QuestionService questionService;

    @Resource
    private GameMapper gameMapper;

    @Resource
    private GameQuestionMapper gameQuestionMapper;

    @Resource
    private GameRankMapper gameRankMapper;

    @Resource
    private GameUserMapper gameUserMapper;

    @Override
    public Long questionSubmit(GameQuestionSubmitRequest gameQuestionSubmitRequest, User loginUser) {
        QuestionSubmitAddRequest questionSubmitAddRequest = gameQuestionSubmitRequest.getQuestionSubmitAddRequest();
        Long gameId = gameQuestionSubmitRequest.getGameId();
        if (questionSubmitAddRequest == null || gameId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "竞赛不存在");
        }
        Date startTime = game.getStartTime();
        Date endTime = game.getEndTime();
        // 判断竞赛是否已经开始
        Date currentDate = new Date();
        if (startTime.after(currentDate)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "竞赛未开始");
        }
        // 判断竞赛是否已经结束
        if (endTime.before(currentDate)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "竞赛已结束");
        }
        //查询是已经加入竞赛
        QueryWrapper<GameUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        queryWrapper.eq("gameId",gameId);
        GameUser gameUser = gameUserMapper.selectOne(queryWrapper);
        if (BeanUtil.isEmpty(gameUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入竞赛");
        }
        long submitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        // 同时异步更新排行榜信息
        CompletableFuture.runAsync(() ->
        {
            // 更新用户提交成绩信息
            QuestionSubmit nowSubmit = questionSubmitService.getById(submitId);
            // 获得该竞赛各题目的满分
            QueryWrapper<GameQuestion> gameQuestionQueryWrapper = new QueryWrapper<>();
            gameQuestionQueryWrapper.eq("gameId", gameId);
            List<GameQuestion> gameQuestionList = gameQuestionMapper.selectList(gameQuestionQueryWrapper);
            Map<Long, Integer> questionIdToFullScore = gameQuestionList.stream().collect(Collectors.toMap(GameQuestion::getQuestionId, GameQuestion::getQuestionScore));
            // 获得当前用户在当前竞赛中的提交信息
            QueryWrapper<GameRank> gameRankQueryWrapper = new QueryWrapper<>();
            gameRankQueryWrapper.eq("userId", loginUser.getId()).eq("gameId", gameId);
            GameRank gameRank = gameRankMapper.selectOne(gameRankQueryWrapper);
            // 为空新建一个gameRank记录并将当前答题情况插入更新（因为第一次统计时，还没新建这个记录）
            if (gameRank == null) {
                gameRank = new GameRank();
                gameRank.setUserId(loginUser.getId());
                gameRank.setGameId(gameId);
                GameDetail gameDetail = new GameDetail();
                gameDetail.setGameId(gameId);
                gameDetail.setUserId(loginUser.getId());
                Map<Long, GameDetailUnit> gameDetailUnitMap = new HashMap<>();
                GameDetailUnit gameDetailUnit = getGameDetailUnit(nowSubmit, questionIdToFullScore);
                gameDetailUnitMap.put(nowSubmit.getQuestionId(), gameDetailUnit);
                gameDetail.setSubmitDetail(gameDetailUnitMap);
                gameRank.setGameDetail(JSONUtil.toJsonStr(gameDetail));
                gameRank.setTotalScore(gameDetailUnit.getScore());
                gameRank.setTotalMemory(gameDetailUnit.getMemoryCost());
                gameRank.setTotalTime(gameDetailUnit.getTimeCost());
                gameRankMapper.insert(gameRank);
            }
            // gameDetail()为空时，则将当前答题情况插入更新
            else if (StringUtils.isBlank(gameRank.getGameDetail())) {
                GameDetail gameDetail = new GameDetail();
                gameDetail.setGameId(gameId);
                gameDetail.setUserId(loginUser.getId());
                Map<Long, GameDetailUnit> gameDetailUnitMap = new HashMap<>();
                GameDetailUnit gameDetailUnit = getGameDetailUnit(nowSubmit, questionIdToFullScore);
                gameDetailUnitMap.put(nowSubmit.getQuestionId(), gameDetailUnit);
                gameDetail.setSubmitDetail(gameDetailUnitMap);
                gameRank.setGameDetail(JSONUtil.toJsonStr(gameDetail));
                gameRank.setTotalScore(gameDetailUnit.getScore());
                gameRank.setTotalMemory(gameDetailUnit.getMemoryCost());
                gameRank.setTotalTime(gameDetailUnit.getTimeCost());
                gameRankMapper.updateById(gameRank);
            } else {
                // 对比两个版本的当前题目提交信息，保留最优的
                GameDetail dbGameDetail = JSONUtil.toBean(gameRank.getGameDetail(), GameDetail.class);
                Map<Long, GameDetailUnit> dbSubmitDetail = dbGameDetail.getSubmitDetail();
                // 获得数据库已有的该题目提交信息
                GameDetailUnit dbGameDetailUnit = dbSubmitDetail.get(nowSubmit.getQuestionId());
                // 组装当前的该题目提交信息
                GameDetailUnit gameDetailUnit = getGameDetailUnit(nowSubmit, questionIdToFullScore);
                if (dbGameDetailUnit == null) {
                    dbSubmitDetail.put(nowSubmit.getQuestionId(), gameDetailUnit);
                    dbGameDetail.setSubmitDetail(dbSubmitDetail);
                    gameRank.setGameDetail(JSONUtil.toJsonStr(dbGameDetail));
                    gameRank.setTotalScore(gameRank.getTotalScore() + gameDetailUnit.getScore());
                    gameRank.setTotalMemory(gameRank.getTotalMemory() + gameDetailUnit.getMemoryCost());
                    gameRank.setTotalTime(gameRank.getTotalTime() + gameDetailUnit.getTimeCost());
                    gameRankMapper.updateById(gameRank);
                } else {
                    // 如果新的优于目前的
                    if (gameDetailUnit.isBetter(dbGameDetailUnit)) {
                        dbSubmitDetail.put(nowSubmit.getQuestionId(), gameDetailUnit);
                        dbGameDetail.setSubmitDetail(dbSubmitDetail);
                        gameRank.setGameDetail(JSONUtil.toJsonStr(dbGameDetail));
                        gameRank.setTotalScore(gameRank.getTotalScore() - dbGameDetailUnit.getScore() + gameDetailUnit.getScore());
                        gameRank.setTotalMemory(gameRank.getTotalMemory() - dbGameDetailUnit.getMemoryCost() + gameDetailUnit.getMemoryCost());
                        gameRank.setTotalTime(gameRank.getTotalTime() - dbGameDetailUnit.getTimeCost() + gameDetailUnit.getTimeCost());
                        gameRankMapper.updateById(gameRank);
                    }
                }
            }
        });
        return submitId;
    }

    @Override
    public GameInfoVO getGameById(Game game) {
        Long id = game.getId();
        //查询关联表信息
        QueryWrapper<GameQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId",id);
        List<GameQuestion> list = gameQuestionMapper.selectList(queryWrapper);
        //获取题目id和分数
        List<Long> gameQuestionIds = list.stream().map(GameQuestion::getQuestionId).collect(Collectors.toList());
        List<Integer> gameQuestionScore = list.stream().map(GameQuestion::getQuestionScore).collect(Collectors.toList());
        GameInfoVO gameInfoVO = new GameInfoVO();
        BeanUtils.copyProperties(game, gameInfoVO);
        gameInfoVO.setQuestionIdList(gameQuestionIds);
        gameInfoVO.setQuestionFullScoreList(gameQuestionScore);
        return gameInfoVO;
    }

    @Override
    public QueryWrapper<Game> getQueryWrapper(GameQueryRequest gameQueryRequest) {
        QueryWrapper<Game> queryWrapper = new QueryWrapper<>();
        if (gameQueryRequest == null) {
            return queryWrapper;
        }
        String gameTitle = gameQueryRequest.getGameTitle();
        Integer status = gameQueryRequest.getStatus();
        String gameType = gameQueryRequest.getGameType();
        String sortField = gameQueryRequest.getSortField();
        String sortOrder = gameQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(gameTitle), "gameTitle", gameTitle);
        queryWrapper.eq(StringUtils.isNotBlank(gameType), "gameType", gameType);
        queryWrapper.eq("isDelete", false);
        //竞赛状态 0为正在进行，1为已结束，2为未开始
        switch (status){
            case 0:
                queryWrapper.ge("startTime", LocalDate.now()).le("endTime",LocalDate.now());
                break;
            case 1:
                queryWrapper.ge("endTime", LocalDate.now());
                break;
            case 2:
                queryWrapper.le("startTime", LocalDate.now());
                break;
        }
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<GameVO> getGameVOPage(Page<Game> gamePage) {
        List<Game> gamePageRecords = gamePage.getRecords();
        Page<GameVO> gameVOPage = new Page<>(gamePage.getCurrent(), gamePage.getSize(), gamePage.getTotal());
        if (CollectionUtils.isEmpty(gamePageRecords)) {
            return gameVOPage;
        }
        List<GameVO> gameVOList = gamePageRecords.stream()
                .map(this::getGameVO)
                .collect(Collectors.toList());
        gameVOPage.setRecords(gameVOList);
        return gameVOPage;
    }


    @Override
    public GameVO getGameVO(Game game) {
        GameVO gameVO = new GameVO();
        BeanUtils.copyProperties(game, gameVO);
        //保存提交用户信息
        User user = userFeignClient.getById(game.getCreateUserId());
        gameVO.setCreateUserName(user.getUserName());
        return gameVO;
    }

    /**
     * 获取竞赛提交详情
     *
     * @param nowSubmit
     * @param questionIdToFullScore
     * @return
     */
    private GameDetailUnit getGameDetailUnit(QuestionSubmit nowSubmit, Map<Long, Integer> questionIdToFullScore) {
        Long questionId = nowSubmit.getQuestionId();
        Integer status = nowSubmit.getStatus();
        JudgeInfo judgeInfo = JSONUtil.toBean(nowSubmit.getJudgeInfo(), JudgeInfo.class);
        Question question = questionService.getById(questionId);
        GameDetailUnit gameDetailUnit = new GameDetailUnit();
        //判断
        if (status.equals(QuestionSubmitStatusEnum.SUCCEED.getValue())) {
            //获取题目分数
            Integer score = questionIdToFullScore.get(questionId);
            gameDetailUnit.setScore(score);
            gameDetailUnit.setMemoryCost(judgeInfo.getMemory());
            gameDetailUnit.setTimeCost(judgeInfo.getTime());
        } else {
            gameDetailUnit.setScore(0);
            gameDetailUnit.setMemoryCost(99999L);
            gameDetailUnit.setTimeCost(99999L);
        }
        gameDetailUnit.setName(question.getTitle());
        gameDetailUnit.setId(questionId);
        return gameDetailUnit;
    }
}




