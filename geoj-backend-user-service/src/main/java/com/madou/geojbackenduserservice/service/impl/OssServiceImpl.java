package com.madou.geojbackenduserservice.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSSClient;
import com.madou.geojbackenduserservice.service.IOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class OssServiceImpl implements IOssService {

    @Value("${aliyun.oss.maxSize}")
    private int maxSize;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.dir.prefix}")
    private String dirPrefix;

    @Resource
    private OSSClient ossClient;
    @Override
    public String upload(MultipartFile file) {
        try {
            return upload(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String upload(InputStream inputStream, String name) {
        String objectName = getBucketName(name);
        // 创建PutObject请求。
        ossClient.putObject(bucketName, objectName, inputStream);
        return formatPath(objectName);
    }
    private String getBucketName(String url){
        String ext = "";
        String[] imageExtension = new String[]{"png","jpg"};
        for(String extItem:imageExtension){
            if(url.contains(extItem)){
                ext = url;
                break;
            }
        }
        return dirPrefix+ DateUtil.today()+"/"+ IdUtil.randomUUID()+ext;
    }

    private String formatPath(String objectName){
        return "https://"  +bucketName+"."+ ossClient.getEndpoint().getHost() + "/" + objectName;
    }
}
