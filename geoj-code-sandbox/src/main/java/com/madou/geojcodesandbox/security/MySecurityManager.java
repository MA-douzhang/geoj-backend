package com.madou.geojcodesandbox.security;

import java.security.Permission;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-code-sandbox
 * @description 沙箱安全管理
 * @date 2023/10/12 19:11:20
 */
public class MySecurityManager extends SecurityManager{

    //检测使用权限
    @Override
    public void checkPermission(Permission perm) {
//        super.checkPermission(perm);
    }

    //检测程序是否可以执行文件
    @Override
    public void checkExec(String cmd) {
        super.checkExec(cmd);
    }

    //检测程序是否可以读取文件
    @Override
    public void checkRead(String file) {
//        super.checkRead(file);
    }
    //检测程序是否可以读写文件
    @Override
    public void checkWrite(String file) {
//        super.checkWrite(file);
    }
    // 检测程序是否允许删除文件
    @Override
    public void checkDelete(String file) {
//        super.checkDelete(file);
    }
    // 检测程序是否允许连接网络
    @Override
    public void checkConnect(String host, int port) {
//        super.checkConnect(host, port);
    }
}
