package com.madou.geojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.madou.geojmodel.dto.questionRun.QuestionRunRequest;
import com.madou.geojmodel.dto.questionRun.QuestionRunResult;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitAddRequest;
import com.madou.geojmodel.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.madou.geojmodel.entity.QuestionSubmit;
import com.madou.geojmodel.entity.User;
import com.madou.geojmodel.vo.QuestionSubmitVO;

/**
* @author madou
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-08-07 20:58:53
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    /**
     * 根据题目id和用户id查询题目状态
     * @param questionId
     * @param id
     * @return
     */
    Integer getQuestionStatueById(long questionId, Long id);

    /**
     * 查询用户通过对应难度数量
     * @param difficulty
     * @param id
     * @return
     */
    Integer getQuestionDifficultyCountByUserId(Integer difficulty, Long id);
}
