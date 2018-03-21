package com.lufi.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimerUtil {
    //获取系统当前时间
    public static Timestamp getCurrentTime(){
        Date currentDate = new Date();//获得系统时间.
        String currentTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate);//将时间格式转换成符合Timestamp要求的格式.
        Timestamp currentTime = Timestamp.valueOf(currentTimeString);//把时间转换
        return currentTime;
    }
}
