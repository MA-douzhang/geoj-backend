package com.madou.geojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.madou.geojmodel.dto.game.GameQuestionVO;
import com.madou.geojmodel.dto.question.QuestionQueryRequest;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author madou
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-08-07 20:58:00
*/
public interface QuestionService extends IService<Question> {


    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 获取题目难度数量
     *
     * @return
     */
    Long getQuestionDifficultyNum(Integer difficulty);

    /**
     * 分页获取竞赛题目封装
     * @param questionPage
     * @param request
     * @return
     */
    Page<GameQuestionVO> getGameQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);
}
