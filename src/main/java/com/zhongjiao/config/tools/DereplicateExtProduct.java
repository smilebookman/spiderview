package com.zhongjiao.config.tools;

import com.zhongjiao.config.constant.FILEPATH;
import com.zhongjiao.config.redis.RedisQueue;

public abstract class DereplicateExtProduct extends BaseProduct {

    protected SimpleBloomFilter filter;
    protected int count = 1;

    public DereplicateExtProduct(RedisQueue queue) {
        super(queue);
        String className = this.getClass().getCanonicalName();
        className = className.substring(className.lastIndexOf(".") + 1);
        String Info = System.getProperties().toString();
        if (Info.contains("Windows")) {
            filter = new SimpleBloomFilter("C:\\" + FILEPATH.SIMPLEBLOOMFILTER.getDetail(), className + ".txt");
        } else {
            if (Info.contains("suitang01")) {
                filter = new SimpleBloomFilter("/home/suitang01/" + FILEPATH.SIMPLEBLOOMFILTER.getDetail(), className + ".txt");
            } else {
                filter = new SimpleBloomFilter("/home/hadoop/" + FILEPATH.SIMPLEBLOOMFILTER.getDetail(), className + ".txt");
            }
        }
        filter.load(filter);
    }

    /**
     * @param queue 队列
     * @param set 集合
     */
    @Override
    public void product(String url) {
        if (!check(url)) {
            super.product(url);
        } else {
            logger.info("数据已抓取,不需要重复抓取." + url);
        }
    }

    public void productByKey(String key, String url) {
        if (!check(url)) {
            count = 0;
            super.product(url);
        } else {
            logger.info("数据已抓取,不需要重复抓取." + url);
        }
    }

    public boolean check(String url) {
        synchronized (this) {
            Boolean result = false;
            if (!filter.contains(url)) {
                filter.add(url);
                count = 0;
            } else {
                result = true;
                count++;
                if (count == 1000) {
                    System.out.println("连续1000条重复，退出！");
//                    exit();
                    throw new Error("连续" + count + "次出现重复数据,生产者退出");
                }
            }
            return result;
        }
    }

}
