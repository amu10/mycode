package cn.a.test;

import org.springframework.util.StringUtils;

public class TestFunction {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String s ="song;koko";
        String[] dd = StringUtils.tokenizeToStringArray(s, "o");
        for(String ss : dd){
            System.out.println(ss);
        }
    }

}
