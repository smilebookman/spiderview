package com.zhongjiao.config.tools;

import com.zhongjiao.config.redis.RedisQueue;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者基础类
 *
 * @author Xuweiwei
 *
 */
public abstract class BaseCustomer implements Runnable, Customer {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 队列
     */
    private RedisQueue queue;
    /**
     * 消费者退出等待时间,-1表示消费者处于守护模式
     */
    private int time;

    private long emptyQueueTime;

    private int error;
    private int LIMIT_ERROR = 1;
    private long lastMailTime;

    /**
     * @param queue 队列
     */
    public BaseCustomer(RedisQueue queue) {
        this(queue, 300);
    }

    /**
     * @param queue 队列
     * @param time 消费者退出等待时间
     */
    public BaseCustomer(RedisQueue queue, int time) {
        this.queue = queue;
        this.time = time;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.bing.tunnelz.product.Customer#process(java.lang.String)
     */
    public abstract void process(String url) throws Exception;

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            try {
                String url = queue.get();
                if (url == null) {
                    logger.info("队列" + queue.getQueueName() + "没有元素!");
                    if (time != -1) {
                        if (emptyQueueTime == 0) {
                            emptyQueueTime = System.currentTimeMillis();
                        } else {
                            long curr = System.currentTimeMillis();
                            if ((curr - emptyQueueTime) / 1000 > time) {
                                logger.info("队列" + queue.getQueueName() + "持续" + time + "秒无元素，"
                                        + ThreadUtil.currentThreadName() + "退出");

                                queue.delete();

                                return;
                            }
                        }

                    }
                    ThreadUtil.sleepSecond(10);
                } else {
                    emptyQueueTime = 0;
                    try {
                        logger.info("获取元素:" + url);
                        process(url);
                        this.error = 0;
                    } catch (Exception e) {
                        logger.error(ExceptionUtil.getStackTrace(e));
                        queue.add(url);
                        // 连续出错n次发邮件
                        error++;
                        if (error >= LIMIT_ERROR) {

                            long now = System.currentTimeMillis();
                            if ((now - lastMailTime) / 3600 / 1000 / 24 >= 1) {
                                logger.error(ExceptionUtil.buildError(getClass().getName(),
                                        new Exception("消费者连续出错" + LIMIT_ERROR + "次")));
                                error = 0;
                                lastMailTime = now;
                            } else {
                                error = 0;
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
                break;
            }
        }
    }
}
