package com.madou.geojai;

import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Ai控制器
 */
@Service
public class AiManager {
    @Resource
    private YuCongMingClient client;

    public String doChat(String message,Long modelId){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);//模型id
        devChatRequest.setMessage(message);//发送消息
        // 获取响应
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        if (response == null){
            throw new RuntimeException("AI 响应错误");
        }
        return response.getData().getContent();
    }

}
