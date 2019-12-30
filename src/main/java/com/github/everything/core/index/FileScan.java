package com.github.everything.core.index;

import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.impl.FileIndexDaoImpl;
import com.github.everything.core.index.impl.FileScanImpl;
import com.github.everything.core.interceptor.FileInterceptor;
import com.github.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.everything.core.interceptor.impl.FilePrintInterceptor;
import com.github.everything.core.model.Thing;

import java.nio.file.Path;

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


    public static void main(String[] args) {
        DataSourceFactory.initDatabase();
        FileScanImpl fileScan = new FileScanImpl();
        FileInterceptor PrintInterceptor = new FilePrintInterceptor();

        fileScan.interceptor(PrintInterceptor);

        FileInterceptor fileIndexInterceptor = new FileIndexInterceptor(
                new FileIndexDaoImpl(DataSourceFactory.dataSource()));
        fileScan.interceptor(fileIndexInterceptor);
        fileScan.index("D:\\张文凯的文件");

    }
}
