/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.chongqing;

import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.BidCustomer;
import com.zhongjiao.config.tools.NewsJson;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Administrator
 */
public class CQCustomer extends BidCustomer {

    public CQCustomer(RedisQueue queue) throws Exception {
        super(queue);
    }

    @Override
    public void process(String jsonStr) throws Exception {
        NewsJson json = gson.fromJson(jsonStr, NewsJson.class);
        Connection conn = Jsoup.connect(json.getUrl());
        conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.header("Accept-Encoding", "gzip, deflate, br");
        conn.header("Accept-Language", "zh-CN,zh;q=0.9");
        conn.header("Host", "www.cqggzy.com");
        conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
        conn.method(Connection.Method.GET).timeout(5000).ignoreContentType(true);
        Response response = conn.execute().charset("utf-8");
        Document doc = Jsoup.parse(response.body());
        String info = doc.select("#mainContent").outerHtml();
        Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "重庆市公共资源交易网");
    }

}
