package com.github.everything.core.monitor;

import com.github.everything.core.util.FileConvertThing;
import com.github.everything.core.util.HandlePath;
import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class FileWatchImpl implements FileWatch, FileAlterationListener {
    private FileIndexDao fileIndexDao;
    private FileAlterationMonitor monitor;

    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(100);
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
//        observer.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File directory) {

    }

    @Override
    public void onDirectoryChange(File directory) {

    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete" + directory);
    }

    @Override
    public void onFileCreate(File file) {
        //文件创建
        System.out.println("onFileCreat" + file);
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange" + file);
    }

    @Override
    public void onFileDelete(File file) {
        //文件删除
        System.out.println("onFileDelete" + file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));

    }

    @Override
    public void onStop(FileAlterationObserver observer) {
//        observer.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监视器，传入需要监视和无序监视的目录
     *
     * @param handlePath
     */
    @Override
    public void monitor(HandlePath handlePath) {
        for (String path : handlePath.getIncludePath()) {
            //传入监视目录和，文件过滤器
            FileAlterationObserver observer =
                    new FileAlterationObserver(path, pathname -> {
                        String currentPath = pathname.getAbsolutePath();
                        for (String excluedePath : handlePath.getExcludePath()) {
                            if (excluedePath.startsWith(currentPath)) {//因为有可能是目录，所以用startsWIth而不直接“=”
                                return false;
                            }
                        }
                        return true;
                    });

            observer.addListener(this);
            //添加一个观察者
            this.monitor.addObserver(observer);
        }
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
