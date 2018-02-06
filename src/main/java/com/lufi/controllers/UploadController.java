package com.lufi.controllers;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


@Controller
public class UploadController {
    private String relativelyPath = "C:\\Projects\\IdeaProjects\\uploadify\\src\\main\\";

    @GetMapping("/")
    public String page() {
        return "index";
    }

    /**
     * @author symsimmy
     * 检查文件存不存在
     */
    @PostMapping("checkFile")
    @ResponseBody
    public Boolean checkFile(@RequestParam(value = "md5File") String md5File) {

        //实际项目中，这个md5File唯一值，应该保存到数据库或者缓存中，通过判断唯一值存不存在，来判断文件存不存在，这里我就不演示了
        String md5FilePath = relativelyPath + "tmp\\md5.txt";//后面改为files
        File file = new File(md5FilePath);
        try {
            if(!file.exists()){
                return false;
            }else{
                BufferedReader reader = new BufferedReader(new FileReader(md5FilePath));
                String line = reader.readLine();
                while (line!=null){
                    if (line.equals(md5File)){
                        return true;
                    }
                    line = reader.readLine();
                }
                reader.close();
            }
        }catch (IOException e){
            return false;
        }
            return false;

    }

    /**
     * @author symsimmy
     * 检查分片存不存在
     */
    @PostMapping("checkChunk")
    @ResponseBody
    public Boolean checkChunk(@RequestParam(value = "md5File") String md5File,
                              @RequestParam(value = "chunk") Integer chunk) {
        Boolean exist = false;
        String path = relativelyPath + "tmp\\" + md5File + "\\";//分片存放目录
        String chunkName = chunk + ".tmp";//分片名
        File file = new File(path + chunkName);
        if (file.exists()) {
            exist = true;
        }
        return exist;
    }

    /**
     * @author symsimmy
     * 修改上传
     */
    @PostMapping("upload")
    @ResponseBody
    public Boolean upload(@RequestParam(value = "file") MultipartFile file,
                          @RequestParam(value = "md5File") String md5File,
                          @RequestParam(value = "chunk", required = false) Integer chunk) { //第几片，从0开始
        String path = relativelyPath + "tmp\\" + md5File + "\\";
        File dirfile = new File(path);
        if (!dirfile.exists()) {//目录不存在，创建目录
            dirfile.mkdirs();
        }
        String chunkName;
        if (chunk == null) {//表示是小文件，还没有一片
            chunkName = "0.tmp";
        } else {
            chunkName = chunk + ".tmp";
        }
        String filePath = path + chunkName;
        File savefile = new File(filePath);

        try {
            if (!savefile.exists()) {
                savefile.createNewFile();//文件不存在，则创建
            }
            file.transferTo(savefile);//将文件保存

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * @author symsimmy
     * 合成分片
     */
    @PostMapping("merge")
    @ResponseBody
    public Boolean merge(@RequestParam(value = "chunks", required = false) Integer chunks,
                         @RequestParam(value = "md5File") String md5File,
                         @RequestParam(value = "name") String name) throws Exception {
        String path = relativelyPath + "tmp\\";//后面改为files
        FileOutputStream fileOutputStream = new FileOutputStream(path  + name);  //合成后的文件
        try {
            byte[] buf = new byte[1024];
            for (long i = 0; i < chunks; i++) {
                String chunkFile = i + ".tmp";
                File file = new File(relativelyPath + "tmp\\" + md5File + "\\" + chunkFile);
                InputStream inputStream = new FileInputStream(file);
                int len = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                inputStream.close();
            }
            //删除md5目录，及临时文件
            String md5FilePath = relativelyPath + "tmp\\md5.txt";//后面改为files
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(md5FilePath, true)));
            writer.write(md5File+"\n");
            writer.close();

            File directory = new File(relativelyPath + "tmp\\" + md5File);
            if (directory.exists()) {
                FileUtils.deleteDirectory(directory);
            }

        } catch (Exception e) {
            return false;
        } finally {
            fileOutputStream.close();
        }
        return true;
    }



}
