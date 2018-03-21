package com.lufi.controllers;

import com.lufi.core.Finder;
import com.lufi.services.service.LogService;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;
import com.lufi.utils.TimerUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Controller
public class FinderController {

    @Autowired
    private LogService logService;

    @GetMapping("/test")
    public String page() {
        return "test";
    }

    @GetMapping("asynctask")
    @ResponseBody
    public DeferredResult<String> asyncTask(@RequestParam(value = "fileName") String fileName) {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        System.out.println("提交任务");
        LongTimeAsyncCallService longTimeAsyncCallService = new LongTimeAsyncCallService(fileName);
        longTimeAsyncCallService.makeRemoteCallAndUnknownWhenFinish(new LongTermTaskCallback() {
            @Override
            public void callback(Object result) {
                String ret = "提交任务完成";
                deferredResult.setResult(ret);
            }
        });

        return deferredResult;
    }

    @GetMapping("check")
    @ResponseBody
    public DeferredResult<String> check() {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        LongTimeAsyncCallService longTimeAsyncCallService = new LongTimeAsyncCallService();

        longTimeAsyncCallService.checkAsyncTaskCompleted(new LongTermTaskCallback() {
            @Override
            public void callback(Object result) {
                String ret = "检测到任务完成";
                deferredResult.setResult(ret);
            }
        });

        return deferredResult;
    }

    @RequestMapping(value = "/download")
    public void downloadResource(HttpServletRequest request, HttpServletResponse response) {
        String fileName = FilenameUtils.getBaseName(request.getParameter("fileName")) + "_report.txt";
        Path file = Paths.get(Constants.ADDRESS_STORE, fileName);
        if (Files.exists(file)) {
            response.setContentType("text/plain");
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
            try {
                response.addHeader("Content-Disposition", "attachment; filename="
                        + URLEncoder.encode(fileName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.out.println("编码不支持");
            }

            try {
                Files.copy(file, response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            logService.addLog(fileName, null, Constants.FLAG_INVALID, TimerUtil.getCurrentTime());
        }
    }
}

interface LongTermTaskCallback {
    void callback(Object result);
}

class LongTimeAsyncCallService {

    @Autowired
    private LogService logService;

    private int CorePoolSize = 4;
    private String flagFilePath = Constants.ADDRESS_FILES + "flag.txt";
    private Random random = new Random();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(CorePoolSize);
    private String uploadFilePath;

    public LongTimeAsyncCallService() {}

    public LongTimeAsyncCallService(final String uploadFilePath) {
        this.uploadFilePath = Constants.ADDRESS_STORE + uploadFilePath;
    }

    public void makeRemoteCallAndUnknownWhenFinish(LongTermTaskCallback callback) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("开始任务");
                long startTime = System.currentTimeMillis();
                Finder find = Finder.getInstance();
                if (uploadFilePath != null) {
                    System.out.println(uploadFilePath);
                    find.input(uploadFilePath);
                }
                find.convert();
                find.process();
                find.snapshot();
                find.output();
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(flagFilePath)));
                    writer.write(true + "\n");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                System.out.println("处理时间:" + (endTime - startTime) / 1000 + "s");
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void checkAsyncTaskCompleted(LongTermTaskCallback callback) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                File file = new File(flagFilePath);
                try {
                    if (file.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(flagFilePath));
                        String line = reader.readLine();
                        while (line != null) {
                            if (line.equals("true")) {
                                callback.callback("长时间异步调用完成.");

                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                        new FileOutputStream(flagFilePath)));
                                writer.write(false + "\n");
                                writer.close();
                                break;
                            }
                            line = reader.readLine();
                        }
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.SECONDS);
    }
}