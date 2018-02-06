package com.lufi.preproccess;

import java.util.Map;

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
}
