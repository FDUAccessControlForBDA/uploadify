package com.lufi.preproccess.converters;

import com.lufi.preproccess.Converter;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Symsimmy on 2018/4/16.
 */
public class DocConverter implements Converter{
    private String fileName;
    private String newFileName;
    private Map<String, String> map;
    private long lines;
    private long size;

    private static BufferedWriter writer = null;

    public DocConverter() {}

    @Override
    public String convert(String fileName){
        if(fileName == null){
            return null;
        }

        if (!FilenameUtils.getExtension(fileName).toLowerCase().contains(Constants.SUFFIX_DOC)) {
            return null;
        }

        InputStream in;
        long j=1;
        try{
            doInit(fileName,Constants.SUFFIX_CSV);
            in = new FileInputStream(fileName);
            WordExtractor extractor = new WordExtractor(in);

            String paraTexts[] = extractor.getParagraphText();
            for (int i=0; i<paraTexts.length; i++) {
                String data = paraTexts[i];
                //对于需要反复增加的String,使用StringBuild能够提高效率
                StringBuilder buffer = new StringBuilder();
                // 判断是否一整行都是空格
                if(!data.trim().isEmpty()){
                    // 将空格和中文的逗号都替换为英文的逗号
                    String ret = regexNonePrintChar(data);
                    if (ret.startsWith(",")) {
                        buffer.append(j + ret + System.getProperty("line.separator"));
                    } else {
                        buffer.append(j + "," + ret + System.getProperty("line.separator"));
                    }

                    writer.write(buffer.toString());
                    j++;
                }
            }

            lines = j-1;
            writer.close();
            in.close();

        }catch (IOException e){
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
}
