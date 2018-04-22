package com.lufi.controllers;

import com.google.gson.JsonObject;
import com.lufi.core.Finder;
import com.lufi.services.service.LogService;
import com.lufi.utils.Constants;
import com.lufi.utils.FilenameUtils;
import com.lufi.utils.TimerUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


@Controller
public class FinderController {

    @Autowired
    private LogService logService;

    private final BlockingQueue<String> queue = new LinkedBlockingDeque<>();
//
//    @GetMapping("/test")
//    public String page() {
//        return "test";
//    }

    @GetMapping("asynctask")
    @ResponseBody
    public Boolean asyncTask(@RequestParam(value = "id") String id,
                                            @RequestParam(value = "timestamp") String timestamp) {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        System.out.println("提交任务");
        //启动消费者
        check(id, timestamp);
        LongTimeAsyncCallService longTimeAsyncCallService = new LongTimeAsyncCallService(id, timestamp);
        longTimeAsyncCallService.makeRemoteCallAndUnknownWhenFinish();
        return true;
    }

    public void check(String id, String timestamp) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(id+timestamp);
        consumer.setNamesrvAddr(Constants.ROCKETMQ_NAMESRV);

        try {
            consumer.subscribe(id, timestamp);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

            MessageListener listener = new MessageListener();
            consumer.registerMessageListener(listener);

            consumer.start();
            System.out.println("消费者启动成功");
        } catch (Exception e) {
            System.out.println("消费者订阅消息失败");
            e.printStackTrace();
        }
    }

    @GetMapping("fetch")
    @ResponseBody
    public String fetch(@RequestParam(value = "id") String id,
                        @RequestParam(value = "timestamp") String timestamp) {
        String message = queue.poll();
        System.out.println("fetch:"+message);
        return message;
    }

    @PostMapping("download")
    public void downloadResource(@RequestParam(value = "fileName") String fileName, HttpServletResponse response) {
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
        }
    }

    class MessageListener implements MessageListenerConcurrently {
        private String message;

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                        ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            String message = new String(list.get(0).getBody());
            queue.add(message);
            System.out.println(message);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        private String getMessage() {
            return this.message;
        }

    }
}

interface LongTermTaskCallback {
    void callback(String result);
}

class LongTimeAsyncCallService {

    private int CorePoolSize = 4;
    private String flagFilePath = Constants.ADDRESS_FILES + "flag.txt";
    private Random random = new Random();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(CorePoolSize);
    private String id;
    private String timestamp;

    public LongTimeAsyncCallService() {
    }

    public LongTimeAsyncCallService(final String id, final String timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public void makeRemoteCallAndUnknownWhenFinish() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Finder find = Finder.getInstance();
                if (id != null && timestamp != null) {
                    find.start(id, timestamp);
                }
                long endTime = System.currentTimeMillis();
                System.out.println("处理时间:" + (endTime - startTime) / 1000 + "s");
            }
        }, 1, TimeUnit.SECONDS);
    }
}