package com.lufi.core;

import com.lufi.matching.MatchInfo;
import com.lufi.matching.Matchers;
import com.lufi.matching.matchers.*;
import com.lufi.preproccess.Converter;
import com.lufi.preproccess.ConverterFactory;
import com.lufi.snapshot.SnapShot;
import com.lufi.utils.FilenameUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Finder implements Serializable {
    private static Matchers matchers = new Matchers();
    private ArrayList<MatchInfo> matchInfoList;
    private static ArrayList<String> filePathList = new ArrayList<>();
    private static String filePath = null;
    private String convertedFilePath = null;
    private String outputPath = null;
    private Converter converter = null;

    private static SparkConf conf = null;
    private static JavaSparkContext sc = null;
    private SnapShot snapShot = null;
    private static int index = 0;
    private static long processTime = 0;

    private static volatile Finder INSTANCE = null;

    private Finder() {
        matchInfoList = new ArrayList<>();
        snapShot = new SnapShot();
        conf = new SparkConf().setAppName("spark").setMaster("local[*]").set("spark.driver.maxResultSize", "20g");
        sc = new JavaSparkContext(conf);
        System.out.println("java spark context");
        matchers = new Matchers();
        matchers.addMatcher(IdMatcher.getInstance());
        matchers.addMatcher(PhoneMatcher.getInstance());
        matchers.addMatcher(BankMatcher.getInstance());
        matchers.addMatcher(MailMatcher.getInstance());
    }


    public static Finder getInstance() {
        if (INSTANCE == null) {
            synchronized (Finder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Finder();
                }
            }
        }
        return INSTANCE;
    }


    public void input(final String path) {
        System.out.println("input");
        if (path == null) {
            return;
        } else {
            filePath = path;
        }
        System.out.println("------文件输入成功！------");
    }


    public boolean convert() {
        System.out.println("in convert");
        ConverterFactory factory = new ConverterFactory();
        String extension = FilenameUtils.getExtension(filePath);
        converter = factory.getConverter(extension);
        if (converter == null) {
            return false;
        }
        converter.convert(filePath);
        convertedFilePath = converter.getNewFileName();
        System.out.println("------文件预处理成功！------");
        return true;
    }

    public void process() {
        matchInfoList.clear();
        JavaRDD<String> dataFile = sc.textFile(convertedFilePath);
        JavaRDD<String> lines = dataFile.flatMap(line -> Arrays.asList(line.split(System.getProperty("line.separator"))).iterator());
        List<String> output = lines.collect();
        for (String x : output) {
            matchx(x);
        }
        System.out.println("------隐私数据匹配成功！------");
    }

    private String matchx(final String x) {
        String[] splitx = x.split(",");
        int count = splitx.length;
        String ret = "";
        String detail = "";
        boolean flag = false;
        for (int i = 1; i < count; i++) {
            String element = splitx[i];
            String returnFlag = matchers.match(element);
            if (returnFlag != null) {
                ret += returnFlag + ",";
                flag = true;
                detail += splitx[i] + ",";
            }
        }
        if (ret.endsWith(",")) {
            ret = ret.substring(0, ret.length() - 1);
        }

        if (detail.endsWith(",")) {
            detail = detail.substring(0, detail.length() - 1);
        }

        if (flag) {
            MatchInfo info = new MatchInfo();
            info.setType(ret);
            info.setDetail(detail);
            info.setLocation(splitx[0]);
            matchInfoList.add(info);
        }

        return ret;
    }

    public void snapshot() {
        try {
            snapShot.getReport(matchInfoList, converter);
            outputPath = snapShot.getReportName();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("------快照处理成功！------");
        }
    }

    public void output() {
        System.out.println("處理時間:" + processTime / 1000 + "s");
        System.out.println("处理文件类型：" + FilenameUtils.getExtension(filePath));
        System.out.println("输入文件地址：" + filePath);
        System.out.println("转换后文件地址：" + convertedFilePath);
        System.out.println("数据快照地址：" + outputPath);
    }

}

