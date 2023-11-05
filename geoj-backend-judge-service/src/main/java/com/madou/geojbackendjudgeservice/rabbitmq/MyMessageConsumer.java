package com.madou.geojbackendjudgeservice.rabbitmq;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.madou.geojbackendjudgeservice.judge.JudgeService;
import com.madou.geojbackendserviceclient.service.QuestionFeignClient;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.codesandbox.JudgeInfo;
import com.madou.geojmodel.entity.QuestionSubmit;
import com.madou.geojmodel.enums.JudgeInfoMessageEnum;
import com.madou.geojmodel.enums.QuestionSubmitStatusEnum;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    @Resource
    private QuestionFeignClient questionFeignClient;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        try {
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            //封装返回信息
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(JudgeInfoMessageEnum.SANDBOX_ERROR.getValue());
            // 3）更改判题（题目提交）的状态为 “结束”，信息为沙盒中返回的信息
            QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            channel.basicNack(deliveryTag, false, false);

        }
    }

}
