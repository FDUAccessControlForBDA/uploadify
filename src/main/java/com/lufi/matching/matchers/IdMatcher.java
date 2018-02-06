package com.lufi.matching.matchers;

import com.lufi.matching.Matcher;
import com.lufi.utils.Constants;
import com.lufi.utils.DateValidator;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by Sunny on 2018/1/4.
 */
public class IdMatcher implements Matcher, Serializable {

    private static volatile IdMatcher INSTANCE = null;

    private final Map<String, String> region;
    private final int[] PARITY_LIST = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    private final int[] POWER_LIST = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    private IdMatcher() {
        region = Mapping.mapOf("iddb");
    }

    public static IdMatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (PhoneMatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new IdMatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 身份证号是否基本有效
     *
     * @param id 身份证号。
     * @return 是否有效，null和""都是false
     */
    @Override
    public boolean isValid(final String id) {
        if (id == null || (id.length() != 15 && id.length() != 18))
            return false;
        final char[] cs = id.toUpperCase().toCharArray();
        // （1）校验位数
        int power = 0;
        for (int i = 0; i < cs.length; i++) {// 循环比正则表达式更快
            if (i == cs.length - 1 && cs[i] == 'X')
                break;// 最后一位可以是X或者x
            if (cs[i] < '0' || cs[i] > '9')
                return false;
            if (i < cs.length - 1)
                power += (cs[i] - '0') * POWER_LIST[i];
        }
        // （2）校验区位码
        if (!region.containsKey(id.substring(0, 6))) {
            return false;
        }

        //校验日期
        String dateString = id.length() == 15 ? "19" + id.substring(6, 12) : id.substring(6, 14);
        Date date = DateValidator.getInstance().validate(dateString, "yyyymmdd");

        if (date == null)
            return false;

        // （6）校验“校验码”
        if (id.length() == 15)
            return true;
        return cs[cs.length - 1] == PARITY_LIST[power % 11];
    }

    @Override
    public String type() {
        return Constants.ID;
    }
}
