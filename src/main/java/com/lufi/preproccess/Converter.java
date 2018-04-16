package com.lufi.preproccess;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sunny on 2018/1/4.
 */
public interface Converter {
    String convert(String fileName);
    String getFileName();
    String getNewFileName();
    Map<String, String> getMap();
    void doInit(final String fileName, final String suffix);
    long getLines();
    long getSize();
    default String regexNonePrintChar(String content){
        if(content.length() != content.getBytes().length){
            Pattern pattern = Pattern.compile("\\s+|[\\u00a0\\u3000\\u3002\\uff1f\\uff01\\uff0c\\u3001\\uff1b\\uff1a\\u300c\\u300d" +
                    "\\u300e\\u300f\\u2018\\u2019\\u201c\\u201d\\uff08\\uff09\\u3014\\u3015\\u3010\\u3011\\u2014\\u2026" +
                    "\\u2013\\uff0e\\u300a\\u300b\\u3008\\u3009]+|[\\\\n]");
            Matcher matcher = pattern.matcher(content);
            return matcher.replaceAll(",");
        }else{
            return content;
        }

    }
}
