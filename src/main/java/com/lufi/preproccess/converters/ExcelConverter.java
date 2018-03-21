package com.lufi.preproccess.converters;

import com.lufi.preproccess.Converter;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sunny on 2018/1/4.
 */
public class ExcelConverter implements Converter {
    private String fileName;
    private String newFileName;
    private Map<String, String> map;
    private long lines;
    private long size;

    private static BufferedWriter writer = null;

    public ExcelConverter() {
    }

    @Override
    public String convert(String fileName) {
        if (fileName == null) {
            return null;
        }
        if (!FilenameUtils.getExtension(fileName).toLowerCase().contains(Constants.SUFFIX_XLS)) {
            return null;
        }
        InputStream in;
        long m = 1;
        try {
            doInit(fileName, Constants.SUFFIX_CSV);

            in = new FileInputStream(fileName);
            Workbook wb = WorkbookFactory.create(in);

            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                Row row;
                long start = m;
                for (int j = 0; j < sheet.getLastRowNum(); j++) {
                    row = sheet.getRow(j);
                    //对于需要反复增加的String,使用StringBuild能够提高效率
                    StringBuilder buffer = new StringBuilder();
                    buffer.append( m + ",");
                    for (int k = 0; k < row.getLastCellNum(); k++) {
                        // 去掉所有cell中的空值
                        if (row.getCell(k) != null && String.valueOf(row.getCell(k)).length() != 0)
                            buffer.append( regexNonePrintChar(String.valueOf(row.getCell(k))) + ",");
                    }
                    buffer.append(System.getProperty("line.separator"));
                    writer.write(buffer.toString());
                    m++;
                }
                // Excel文件工作簿名和csv文件行数的mapping
                // 例如：Sheet1-->1-212
                long end = m - 1;
                if (end < start)
                    map.put(wb.getSheetAt(i).getSheetName(), null);
                else
                    map.put(wb.getSheetAt(i).getSheetName(), start + "-" + end);
            }
            lines = m - 1;
            writer.close();
            in.close();
        } catch (InvalidFormatException | IOException ex) {
            Logger.getLogger(ExcelConverter.class.getName()).log(Level.SEVERE, null, ex);
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
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("");
    }
}
