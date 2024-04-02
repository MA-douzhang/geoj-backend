package com.madou.geojbackendquestionservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.madou.geojbackendquestionservice.mapper.GameMapper;
import com.madou.geojbackendquestionservice.mapper.GameRankMapper;
import com.madou.geojbackendquestionservice.service.QuestionService;
import com.madou.geojbackendquestionservice.service.QuestionSubmitService;
import com.madou.geojbackendserviceclient.service.UserFeignClient;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.dto.game.GameDetail;
import com.madou.geojmodel.dto.game.GameDetailUnit;
import com.madou.geojmodel.dto.game.GameQuestionSubmitRequest;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitAddRequest;
import com.madou.geojmodel.entity.*;
import com.madou.geojbackendquestionservice.service.GameQuestionService;
import com.madou.geojbackendquestionservice.mapper.GameQuestionMapper;
import com.madou.geojmodel.enums.QuestionSubmitStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author MA_dou
 * @description 针对表【game_question(竞赛题目关系表)】的数据库操作Service实现
 * @createDate 2024-03-29 17:10:52
 */
@Service
public class GameQuestionServiceImpl extends ServiceImpl<GameQuestionMapper, GameQuestion>
        implements GameQuestionService {


}




