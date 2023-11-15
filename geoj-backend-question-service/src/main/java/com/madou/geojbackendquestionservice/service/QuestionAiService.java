package com.madou.geojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.madou.geojai.model.AnswerAi;
import com.madou.geojmodel.dto.question.QuestionQueryRequest;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* 题目ai服务
*/

public interface QuestionAiService {
    AnswerAi getAnswerAi(Long submitId);

}
