package com.lufi.matching;

/**
 * Created by Sunny on 2018/1/4.
 */
public interface Matcher {
    boolean isValid(final String input);

    String type();
}
