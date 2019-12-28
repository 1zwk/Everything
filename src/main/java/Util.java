import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;

public class Util {

    public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String[] pinyins1 = PinyinHelper.toHanyuPinyinStringArray(
                'a',format);
        String[] pinyins2 = PinyinHelper.toHanyuPinyinStringArray(
                '只',format);
        String[] pinyins3 = PinyinHelper.toHanyuPinyinStringArray(
                '和',format);
        System.out.println(Arrays.toString(pinyins1));
        System.out.println(Arrays.toString(pinyins2));
        System.out.println(Arrays.toString(pinyins3));
        char[] inputs = "中华人民共和国".toCharArray();
        for(char input : inputs){

        }
        // （1）inputs char[] 转换为String[][]
//        String[][]
        // （2）String[][]转换为拼音的排列组合：Set<String>
        // 长（zhong,chang）和（he,huo,hai,he,hu）黑（...）
    }
}
