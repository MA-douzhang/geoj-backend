package com.madou.geojbackenduserservice;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description oss测试
 * @date 2024/1/12 20:13:15
 */

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.*;

import java.io.ByteArrayInputStream;

public class oss {

    public static void main(String[] args) throws Exception {
// 授权STSAssumeRole访问的Region。以华东1（杭州）为例，其它Region请根据实际情况填写。
        String region = "cn-chendu";
// 从环境变量中获取RAM用户的访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = "*";
        String accessKeySecret = "*";
// 填写RAM角色的ARN信息，即需要扮演的角色ID。
        String roleArn = "*";

// 使用代码嵌入的RAM用户的访问密钥配置访问凭证。
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
        EnvironmentVariableCredentialsProvider credentialsProviders = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 填写Bucket名称，例如examplebucket。
        String bucketName = "geoj-bucket";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = "exampledir/exampleobject.txt";
        String endpoint = "https://oss-cn-chengdu.aliyuncs.com";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        try {
            String content = "Hello OSS";
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
