package com.lufi.matching;

import java.io.Serializable;

/**
 * Created by Sunny on 2018/1/11.
 */
public class MatchInfo implements Serializable {

    private String type; // 隐私信息的类型
    private String detail; // 隐私信息的详细内容
    private String location; // 隐私信息对应的行数

    public MatchInfo() {}

    public String getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    public String getLocation() {
        return location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
