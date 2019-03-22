/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.jilin;

import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.BidCustomer;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author lh
 */
public class JiLinCustomer extends BidCustomer {

    public JiLinCustomer(RedisQueue queue) throws Exception {
        super(queue);
       
    }


    @Override
    public void process(String jsonStr) {
        try {
            NewsJson json = gson.fromJson(jsonStr, NewsJson.class);
            Connection conn = Jsoup.connect(json.getUrl());
            conn.method(Connection.Method.GET).timeout(10000);
            Connection.Response response = conn.execute().charset("utf-8");
            Document doc = Jsoup.parse(response.body());
            String info = doc.select("div.ewb-article-info").outerHtml();
            Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "吉林省公共资源交易中心网");
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("jilinggzy_queue");
            JiLinCustomer b = new JiLinCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"https://www.bjggzyfw.gov.cn/cmsbj/zcfgqt/19990830/159456.html\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
