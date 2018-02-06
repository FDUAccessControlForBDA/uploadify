package com.lufi.matching.matchers;

import com.lufi.matching.Matcher;
import com.lufi.utils.Constants;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Sunny on 2018/1/4.
 */
public class PhoneMatcher implements Matcher, Serializable {

    private static volatile PhoneMatcher INSTANCE = null;

    private final Map<String, String> districts;
    private final Map<String, String> phoneNumbers;
    private final Map<String, String> diallingCodes;


    private PhoneMatcher() {
        districts = Mapping.mapOf("province-city");
        phoneNumbers = Mapping.mapOf("phone-numbers");
        diallingCodes = Mapping.mapOf("dialling-code");
    }

    public static PhoneMatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (PhoneMatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PhoneMatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 返回手机/电话号码对应的归属地。
     *
     * @param number 手机/电话号码。
     * @return 是否有效。
     */
    @Override
    public boolean isValid(final String number) {
        if (number == null || districts.isEmpty()) {
            return false;
        }
        if (number.startsWith("0")) {
            if (number.length() >= 3) {
                String prefix = number.substring(0, 3);
                if (diallingCodes.containsKey(prefix) && (number.substring(3).length() == 7 || number.substring(3).length() == 8)) {
                    return true;
                }
            }
            if (number.length() >= 4) {
                String prefix = number.substring(0, 4);
                if (diallingCodes.containsKey(prefix) && (number.substring(4).length() == 7 || number.substring(4).length() == 8)) {
                    return true;
                }
            }
        } else if (number.startsWith("1") && number.length() == 11) {
            String prefix = number.substring(0, 7);
            if (phoneNumbers.containsKey(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String type() {
        return Constants.PHONE;
    }
}
