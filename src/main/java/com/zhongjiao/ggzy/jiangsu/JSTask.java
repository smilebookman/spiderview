/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.jiangsu;

/**
 *
 * @author tangyu
 */
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.ExceptionUtil;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JSTask {

    private static final Logger logger = LoggerFactory.getLogger(JSTask.class);

    public static void task() {
        logger.info("江苏省公共资源交易网数据爬取任务开始");
        RedisQueue queue = new RedisQueue("jsggzy_queue");
        ExecutorService pexecutorService = Executors.newFixedThreadPool(1); // 生产者线程池
        pexecutorService.execute(new JSProduct(queue));
        pexecutorService.shutdown();
        int customer = 2;
        ExecutorService cexecutorService = Executors.newFixedThreadPool(customer); // 消费者线程池
        for (int i = 1; i < customer; i++) {
            try {
                Thread t = new Thread(new JSCustomer(queue));
                t.setName("JSCustomer_" + String.valueOf(i + 1));
                cexecutorService.execute(t);
            } catch (Exception e) {
                logger.error("JSCustomer_" + String.valueOf(i + 1) + "线程启动错误，错误信息:" + ExceptionUtil.getStackTrace(e));
            }
        }
        cexecutorService.shutdown();
    }

    public static void main(String args[]) {
        task();
    }
}
