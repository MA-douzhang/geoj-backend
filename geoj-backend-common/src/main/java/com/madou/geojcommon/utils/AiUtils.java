package com.madou.geojcommon.utils;

import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description 处理ai返回工具
 * @date 2023/11/18 12:36:27
 */
public class AiUtils {
    /**
     * AI分析题目之后处理数据方法
     * @param result
     * @return
     */
    public static String[] strToAnswerAi(String result) {
        String solutionIdea;
        String reason;
        String codeAi;
        if (StringUtils.isBlank(result)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }

        String[] splits = result.split("【【【【【【");
        //如果生成的没有【【【【【
        if (splits.length == 4) {
            solutionIdea = splits[1].trim();
            reason = splits[2].trim();
            codeAi = splits[3].trim();
        } else {
            int solutionIdeaIndex = result.indexOf("题目解题思路");
            int reasonIndex = result.indexOf("代码犯错原因");
            int codeAiIndex = result.indexOf("正确的解题代码");
            solutionIdea = result.substring(solutionIdeaIndex, reasonIndex);
            reason = result.substring(reasonIndex, codeAiIndex);
            codeAi = result.substring(codeAiIndex);
        }
        solutionIdea = solutionIdea.replace("【【【【【【", "");
        reason = reason.replace("【【【【【【", "");
        codeAi = codeAi.replace("【【【【【【", "");
        return new String[]{solutionIdea, reason, codeAi};
    }
}
