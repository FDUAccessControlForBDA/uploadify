package com.lufi.matching;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sunny on 2018/1/4.
 */
public class Matchers implements Serializable {

    public Matchers() {
    }

    private ArrayList<Matcher> matchers = new ArrayList<>();


    // 添加新的匹配方法到Matchers中，使得匹配方法灵活可扩展
    public void addMatcher(Matcher matcher) {
        matchers.add(matcher);
    }

    // 对目标项进行隐私信息匹配，返回匹配到的结果类型
    // 未匹配到返回null，匹配到返回对应的信息种类
    public String match(String item) {
        for (Matcher m : matchers) {
            if (m.isValid(item)) {
                return m.type();
            }
        }
        return null;
    }

}
