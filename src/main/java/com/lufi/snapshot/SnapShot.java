package com.lufi.snapshot;

import com.lufi.matching.MatchInfo;
import com.lufi.preproccess.Converter;
import com.lufi.services.model.DataInfo;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;
import scala.collection.immutable.Stream;

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
    public String getReport(ArrayList<MatchInfo> matchList, Converter con, DataInfo dataInfo,
                            final String id, final String timestamp) throws IOException {
        converter = con;

        StringBuilder buffer = new StringBuilder();
        String splitLine = "########################################################" + System.getProperty("line.separator");

        // 获取文件的元数据信息
        String fileName = con.getFileName();
        type = FilenameUtils.getExtension(fileName).toLowerCase();
        long fileSize = con.getSize();

        // 初始化输出文件
        reportName = Constants.ADDRESS_STORE + id + timestamp + ".txt";
        BufferedWriter writer = null;
        FileOutputStream writerStream = new FileOutputStream(reportName,true);

        try {
            buffer.append(splitLine)
                    .append("#文件名：").append(fileName).append(System.getProperty("line.separator"))
                    .append("#文件类型：").append(type).append(System.getProperty("line.separator"))
                    .append("#文件大小：").append(fileSize)
                    .append("字节").append(System.getProperty("line.separator"))
                    .append("#隐私信息所占比：").append((float) dataInfo.priv * 100 / dataInfo.sum)
                    .append("%").append(System.getProperty("line.separator")).append(splitLine)
                    .append("详细信息如下").append(System.getProperty("line.separator"));
            //TODO;

            writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            writer.write(buffer.toString());
            for (MatchInfo m : matchList) {
                //清空buffer
                buffer.setLength(0);
                buffer.append(getLocationInfo(m.getLocation()))
                        .append("检测到-->").append(m.getType())
                        .append(" --> ").append(m.getDetail()).append(System.getProperty("line.separator"));
                writer.write(buffer.toString());
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
        if (type.equalsIgnoreCase(Constants.SUFFIX_CSV) || type.equalsIgnoreCase(Constants.SUFFIX_TXT)) {
            result = "第" + line + "行";
        } else if(type.equalsIgnoreCase(Constants.SUFFIX_DOC) || type.equalsIgnoreCase(Constants.SUFFIX_DOCX)){
            result = "第" + line + "段";
        } else if (type.equalsIgnoreCase(Constants.SUFFIX_XLS) || type.equalsIgnoreCase(Constants.SUFFIX_XLSX) || type.equalsIgnoreCase(Constants.SUFFIX_PDF)) {
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
