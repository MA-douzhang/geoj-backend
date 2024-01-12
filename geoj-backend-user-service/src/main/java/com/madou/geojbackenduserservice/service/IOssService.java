package com.madou.geojbackenduserservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description oss对象
 * @date 2024/1/12 20:43:52
 */
public interface IOssService {

    String upload(MultipartFile file);
    String upload(InputStream inputStream, String name);
}
