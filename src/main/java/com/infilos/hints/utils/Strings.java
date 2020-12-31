package com.infilos.hints.utils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.*;
import java.util.stream.Collectors;

public final class Strings {
    private Strings() {
    }

    public static boolean isBlank(String string) {
        return string==null || string.trim().length()==0;
    }

    public static boolean nonBlank(String string) {
        return !isBlank(string);
    }

    public static boolean isChinese(String string) {
        if (isBlank(string)) {
            return false;
        }

        for (char c : string.toCharArray()) {
            if (!Pinyin.isChinese(c)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isCapitals(String string) {
        if (isBlank(string) || string.length() > 6) {
            return false;
        }

        for (int i = 0; i < string.length(); ++i) {
            if (!Character.isUpperCase(string.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static final Set<String> PinyinSyllables = new HashSet<String>() {{
        addAll(Arrays.asList("a ai an ang ao".split(" ")));
        addAll(Arrays.asList("ba bai ban bang bao bei ben beng bi bian biao bie bin bing bo bu".split(" ")));
        addAll(Arrays.asList("ca cai can cang cao ce cen ceng cha chai chan chang chao che chen cheng chi chong chou chu chua chuai chuan chuang chui chun chuo ci cong cou cu cuan cui cun cuo".split(" ")));
        addAll(Arrays.asList("da dai dan dang dao de dei den deng di dia dian diao die ding diu dong dou du duan dui dun duo".split(" ")));
        addAll(Arrays.asList("e ei en eng er".split(" ")));
        addAll(Arrays.asList("fa fan fang fei fen feng fiao fo fou fu".split(" ")));
        addAll(Arrays.asList("ga gai gan gang gao ge gei gen geng gong gou gu gua guai guan guang gui gun guo".split(" ")));
        addAll(Arrays.asList("ha hai han hang hao he hei hen heng hong hou hu hua huai huan huang hui hun huo".split(" ")));
        addAll(Arrays.asList("ji jia jian jiang jiao jie jin jing jiong jiu ju juan jue jun".split(" ")));
        addAll(Arrays.asList("ka kai kan kang kao ke kei ken keng kong kou ku kua kuai kuan kuang kui kun kuo".split(" ")));
        addAll(Arrays.asList("la lai lan lang lao le lei leng li lia lian liang liao lie lin ling liu lo long lou lu luan lue lun luo lv".split(" ")));
        addAll(Arrays.asList("ma mai man mang mao me mei men meng mi mian miao mie min ming miu mo mou mu".split(" ")));
        addAll(Arrays.asList("na nai nan nang nao ne nei nen neng ni nian niang niao nie nin ning niu nong nou nu nuan nue nun nuo nü".split(" ")));
        addAll(Arrays.asList("o ou".split(" ")));
        addAll(Arrays.asList("pa pai pan pang pao pei pen peng pi pian piao pie pin ping po pou pu".split(" ")));
        addAll(Arrays.asList("qi qia qian qiang qiao qie qin qing qiong qiu qu quan que qun".split(" ")));
        addAll(Arrays.asList("ran rang rao re ren reng ri rong rou ru rua ruan rui run ruo".split(" ")));
        addAll(Arrays.asList("sa sai san sang sao se sen seng sha shai shan shang shao she shei shen sheng shi shou shu shua shuai shuan shuang shui shun shuo si song sou su suan sui sun suo".split(" ")));
        addAll(Arrays.asList("ta tai tan tang tao te tei teng ti tian tiao tie ting tong tou tu tuan tui tun tuo".split(" ")));
        addAll(Arrays.asList("wa wai wan wang wei wen weng wo wu".split(" ")));
        addAll(Arrays.asList("xi xia xian xiang xiao xie xin xing xiong xiu xu xuan xue xun".split(" ")));
        addAll(Arrays.asList("ya yan yang yao ye yi yin ying yo yong you yu yuan yue yun".split(" ")));
        addAll(Arrays.asList("za zai zan zang zao ze zei zen zeng zha zhai zhan zhang zhao zhe zhei zhen zheng zhi zhong zhou zhu zhua zhuai zhuan zhuang zhui zhun zhuo zi zong zou zu zuan zui zun zuo".split(" ")));
    }};
    
    public static boolean isPinyin(String string) {
        if (isBlank(string)) {
            return false;
        }

        for (int i = 0; i < string.length(); ++i) {
            if (!Character.isLetter(string.charAt(i))) {
                return false;
            }
        }

        String lowercase = string.toLowerCase();

        return PinyinSyllables.stream().anyMatch(lowercase::startsWith) &&
            PinyinSyllables.stream().anyMatch(lowercase::endsWith);
    }

    /**
     * 获取中文词汇的拼音全拼: 词汇 -> CIHUI
     */
    public static String formtPinyin(String word) {
        if (isBlank(word) || !isChinese(word)) {
            return word;
        }

        return Pinyin.toPinyin(word, "");
    }

    /**
     * 获取中文词汇首字符构成的字符串: 词汇 -> [CI,HUI] -> CH
     */
    public static String formatCapital(String word) {
        if (isBlank(word) || !isChinese(word)) {
            return word;
        }

        return Arrays.stream(Pinyin.toPinyin(word, ",").split(","))
            .filter(Strings::nonBlank)
            .map(pinyin -> String.valueOf(pinyin.charAt(0)))
            .collect(Collectors.joining());
    }
}
