package com.lufi.controllers;

import com.lufi.services.service.LogService;
import com.lufi.utils.Constants;
import com.lufi.utils.TimerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;

@Controller
public class FileOperationLogControllers {

    @Autowired
    private LogService logService;
    /**
     * @author symsimmy
     * 添加文件信息到数据库
     */
    @PostMapping("addLog")
    @ResponseBody
    public Boolean addLog(@RequestParam(value = "fileName") String fileName,
                              @RequestParam(value = "md5") String md5) {
        Boolean ret = false;
        Timestamp currentTime = TimerUtil.getCurrentTime();
        int rst = logService.addLog(fileName, md5, Constants.FLAG_DEFAULT, currentTime);
        ret = rst > 0 ;
        return ret;
    }
}
