package com.lufi.preproccess.converters;

import com.lufi.preproccess.Converter;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sunny on 2018/1/4.
 */
public class TxtConverter implements Converter, Serializable {

    private String fileName;
    private String newFileName;
    private Map<String, String> map;
    private long lines;
    private long size;

    private static BufferedWriter writer = null;


    public TxtConverter() {
    }

    @Override
    public String convert(String fileName) {
        if (fileName == null)
            return null;
        if (fileName.length() <= 0 || !FilenameUtils.getExtension(fileName).toLowerCase().equals(Constants.SUFFIX_TXT)) {
            return null;
        }

        long i = 1;
        BufferedReader reader;
        try {
            doInit(fileName, Constants.SUFFIX_CSV);
            reader = new BufferedReader(new FileReader(fileName));
            String data;
            while ((data = reader.readLine()) != null) {
                //对于需要反复增加的String,使用StringBuild能够提高效率
                StringBuilder buffer = new StringBuilder();
                if (!data.trim().isEmpty()) { // 判断是否一整行都是空格
                    // 将空格和中文的逗号都替换为英文的逗号
                    buffer.append(i + "," + regexNonePrintChar(data) + System.getProperty("line.separator"));
                } else {
                    buffer.append(i + "," + System.getProperty("line.separator"));
                }
                // txt文件中的一行对应csv文件中的一行，因此不需要存储额外的匹配信息。
                // 1-1, 2-2, 3-3, ...
                // 当一行全部为空格时，以“数字+逗号”的形式表示这一行
                // 例如：第142行全是空格，则在转换后csv中表示为“142，”
                writer.write(buffer.toString());
                i++;
            }
            lines = i - 1;
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getNewFileName() {
        return newFileName;
    }

    @Override
    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public void doInit(String fileName, String suffix) {
        this.fileName = fileName;
        this.newFileName = FilenameUtils.getFullPath(fileName) + FilenameUtils.getBaseName(fileName) + "_new." + suffix;
        this.map = new HashMap<>();
        File file = new File(fileName);
        this.size = file.length();
        File targetFile = new File(newFileName);
        writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(targetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getLines() {
        return lines;
    }

    @Override
    public long getSize() {
        return size;
    }

    private static String regexNonePrintChar(String content) {
        Pattern pattern = Pattern.compile("\\s+|[，]");
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll(",");
    }
}
