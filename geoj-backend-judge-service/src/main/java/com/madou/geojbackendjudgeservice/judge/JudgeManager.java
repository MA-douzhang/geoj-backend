package com.madou.geojbackendjudgeservice.judge;

import com.madou.geojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.madou.geojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.madou.geojbackendjudgeservice.judge.strategy.JudgeContext;
import com.madou.geojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
