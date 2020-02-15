package com.github.everything.core.monitor;

import com.github.everything.core.util.HandlePath;

public interface FileWatch {
    /**
     * 监听启动
     */
    void start();
    /**
     * 监听的目录
     */
    void monitor(HandlePath handlePath);
    /**
     * 监听停止
     */
    void stop();
}
