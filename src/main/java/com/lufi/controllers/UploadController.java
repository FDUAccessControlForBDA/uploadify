package com.lufi.controllers;

import com.lufi.services.service.LogService;
import com.lufi.utils.Constants;
import com.lufi.utils.TimerUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Controller
public class UploadController {

    private LogService logService;

    @GetMapping("/")
    public String page() {
        return "index";
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload (@RequestParam(value = "files") MultipartFile [] files,
                          @RequestParam(value = "id") String id) {
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
            File storeFile = new File(dirPath+file.getOriginalFilename());
            //将文件拷贝到当前目录下
            file.transferTo(storeFile);
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
}
