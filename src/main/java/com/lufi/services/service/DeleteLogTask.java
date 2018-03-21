package com.lufi.services.service;

import com.lufi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeleteLogTask {

    @Autowired
    private LogService logService;

    //@Scheduled(cron = "0 */1 * * * *")
    @Scheduled(cron = "0 * * * * */1")
    public void deleteLog(){
        //logService.deleteLog(Constants.FLAG_INVALID);
        int a=1;
    }
}
