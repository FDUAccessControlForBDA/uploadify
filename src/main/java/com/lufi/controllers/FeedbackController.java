package com.lufi.controllers;

import com.lufi.services.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Sunny on 2018/4/16.
 */
public class FeedbackController {
    @Autowired
    private LogService logService;

    /**
     * @author sunny
     * 上传反馈意见
     */
    @PostMapping("uploadFeedback")
    @ResponseBody
    public Boolean uploadFeedback(@RequestParam(value = "content") String content,
                                  @RequestParam(value = "contact") String contact,HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        if(logService.addFeedback(content, contact) == 1)
            return true;
        return false;
    }

}
