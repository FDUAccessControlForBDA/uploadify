package com.lufi.matching.matchers;

import com.lufi.matching.Matcher;
import com.lufi.utils.Constants;

import java.io.Serializable;

/**
 * Created by Sunny on 2018/1/4.
 */
public class AddressMatcher implements Matcher, Serializable {

    private static volatile AddressMatcher INSTANCE = null;

    private static String[] cityMatcher = {"省", "市", "区", "县", "乡", "镇", "村"};
    private static String[] streetMatcher = {"大道", "路", "街", "弄", "宅"};
    private static String[] numberMatcher = {"队", "号", "丘", "组", "楼", "层", "室", "幢", "单元"};

    private AddressMatcher() {
    }

    public static AddressMatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (AddressMatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AddressMatcher();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public boolean isValid(String address) {
        if (address == null || address.length() == 0) {
            return false;
        }

        int index0 = index(address, cityMatcher);
        String tmp = address.substring(index0);

        int index1 = index(tmp, streetMatcher);
        tmp = tmp.substring(index1);

        int index2 = index(tmp, numberMatcher);

        if ((index0 > 0 || index1 > 0) && index2 > 0)
            return true;
        else
            return false;
    }

    @Override
    public String type() {
        return Constants.ADDRESS;
    }

    private static int index(String address, String[] matcher) {
        int index = 0;
        for (int i = 0; i < matcher.length; i++) {
            int max = address.indexOf(matcher[i]);
            if (max > index)
                index = max;
        }
        return index;
    }
}
