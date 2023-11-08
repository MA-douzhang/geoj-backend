package com.madou.geojbackendquestionservice.controller.inner;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.madou.geojbackendquestionservice.mapper.QuestionMapper;
import com.madou.geojbackendquestionservice.service.QuestionService;
import com.madou.geojbackendquestionservice.service.QuestionSubmitService;
import com.madou.geojbackendserviceclient.service.QuestionFeignClient;
import com.madou.geojmodel.entity.Question;
import com.madou.geojmodel.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

    @Override
    public void updateQuestionAcceptedById(long questionId) {
       questionMapper.update(null, new UpdateWrapper<Question>()
                .setSql("acceptedNum = acceptedNum + 1").eq("id", questionId));
    }

}
