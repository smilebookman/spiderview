/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.fujian;

import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.BidCustomer;
import com.zhongjiao.config.tools.NewsJson;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author cxyue
 */
public class FuJianCustomer extends BidCustomer {

    public FuJianCustomer(RedisQueue queue) throws Exception {
        super(queue);
    }

    @Override
    public void process(String jsonStr) {
        try {
            NewsJson json = gson.fromJson(jsonStr, NewsJson.class);
            Connection conn = Jsoup.connect(json.getUrl());
            conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.header("Accept-Encoding", "gzip, deflate");
            conn.header("Accept-Language", "zh-CN,zh;q=0.9");
            conn.header("Cache-Control", "max-age=0");
            conn.header("Connection", "keep-alive");
            conn.header("Host", "www.fjggzyjy.cn");
            conn.header("Upgrade-Insecure-Requests", "1");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            conn.method(Connection.Method.GET).timeout(10000);
            Connection.Response response = conn.execute().charset("utf-8");
            String body = response.body();
            Document doc = Jsoup.parse(body);
            String info = doc.select("body > div > div.layout-content > div > div > div > div.inside-main > div.inside-main-right > div > div > div.article-content").outerHtml();
            Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "福建省公共资源交易中心网");
            Thread.sleep(3000);
        } catch (Exception ex) {
            Logger.getLogger(FuJianCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("fujianggzy_queue");
            FuJianCustomer b = new FuJianCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"http://www.fjggzyjy.cn/news/16057/\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
