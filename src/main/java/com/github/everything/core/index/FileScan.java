package com.github.everything.core.index;

import com.github.everything.core.interceptor.FileInterceptor;

public interface FileScan {

    /**
     * 遍历PATH
     *
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     *
     * @param fileInterceptor
     */
    void interceptor(FileInterceptor fileInterceptor);



}
