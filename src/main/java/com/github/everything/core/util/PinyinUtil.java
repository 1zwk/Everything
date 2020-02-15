package com.github.everything.core.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.*;

import static net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray;

public final class PinyinUtil {

    private PinyinUtil(){

    }


    /**
     * 存储汉字和他的所有拼音对应关系的表
     */
    private static Map<Character, Set<String>> map = new HashMap<>();

    /**
     * 获取拼音对象
     */
    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);//TODO？
    }

    //获取一个词组所有的可能全音
    public static List<String> getPinyin(String fileName) {
        return getPinyinGroup(getCharOfName(fileName));
    }

    private static List<String> getPinyinGroup(char[] chars) {
        if(chars == null){
            return new ArrayList<>();
        }

        int length = chars.length;
        List<String> result = new ArrayList<>();

        DFS(length,0,result,
                new ArrayList<>(),new ArrayList<>(),chars);

        return result;
    }


    private static void DFS(int MaxLength,int curIndex,List<String> result,
                                    List<String> fristList, List<String> allList,
                                    char[] chars) {
        //出递归条件：当当前下标=数组长度时结束，以为这已经全部遍历
        if(curIndex == MaxLength){
            //把全部拼音拼接成一个
            String s = buildString(allList);

            String s1 = buildString(fristList);
            //将得到字符串添加到结果集合中
            result.add(s);
            //得判断首字母的字符串是否已经添加到resultList中了，去重
            if (!result.contains(s1)) {
                result.add(s1);
            }
            return;
        }

        //先获取到当前字符
        char c = chars[curIndex];

        //获取当前字符的所有拼音
        Set<String> set = map.get(c);

        for(String s : set){
            //把一整个拼音存储
            allList.add(s);
            //把拼音首字母存储
            fristList.add(s.substring(0,1));
            //递归进入
            DFS(chars.length,curIndex+1,result,fristList,allList,
                    chars);
            //递归返回时删除最后一个元素
            allList.remove(allList.size()-1);
            //同理
            fristList.remove(fristList.size()-1);
        }


    }

    private static String buildString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for(String s : list){
            sb.append(s);
        }
        return sb.toString();
    }


    //获得一个字段的每一个汉字
    private static char[] getCharOfName(String fileName) {
        if(fileName.contains(".")){
            fileName  = fileName.substring(0, fileName.lastIndexOf("."));
        }
        char[] chars = fileName.toCharArray();

        initMap(chars);

        return chars;
    }

    //把汉字和对应的所有拼音存入map
    private static void initMap(char[] chars){
        for(char c : chars){
            try {
                String[] CharPinyin = PinyinHelper.toHanyuPinyinStringArray(c,format);

                Set<String> set = new HashSet<>(Arrays.asList(CharPinyin));

                //如果set为空说明，这不是汉字，是字母，直接添加进去就可以
                if(set.isEmpty()){
                    set.add(String.valueOf(c).toLowerCase());
                }

                map.put(c,set);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
    }

}
