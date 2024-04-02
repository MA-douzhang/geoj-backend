package com.madou.geojbackendquestionservice.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.madou.geojbackendquestionservice.service.GameQuestionService;
import com.madou.geojbackendquestionservice.service.GameService;
import com.madou.geojbackendquestionservice.service.GameUserService;
import com.madou.geojbackendquestionservice.service.QuestionService;
import com.madou.geojbackendserviceclient.service.UserFeignClient;
import com.madou.geojcommon.annotation.AuthCheck;
import com.madou.geojcommon.common.BaseResponse;
import com.madou.geojcommon.common.DeleteRequest;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.common.ResultUtils;
import com.madou.geojcommon.constant.UserConstant;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojcommon.exception.ThrowUtils;
import com.madou.geojmodel.dto.game.*;
import com.madou.geojmodel.dto.question.QuestionQueryRequest;
import com.madou.geojmodel.dto.user.UserQueryRequest;
import com.madou.geojmodel.entity.*;
import com.madou.geojmodel.vo.QuestionVO;
import com.madou.geojmodel.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题竞赛接口
 */
@RestController
@RequestMapping("/game")
@Slf4j
public class GameController {


    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private GameQuestionService gameQuestionService;

    @Resource
    private GameService gameService;

    @Resource
    private GameUserService gameUserService;

    @Resource
    private QuestionService questionService;
    /**
     * 竞赛提交题目
     *
     * @param questionSubmitRequest
     * @param request
     * @return 提交记录的 id
     */
    @PostMapping("/do")
    public BaseResponse<Long> doGameSubmit(@RequestBody GameQuestionSubmitRequest questionSubmitRequest,
                                           HttpServletRequest request) {
        if (questionSubmitRequest == null || questionSubmitRequest.getGameId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final User loginUser = userFeignClient.getLoginUser(request);
        long questionSubmitId = gameService.questionSubmit(questionSubmitRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 竞赛-题目创建
     *
     * @param gameQuestionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGame(@RequestBody GameQuestionAddRequest gameQuestionAddRequest, HttpServletRequest request) {
        if (gameQuestionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        List<Long> questionIdList = gameQuestionAddRequest.getQuestionIdList();
        List<Integer> questionFullScoreList = gameQuestionAddRequest.getQuestionFullScoreList();
        if (BeanUtil.isEmpty(questionIdList) || BeanUtil.isEmpty(questionFullScoreList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不能为空");
        }
        if (questionIdList.size() != questionFullScoreList.size()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请重新填入正确的参数");
        }
        Game game = new Game();
        BeanUtils.copyProperties(gameQuestionAddRequest, game);
        game.setCreateUserId(loginUser.getId());
        boolean result = gameService.save(game);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long gameId = game.getId();
        //保存竞赛和题目关系
        for (int i = 0; i < questionIdList.size(); i++) {
            GameQuestion gameQuestion = new GameQuestion();
            gameQuestion.setGameId(gameId);
            gameQuestion.setQuestionId(questionIdList.get(i));
            gameQuestion.setQuestionScore(questionFullScoreList.get(i));
            boolean save = gameQuestionService.save(gameQuestion);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(gameId);
    }

    /**
     * 更新（仅管理员）
     *
     * @param gameUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGame(@RequestBody GameUpdateRequest gameUpdateRequest) {
        if (gameUpdateRequest == null || gameUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long gameId = gameUpdateRequest.getId();
        Game gameOld = gameService.getById(gameId);
        Date startTime = gameOld.getStartTime();
        // 判断竞赛是否已经开始
        Date currentDate = new Date();
        // 判断竞赛是否已经开始
        if (startTime.before(currentDate)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "竞赛已经开始");
        }
        List<Long> questionIdList = gameUpdateRequest.getQuestionIdList();
        List<Integer> questionFullScoreList = gameUpdateRequest.getQuestionFullScoreList();
        if (BeanUtil.isEmpty(questionIdList) || BeanUtil.isEmpty(questionFullScoreList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不能为空");
        }
        if (questionIdList.size() != questionFullScoreList.size()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请重新填入正确的参数");
        }
        Game game = new Game();
        BeanUtils.copyProperties(gameUpdateRequest, game);
        boolean result = gameService.updateById(game);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        //删除竞赛题目关联表信息
        QueryWrapper<GameQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId", gameId);
        gameQuestionService.remove(queryWrapper);
        //保存竞赛和题目关系
        for (int i = 0; i < questionIdList.size(); i++) {
            GameQuestion gameQuestion = new GameQuestion();
            gameQuestion.setGameId(gameId);
            gameQuestion.setQuestionId(questionIdList.get(i));
            gameQuestion.setQuestionScore(questionFullScoreList.get(i));
            boolean save = gameQuestionService.save(gameQuestion);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(result);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGame(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Game game = gameService.getById(id);
        ThrowUtils.throwIf(game == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!game.getCreateUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = gameService.removeById(id);
        //删除竞赛题目关联表信息
        QueryWrapper<GameQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId", id);
        gameQuestionService.remove(queryWrapper);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<GameInfoVO> getGameById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Game game = gameService.getById(id);
        if (game == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        GameInfoVO gameInfoVO = gameService.getGameById(game);
        // 不是本人或管理员，不能直接获取所有信息
        if (!game.getCreateUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(gameInfoVO);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param gameQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<GameVO>> listGameByPage(@RequestBody GameQueryRequest gameQueryRequest,
                                                     HttpServletRequest request) {
        long current = gameQueryRequest.getCurrent();
        long size = gameQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<Game> gamePage = gameService.page(new Page<>(current, size),
                gameService.getQueryWrapper(gameQueryRequest));
        // 返回脱敏信息
        return ResultUtils.success(gameService.getGameVOPage(gamePage));
    }
    /**
     * 分页获取列表（封装类）
     *
     * @param gameQuestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GameQuestionVO>> listGameQuestionVOByPage(@RequestBody GameQuestionQueryRequest gameQuestionQueryRequest,
                                                               HttpServletRequest request) {
        long current = gameQuestionQueryRequest.getCurrent();
        long size = gameQuestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<GameQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId",gameQuestionQueryRequest.getGameId());
        List<GameQuestion> list = gameQuestionService.list(queryWrapper);
        Map<Long, Integer> questionIdMap= list.stream().collect(Collectors.toMap(GameQuestion::getQuestionId, GameQuestion::getQuestionScore));
        //题目id列表
        QueryWrapper<Question> queryWrapper1 = new QueryWrapper<>();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                queryWrapper1.in("id",new ArrayList<>(questionIdMap.keySet())));
        Page<GameQuestionVO> questionVOPage = questionService.getGameQuestionVOPage(questionPage, request);
        //获取分数
        questionVOPage.getRecords().forEach(item->{
            item.setFullScore(questionIdMap.get(item.getQuestionVO().getId()));
        });
        return ResultUtils.success(questionVOPage);
    }
    /**
     * 分页获取加入竞赛用户封装列表
     *
     * @param gameQuestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/user/list")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody GameQuestionQueryRequest gameQuestionQueryRequest,
                                                       HttpServletRequest request) {
        if (gameQuestionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = gameQuestionQueryRequest.getCurrent();
        long size = gameQuestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<GameUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gameId",gameQuestionQueryRequest.getGameId());
        List<GameUser> gameUserList = gameUserService.list(queryWrapper);
        List<Long> userIdList = gameUserList.stream().map(GameUser::getUserId).collect(Collectors.toList());
        List<User> userList = userFeignClient.listByIds(userIdList);
        Page<UserVO> userVOPage = new Page<>(current, size, userList.size());
        List<UserVO> userVOList = userList.stream().map(user -> userFeignClient.getUserVO(user)).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 竞赛-用户加入
     *
     * @param gameJoinRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinGame(@RequestBody GameJoinRequest gameJoinRequest, HttpServletRequest request) {
        if (gameJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        Long gameId = gameJoinRequest.getGameId();
        Long userId = loginUser.getId();

        Boolean result = gameUserService.joinGame(gameId, userId);

        return ResultUtils.success(result);
    }
}
