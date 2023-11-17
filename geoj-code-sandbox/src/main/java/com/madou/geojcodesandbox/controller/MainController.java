package com.madou.geojcodesandbox.controller;

import com.madou.geojcodesandbox.template.code.CodeSandboxFactory;
import com.madou.geojcodesandbox.template.code.CodeSandboxTemplate;
import com.madou.geojcodesandbox.template.code.JavaNativeAcmSandbox;
import com.madou.geojcodesandbox.model.ExecuteCodeRequest;
import com.madou.geojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("/")
public class MainController {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private CodeSandboxFactory codeSandboxFactory;

    @GetMapping("/health")
    public String healthCheck() {
        return "ok";
    }

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                    HttpServletResponse response) {
        // 基本的认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        CodeSandboxTemplate sandboxTemplate = codeSandboxFactory.newInstance(executeCodeRequest.getLanguage());
        return sandboxTemplate.executeCode(executeCodeRequest);
    }
}
