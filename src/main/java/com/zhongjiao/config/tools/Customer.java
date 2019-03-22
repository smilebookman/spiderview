package com.zhongjiao.config.tools;

/**
 * 消费者接口
 * @author Xuweiwei
 *
 */
public interface Customer {
	/**
	 * 抓取并处理指定的元素
	 * @param url   从队列中取出来的元素
	 * @throws Exception  异常
	 */
	public abstract void process(String url) throws Exception;
}
