package com.madou.geojbackenduserservice.controller;

import com.madou.geojbackenduserservice.service.IOssService;
import com.madou.geojcommon.common.BaseResponse;
import com.madou.geojcommon.common.ResultUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/ossController")
public class OssController {
    @Resource
    private IOssService ossService;

    @PostMapping("/upload")
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest req)  {
        return ResultUtils.success(ossService.upload(file));
    }
}
