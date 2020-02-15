package com.github.everything.core;

import com.github.everything.config.EverythingPlusConfig;
import com.github.everything.core.util.HandlePath;
import com.github.everything.DB.DataSourceFactory;
import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.dao.FileIndexDaoImpl;
import com.github.everything.core.index.FileScan;
import com.github.everything.core.index.FileScanImpl;
import com.github.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.everything.core.interceptor.impl.FilePrintInterceptor;
import com.github.everything.core.interceptor.impl.ThingClearInterceptor;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;
import com.github.everything.core.monitor.FileWatch;
import com.github.everything.core.monitor.FileWatchImpl;
import com.github.everything.core.search.FileSearch;
import com.github.everything.core.search.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class EverythingPlusManager {

    //文件监控
    private FileWatch fileWatch;

    //文件搜索
    private FileSearch fileSearch;

    //文件扫描
    private FileScan fileScan;

    //
    private static volatile EverythingPlusManager manager;

    //线程池先不固定大小，根据具体使用判断
    private ExecutorService executorService;

    private EverythingPlusManager() {
        this.initComponent();
    }

    /**
     * 清理删除的文件
     */
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);


    //单例模式
    public static EverythingPlusManager getInstance() {
        if (manager == null) {
            synchronized (EverythingPlusManager.class) {
                if (manager == null) {
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

        //初始化数据库
        initOrResetDatabase();

        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);

        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl();

        //发布代码的时候是不需要的，仅为了调试
        this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));

        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);

        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);//守护线程   问题:什么是守护线程


        //文件监控对象
        this.fileWatch = new FileWatchImpl(fileIndexDao);

    }

    //检查数据库
//    private void checkDatabase() {
//        String fileName = EverythingPlusConfig.getInstance().getH2IndexPath()+ ".mv.db";
//        File dbFile = new File(fileName);
//        //如果是文件或者这个目录不存在就初始化
//        if(dbFile.isFile()&&!dbFile.exists()){
//            //初始化数据库（一般先检查是否存在数据库，因为如果存在，初始化后数据会消失）
//            DataSourceFactory.initDatabase();
//        }
//    }

    private void initOrResetDatabase() {
        DataSourceFactory.initDatabase();
    }


    /**
     * 检索
     */
    public List<Thing> search(Condition condition) {
        //NOTICE 扩展
        //Stream 流式处理
        return this.fileSearch.search(condition)
                .stream().filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    //如果存在这个文件就返回，如果不存在就删除并且不返回
                    boolean flag = f.exists();
                    if (!flag) {
                        //删除
                        thingClearInterceptor.apply(thing);
                    }
                    return flag;
                }).collect(Collectors.toList());
    }

    /**
     * 索引
     */
    public void buildIndex() {
        initOrResetDatabase();
        Set<String> directories = EverythingPlusConfig.getInstance().getIncludePath();
        /**
         * 依据目录的大小来创建合适大小的线程池，并且命名
         */
        if (this.executorService == null) {
            this.executorService = Executors.newFixedThreadPool(directories.size()
                   /* , new ThreadFactory() {
                        //一个可能原子性更新的int值，默认为0，也可以以给定参数值开始
                        private final AtomicInteger threadId = new AtomicInteger();

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                            return thread;
                        }
                    }*/);
        }

        //监控线程是否执行完毕，相当于一个标记位
        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());

        System.out.println("Build index start");
        //多线程的把文件建立索引
        for (String path : directories) {
            System.out.println(directories.size());
            this.executorService.submit(() -> {
                EverythingPlusManager.this.fileScan.index(path);
                //当前任务完成，值-1
                countDownLatch.countDown();
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
        //AtomicBoolean.compareAndSet(expect, update)意思是如果当前值=expect值，就把当前值改为update。
        //放在这里意味着如果线程是关闭状态，就把线程状态原子性的改为启动，
        if (this.backgroundClearThreadStatus.compareAndSet(false, true)) {
            this.backgroundClearThread.start();
        } else {
            System.out.println("Cant repeat start BackgroundClearThread");
        }
    }

    /**
     * 启动文件监听
     */
    public void startFileSystemMonitor(){
        EverythingPlusConfig config = EverythingPlusConfig.getInstance();
        HandlePath handlePath = new HandlePath();
        handlePath.setExcludePath(config.getExcludePath());
        handlePath.setIncludePath(config.getIncludePath());
        this.fileWatch.monitor(handlePath);
        //异步启动，不要阻塞其他线程
        new Thread(() -> {
            System.out.println("启动");
        fileWatch.start();}).start();
    }


}
