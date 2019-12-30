package com.github.everything.core;

import com.github.everything.config.EverythingPlusConfig;
import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.dao.impl.FileIndexDaoImpl;
import com.github.everything.core.index.FileScan;
import com.github.everything.core.index.impl.FileScanImpl;
import com.github.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;
import com.github.everything.core.search.FileSearch;
import com.github.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EverythingPlusManager {

    private FileSearch fileSearch;

    private FileScan fileScan;

    private static volatile EverythingPlusManager manager;

    //线程池先不固定大小，根据具体使用判断
    private ExecutorService executorService;

    private EverythingPlusManager(){
        this.initComponent();
    }

    /**
     * 清理删除的文件
     */
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);


    //单例模式
    public static EverythingPlusManager getInstance(){
        if(manager == null){
            synchronized (EverythingPlusManager.class){
                if(manager == null){
                    manager = new EverythingPlusManager();
                }
            }
        }
        return manager;
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        initOrResetDatabase();

        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);

        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl();
        //发布代码的时候是不需要的
//        this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));

        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);

        //文件监控对象
        this.fileWatch = new FileWatchImpl(fileIndexDao);

    }

    /**
     * 调用数据库初始化
     */
    public void initOrResetDatabase(){
        DataSourceFactory.initDatabase();
    }

    /**
     * 检索
     */
    public List<Thing> search(Condition condition){
        //NOTICE 扩展
        return this.fileSearch.search(condition);
    }

    /**
     * 索引
     */
    public void buildIndex(){
        Set<String> directories = EverythingPlusConfig.getInstance().getIncludePath();

        /**
         * 依据目录的大小来创建合适大小的线程池，并且命名
         */
        if(this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(directories.size()
                    , new ThreadFactory() {

                        //一个可能原子性更新的int值，默认为0，也可以以给定参数值开始
                        private final AtomicInteger threadId = new AtomicInteger();
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread();
                            thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                            return thread;
                        }
                    });
        }

        //监控线程是否执行完毕，相当于一个标记位
        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());

        System.out.println("Build index start");
        //多线程的把文件建立索引
        for(String path : directories){
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    EverythingPlusManager.this.fileScan.index(path);
                    //当前任务完成，值-1
                    countDownLatch.countDown();
                }
            });
        }

        //阻塞，直到任务完成（值为0）
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index end");
    }

    /**
     * 启动清理线程
     */
    public void startBackgroundClearThread() {
        if (this.backgroundClearThreadStatus.compareAndSet(false, true)) {
            this.backgroundClearThread.start();
        } else {
            System.out.println("Cant repeat start BackgroundClearThread");
        }
    }


}
