package com.lufi.matching.matchers;

import com.lufi.matching.Matcher;
import com.lufi.utils.Constants;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Created by symsimmy on 2018/1/4
 */
public class MacMatcher implements Matcher, Serializable {

    private static volatile MacMatcher INSTANCE = null;

    private MacMatcher() {
    }

    public static MacMatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (MacMatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MacMatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * MAC地址是否基本有效
     *
     * @param macAddress MAC地址。
     * @return 是否有效，null和""都是false
     */
    @Override
    public boolean isValid(final String macAddress) {
        String pattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        return Pattern.matches(pattern, macAddress);
    }

    @Override
    public String type() {
        return Constants.MAC;
    }
}
