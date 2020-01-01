package com.github.everything.cmd;

import com.github.everything.config.EverythingPlusConfig;
import com.github.everything.core.EverythingPlusManager;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class EverythingCmdApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //解析参数
        parseParams(args);

        //欢迎
        welcome();

        //统一调度器
        EverythingPlusManager manager = EverythingPlusManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();

        //启动监控
//TODO        manager.startFileSystemMonitor();

        //交互式
        interactive(manager);

    }

    private static void parseParams(String[] args) {
        /**
         * 处理时如果用户输入参数不对，直接使用默认值
         */
        for(String param : args){
            if(param.startsWith("--maxReturnNum=")){
                int index = param.indexOf("=");
                if(index < "--maxReturnNum".length() -1){

                }
            }
            if(param.startsWith("--depthOrderByAsc=")){

            }
            if(param.startsWith("--includePath=")){

            }
            if(param.startsWith("--excludePath=")){

            }
        }
    }

    private static void interactive(EverythingPlusManager manager) {
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if (input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!values[0].equals("search")) {
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }
            switch (input) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void search(EverythingPlusManager manager, Condition condition) {
        //统一调度器中的search
        //name fileType limit orderByAsc

        //get搜索的默认大小和顺序
        condition.setLimit(EverythingPlusConfig.getInstance().getMaxReturnNum());
        condition.setOrderByAsc(EverythingPlusConfig.getInstance().getDepthOrderAsc());
        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList) {
            System.out.println(thing.getPath());
        }

    }

    private static void index(EverythingPlusManager manager) {
        //统一调度器中的index
        new Thread(manager::buildIndex).start();//lambda表达式
//        lambda的内容：
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                manager.buildIndex();
//            }
//        }).start();
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用，Everything Plus");
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }


}
