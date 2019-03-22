package com.zhongjiao.config.tools;

import com.zhongjiao.config.redis.RedisQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 生产者基础类
 *
 * @author Xuweiwei
 *
 */
public abstract class BaseProduct implements Runnable, Product {

    /**
     * 队列
     */
    protected RedisQueue queue;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param queue 队列
     */
    public BaseProduct(RedisQueue queue) {
        this.queue = queue;
    }

    /* (non-Javadoc)
	 * @see com.bing.tunnelz.product.Product#product(java.lang.String)
     */
    public void product(String url) {
        try {
            queue.add(url);
            logger.info("生产:" + url);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public String clerData(String str) {
        Pattern p = Pattern.compile("\\s");
        Matcher m = p.matcher(str);
        while (m.find()) {
            str = str.replaceFirst(m.group(), "");
        }
        return str;
    }

    public String getTime(String str) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\d{4}\\s*{1,5}[-|年|.|,|，|\\/]\\s*{1,5}(\\d{1,2})\\s*{1,5}[-|月|.|,|，|\\/]\\s*{1,5}(\\d{1,2})\\s*{1,5}([号|日]?)");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return clerData(m.group());
        }
        return "";
    }

    /* (non-Javadoc)
	 * @see java.lang.Runnable#run()
     */
    public abstract void run();

}
