package com.lufi.core;

import com.google.gson.Gson;
import com.lufi.matching.MatchInfo;
import com.lufi.matching.Matchers;
import com.lufi.matching.matchers.*;
import com.lufi.preproccess.Converter;
import com.lufi.preproccess.ConverterFactory;
import com.lufi.services.model.DataInfo;
import com.lufi.snapshot.SnapShot;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Finder implements Serializable {

    private static Matchers matchers = new Matchers();
    private static ArrayList<MatchInfo> matchInfoList;
    private static ArrayList<String> filePathList;
    private static String filePath = null;
    private String convertedFilePath = null;
    private String outputPath = null;
    private Converter converter = null;
    private DataInfo dataInfo;
    private DataInfo tmpDataInfo = null;
    private DefaultMQProducer producer = null;

    private static JavaSparkContext sc = null;
    private SnapShot snapShot = null;

    private static volatile Finder INSTANCE = null;

    private Finder() {
        matchInfoList = new ArrayList<>();
        filePathList = new ArrayList<>();
        snapShot = new SnapShot();
        SparkConf conf = new SparkConf().setAppName("spark").setMaster("local[*]").set("spark.driver.maxResultSize", "20g");
        sc = new JavaSparkContext(conf);
        matchers = new Matchers();
        matchers.addMatcher(IdMatcher.getInstance());
        matchers.addMatcher(PhoneMatcher.getInstance());
        matchers.addMatcher(BankMatcher.getInstance());
        matchers.addMatcher(MailMatcher.getInstance());
        matchers.addMatcher(IpMatcher.getInstance());
        matchers.addMatcher(MacMatcher.getInstance());
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

    public void start(final String id, final String timestamp) {
        try {
            //消费者启动
            producer = new DefaultMQProducer(id + timestamp);
            producer.setNamesrvAddr("10.141.211.81:9876");
            producer.start();

            tmpDataInfo = new DataInfo();
            dataInfo = new DataInfo();

            filePathList = getFilesUnderDir(id, timestamp);
            for (String path : filePathList) {
                input(path);
                convert();
                process(id, timestamp);
                snapshot(id, timestamp);
                output();

                tmpDataInfo = new DataInfo();
            }
            produceLast(id, timestamp);

            deleteFile(id, timestamp);

            producer.shutdown();
        } catch (Exception e) {
            System.out.println("关闭producer失败");
            e.printStackTrace();
        } finally {
            System.out.println("关闭producer成功");
        }
    }

    public ArrayList<String> getFilesUnderDir(final String id, final String timestamp) {
        ArrayList<String> paths = new ArrayList<>();
        try {
            String dirName = id + "_" + timestamp + "\\";
            File dir = new File(Constants.ADDRESS_TMP + dirName);
            if (dir.exists()) {
                Collection<File> filesList = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                for (File file : filesList) {
                    paths.add(Constants.ADDRESS_TMP + dirName + file.getName());
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return paths;
    }

    public void input(final String path) {
        if (path == null) {
            return;
        } else {
            filePath = path;
        }
        System.out.println("------" + filePath + "文件输入成功！------");
    }

    public boolean convert() {
        ConverterFactory factory = new ConverterFactory();
        String extension = FilenameUtils.getExtension(filePath);
        converter = factory.getConverter(extension);
        if (converter == null) {
            return false;
        }
        converter.convert(filePath);
        convertedFilePath = converter.getNewFileName();
        System.out.println("------" + filePath + "文件预处理成功！------");
        return true;
    }

    public void process(final String id, final String timestamp) {
        matchInfoList.clear();
        JavaRDD<String> dataFile = sc.textFile(convertedFilePath);
        JavaRDD<String> lines = dataFile.flatMap(line -> Arrays.asList(line.split(System.getProperty("line.separator"))).iterator());
        List<String> output = lines.collect();

        Gson gson = new Gson();
        try {
            int size = output.size();
            long preTime = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                matchx(output.get(i));
                long currentTime = System.currentTimeMillis();
                if (currentTime - preTime >= 1000 || i == size - 1) {
                    preTime = currentTime;
                    dataInfo.flag = false;
                    String msgData = gson.toJson(dataInfo);
                    System.out.println("process:" + msgData);
                    Message msg = new Message(id, timestamp, msgData.getBytes());
                    producer.sendOneway(msg);
                }
            }
        } catch (Exception e) {
            System.out.println("发送消息失败");
            e.printStackTrace();
        } finally {
            System.out.println("------隐私数据匹配成功！------");
        }
    }

    public void produceLast(final String id, final String timestamp) {
        dataInfo.flag = true;
        dataInfo.reportName = id + timestamp + Constants.SUFFIX_REPORT;
        dataInfo.paths = getDetails();

        Gson gson = new Gson();
        String msgData = gson.toJson(dataInfo);
        System.out.println("produceLast:" + msgData);
        try {
            Message msg = new Message(id, timestamp, msgData.getBytes());
            producer.sendOneway(msg);
        } catch (Exception e) {
            System.out.println("发送消息失败");
            e.printStackTrace();
        }
    }

    public String getDetails() {
        StringBuilder paths = new StringBuilder();
        for (String path : filePathList) {
            paths.append(FilenameUtils.getName(path)).append("|");
        }
        if (paths.length() > 0) {
            paths.setLength(paths.length() - 1);
        }

        return paths.toString();
    }

    private void matchx(final String x) {
        String[] splitx = x.split(",");
        int count = splitx.length;

        dataInfo.sum += count;
        tmpDataInfo.sum += count;

        StringBuilder ret = new StringBuilder();
        StringBuilder detail = new StringBuilder();
        boolean flag = false;
        Set<String> typeList = new HashSet<>();
        for (int i = 1; i < count; i++) {
            String element = splitx[i];
            String returnFlag = matchers.match(element);
            if (returnFlag != null) {
                typeList.add(returnFlag);
                flag = true;
                detail.append(splitx[i]).append(",");
                dataInfo.priv++;
                tmpDataInfo.priv++;

                switch (returnFlag) {
                    case Constants.PHONE:
                        dataInfo.phone++;
                        tmpDataInfo.phone++;
                        break;
                    case Constants.ADDRESS:
                        dataInfo.address++;
                        tmpDataInfo.address++;
                        break;
                    case Constants.ID:
                        dataInfo.id++;
                        tmpDataInfo.id++;
                        break;
                    case Constants.IP:
                        dataInfo.ip++;
                        tmpDataInfo.ip++;
                        break;
                    case Constants.MAC:
                        dataInfo.mac++;
                        tmpDataInfo.mac++;
                        break;
                    case Constants.BANK:
                        dataInfo.bank++;
                        tmpDataInfo.bank++;
                        break;
                    case Constants.MAIL:
                        dataInfo.email++;
                        tmpDataInfo.email++;
                        break;
                    default:
                        break;
                }
            }
        }

        for (String type : typeList)
            ret.append(type).append(",");

        if (flag) {
            MatchInfo info = new MatchInfo();
            info.setType(ret.toString());
            info.setDetail(detail.toString());
            info.setLocation(splitx[0]);
            matchInfoList.add(info);
        }
    }

    public void snapshot(final String id, final String timestamp) {
        try {
            snapShot.getReport(matchInfoList, converter, tmpDataInfo, id, timestamp);
            outputPath = snapShot.getReportName();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("------快照处理成功！------");
        }
    }

    public void deleteFile(final String id, final String timestamp) {
        try {
            File dir = new File(Constants.ADDRESS_TMP + id + "_" + timestamp);
            if (dir.exists()) {
                FileUtils.deleteDirectory(dir);
            }
            System.out.println("------" + id + "_" + timestamp + "目录删除成功！------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void output() {
        System.out.println("处理文件类型：" + FilenameUtils.getExtension(filePath));
        System.out.println("输入文件地址：" + filePath);
        System.out.println("转换后文件地址：" + convertedFilePath);
        System.out.println("数据快照地址：" + outputPath);
    }
}

