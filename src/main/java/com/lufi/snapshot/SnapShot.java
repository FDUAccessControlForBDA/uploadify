package com.lufi.snapshot;

import com.lufi.matching.MatchInfo;
import com.lufi.preproccess.Converter;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Sunny on 2018/1/4.
 */
public class SnapShot implements Serializable {
    private Converter converter;
    private String type;
    private String reportName;

    public SnapShot() {
    }

    // 根据matching获取的匹配到的对象信息和预处理时源文件和检测文件的mapping关系生成报告
    // 返回report的路径
    public String getReport(ArrayList<MatchInfo> matchList, Converter con) throws IOException {
        converter = con;

        String buffer;
        String splitLine = "########################################################" + System.getProperty("line.separator");

        // 获取文件的元数据信息
        String fileName = con.getFileName();
        type = FilenameUtils.getExtension(fileName).toLowerCase();
        long fileSize = con.getSize();

        // 初始化输出文件
        reportName = FilenameUtils.getFullPath(fileName) + FilenameUtils.getBaseName(fileName) + "_report.txt";
        //File reportFile = new File(reportName);
        BufferedWriter writer = null;
        java.io.FileOutputStream writerStream = new java.io.FileOutputStream(reportName);
        //writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(writerStream, "UTF-8"));

        try {
            buffer = splitLine
                    + "#文件名：" + fileName + System.getProperty("line.separator")
                    + "#文件类型：" + type + System.getProperty("line.separator")
                    + "#文件大小：" + fileSize + "字节" + System.getProperty("line.separator")
                    + "#隐私信息所占比（行数/总行数）：" + ((float) matchList.size() * 100 / con.getLines()) + "%" + System.getProperty("line.separator")
                    + splitLine
                    + "详细信息如下" + System.getProperty("line.separator");

            writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(writerStream, "UTF-8"));
            writer.write(buffer);
            for (MatchInfo m : matchList) {
                buffer = getLocationInfo(m.getLocation()) + "检测到-->" + m.getType() + " --> " + m.getDetail() + System.getProperty("line.separator");
                writer.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return reportName;
    }

    // 根据行数到原来的mapping中获取在源文件中的位置信息
    public String getLocationInfo(String line) {
        String result = "";
        if (type.equals(Constants.SUFFIX_CSV) || type.equals(Constants.SUFFIX_TXT)) {
            result = "第" + line + "行";
        } else if (type.equals(Constants.SUFFIX_XLS) || type.equals(Constants.SUFFIX_XLSX) || type.equals(Constants.SUFFIX_PDF)) {
            Map<String, String> map = converter.getMap();
            Set<String> keys = map.keySet();
            Iterator<String> it = keys.iterator();
            long tmp = Long.parseLong(line);
            Pattern LINE = Pattern.compile("-");
            while (it.hasNext()) {
                String key = it.next();
                String rank = map.get(key); // 格式：123-293
                if (rank != null) {
                    String[] s_e = LINE.split(rank);

                    long start = Long.parseLong(s_e[0]);
                    long end = Long.parseLong(s_e[1]);
                    if (tmp >= start && tmp <= end) {
                        if (type.equals(Constants.SUFFIX_PDF))
                            result = "第" + key + "页";
                        else
                            result = "工作簿：" + key + " 第" + (tmp - start + 1) + "行";
                        break;
                    }
                }
            }
        }
        return result;
    }


    public String getReportName() {
        return reportName;
    }
}
