package com.lufi.matching.matchers;

import com.lufi.matching.Matcher;
import com.lufi.utils.Constants;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Created by symsimmy on 2018/1/4
 */
public class IpMatcher implements Matcher, Serializable {

    private static volatile IpMatcher INSTANCE = null;

    private IpMatcher() {
    }

    public static IpMatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (IpMatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new IpMatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * IP地址是否基本有效
     *
     * @param ipAddress IP地址。
     * @return 是否有效，null和""都是false
     */
    @Override
    public boolean isValid(final String ipAddress) {
        String pattern = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
        return Pattern.matches(pattern, ipAddress);
    }

    @Override
    public String type() {
        return Constants.IP;
    }
}
