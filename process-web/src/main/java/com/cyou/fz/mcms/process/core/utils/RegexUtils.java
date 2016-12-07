package com.cyou.fz.mcms.process.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cnJason on 2016/12/7.
 */
public class RegexUtils {

    public  static String getImageType(String url){
        final  Pattern pattern = Pattern.compile("\\S*[?]\\S*");
        Matcher matcher = pattern.matcher(url);

        String[] spUrl = url.toString().split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];

        if(matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            return spEndUrl[0].split("\\.")[1];
        }
        return endUrl.split("\\.")[1];
    }


    public static void main(String[] args) {
        System.out.println(getImageType("http://wwww.17173.com/a.jpg"));

    }
}
