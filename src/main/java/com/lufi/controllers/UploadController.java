package com.lufi.controllers;

import com.lufi.services.service.LogService;
import com.lufi.utils.Constants;
import com.lufi.utils.TimerUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Controller
public class UploadController {

    private LogService logService;

    @GetMapping("/")
    public String page() {
        return "success";
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload (@RequestParam(value = "files") MultipartFile [] files,
                           @RequestParam(value = "id") String id,
                           HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        //TODO 增加"用户名+时间戳"的文件目录
        try {
            String timestamp = Long.toString(System.currentTimeMillis());//时间戳
            String prefix = id+"_"+timestamp+"\\";//用户id-时间戳
            String folderName = Constants.ADDRESS_TMP + prefix;
            File folder = new File(folderName);

            if (!folder.exists()){
                FileUtils.forceMkdir(folder);
            }

            for (MultipartFile file : files){
                System.out.println(file.getOriginalFilename());
                copyFile(file,folderName);
            }
            return timestamp;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private boolean copyFile(MultipartFile file,final String dirPath){
        try{
            File dir = new File(dirPath);
            if(!dir.exists()){
                return false;
            }
            //将文件拷贝到当前目录下
            file.transferTo(dir);
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }



//    /**
//     * @author symsimmy
//     * 检查文件存不存在
//     */
//    @PostMapping("checkFile")
//    @ResponseBody
//    public Boolean checkFile(@RequestParam(value = "md5File") String md5File) {
//        //实际项目中，这个md5File唯一值，应该保存到数据库或者缓存中，通过判断唯一值存不存在，来判断文件存不存在，这里我就不演示了
//        return false;
//    }

//    /**
//     * @author symsimmy
//     * 检查分片存不存在
//     */
//    @PostMapping("checkChunk")
//    @ResponseBody
//    public Boolean checkChunk(@RequestParam(value = "md5File") String md5File,
//                              @RequestParam(value = "chunk") Integer chunk) {
//        Boolean exist = false;
//        String path = Constants.ADDRESS_TMP + md5File + "\\";//分片存放目录
//        String chunkName = chunk + ".tmp";//分片名
//        File file = new File(path + chunkName);
//        if (file.exists()) {
//            exist = true;
//        }
//        return exist;
//    }

//    /**
//     * @author symsimmy
//     * 修改上传
//     */
//    @PostMapping("uploadbak")
//    @ResponseBody
//    public Boolean upload(@RequestParam(value = "file") MultipartFile file,
//                          @RequestParam(value = "md5File") String md5File,
//                          @RequestParam(value = "chunk", required = false) Integer chunk) { //第几片，从0开始
//        String path = Constants.ADDRESS_TMP + md5File + "\\";
//        File dirfile = new File(path);
//        if (!dirfile.exists()) {//目录不存在，创建目录
//            dirfile.mkdirs();
//        }
//        String chunkName;
//        if (chunk == null) {//表示是小文件，还没有一片
//            chunkName = "0.tmp";
//        } else {
//            chunkName = chunk + ".tmp";
//        }
//        String filePath = path + chunkName;
//        File savefile = new File(filePath);
//
//        try {
//            if (!savefile.exists()) {
//                savefile.createNewFile();//文件不存在，则创建
//            }
//            file.transferTo(savefile);//将文件保存
//
//        } catch (IOException e) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * @author symsimmy
//     * 合成分片
//     */
//    @PostMapping("merge")
//    @ResponseBody
//    public Boolean merge(@RequestParam(value = "chunks", required = false) Integer chunks,
//                         @RequestParam(value = "md5File") String md5File,
//                         @RequestParam(value = "name") String name) throws Exception {
//
//        FileOutputStream fileOutputStream = new FileOutputStream(Constants.ADDRESS_STORE + name);  //合成后的文件
//        try {
//            byte[] buf = new byte[1024];
//            for (long i = 0; i < chunks; i++) {
//                String chunkFile = i + ".tmp";
//                File file = new File(Constants.ADDRESS_TMP + md5File + "\\" + chunkFile);
//                InputStream inputStream = new FileInputStream(file);
//                int len = 0;
//                while ((len = inputStream.read(buf)) != -1) {
//                    fileOutputStream.write(buf, 0, len);
//                }
//                inputStream.close();
//            }
//
//            File directory = new File(Constants.ADDRESS_TMP + md5File);
//            if (directory.exists()) {
//                FileUtils.deleteDirectory(directory);
//            }
//
//            logService.addLog(name, md5File, Constants.FLAG_DEFAULT, TimerUtil.getCurrentTime());
//
//        } catch (Exception e) {
//            return false;
//        } finally {
//            fileOutputStream.close();
//        }
//        return true;
//    }
}
