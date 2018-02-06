package com.lufi.matching.matchers;

import com.lufi.matching.Matcher;
import com.lufi.utils.Constants;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Sunny on 2018/1/4.
 */
public class BankMatcher implements Matcher, Serializable {

    private static volatile BankMatcher INSTANCE = null;

    private final Map<String, String> banks;

    private BankMatcher() {
        banks = Mapping.mapOf("bank-new", "bankprefix");
    }

    public static BankMatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (BankMatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BankMatcher();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * 基于Luhn算法,校验银行卡号是否有效
     *
     * @param bankId 银行卡号。
     * @return 是否有效，null和""都是false
     */
    @Override
    public boolean isValid(final String bankId) {
        if (bankId == null || (bankId.length() < 13 || bankId.length() > 19)) {
            return false;
        }
        int[] cardNoArr = new int[bankId.length()];
        for (int i = 0; i < bankId.length(); i++) {
            if (bankId.charAt(i) - '0' < 0 || bankId.charAt(i) - '0' > 9) {
                return false;
            }
            cardNoArr[i] = Integer.valueOf(String.valueOf(bankId.charAt(i)));
        }
        for (int i = cardNoArr.length - 2; i >= 0; i -= 2) {
            cardNoArr[i] <<= 1;
            cardNoArr[i] = cardNoArr[i] / 10 + cardNoArr[i] % 10;
        }
        int sum = 0;
        for (int i = 0; i < cardNoArr.length; i++) {
            sum += cardNoArr[i];
        }

        if ((sum % 10 != 0) || !check(bankId)) {
            return false;
        }

        return true;
    }

    @Override
    public String type() {
        return Constants.BANK;
    }

    /**
     * 获取银行编码
     *
     * @param bankId 银行卡号。
     * @return 银行编码
     */

    private boolean check(final String bankId) {
        if (banks.isEmpty()) {
            return false;
        }

        for (String key : banks.keySet()) {
            if (Pattern.matches(key, bankId)) {
                return true;
            }
        }

        return false;

    }
}
