//package com.lufi.services.service;
//
//import com.lufi.core.Finder;
//import com.lufi.utils.Constants;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
//import org.apache.rocketmq.common.message.MessageExt;
//
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class ConsumerAsyncCallService {
//    private int CorePoolSize = 4;
//    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(CorePoolSize);
//    private String uploadFilePath;
//
//    public ConsumerAsyncCallService() {}
//
//    public ConsumerAsyncCallService(final String uploadFilePath){
//        this.uploadFilePath = uploadFilePath;
//    }
//
//    public void makeRemoteCallWhenTaskFinish(LongTermTaskCallBack callBack){
//        scheduler.schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("开始任务");
//                Finder finder = Finder.getInstance();
//                if(uploadFilePath != null){
//                    finder.start(uploadFilePath);
//                }
//
//                callBack.callback("结束了");
//
//            }
//        },1, TimeUnit.SECONDS);
//    }
//
//    public void checkAsyncTaskCompleted(LongTermTaskCallBack callback) {
//        scheduler.schedule(new Runnable() {
//            @Override
//            public void run() {
//                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("PushConsumer_sym");
//                consumer.setNamesrvAddr(Constants.ROCKETMQ_NAMESRV);
//
//                try {
//                    consumer.subscribe(Constants.ROCKETMQ_TOPIC,Constants.ROCKETMQ_TAG);
//                    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//                    consumer.registerMessageListener(new MessageListenerConcurrently() {
//                        @Override
//                        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
//                            String message = new String(list.get(0).getBody());
//                            if(message.contains("true")){
//                                callback.callback("长时间异步调用完成");
//                            }
//                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                        }
//                    });
//
//                    consumer.start();
//                    System.out.println("消费者启动成功");
//                } catch (Exception e) {
//                    System.out.println("消费者订阅消息失败");
//                    e.printStackTrace();
//                }
//            }
//        }, 0, TimeUnit.SECONDS);
//    }
//}
