/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.anhui;

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
public class AnHuiCustomer extends BidCustomer {

    public AnHuiCustomer(RedisQueue queue) throws Exception {
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
            conn.header("Host", "www.ahggzy.gov.cn");
            conn.header("Upgrade-Insecure-Requests", "1");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            conn.method(Connection.Method.GET).timeout(10000);
            Connection.Response response = conn.execute().charset("GBK");
            String body = response.body();
            Document doc = Jsoup.parse(body);
            String info = doc.select("body > div.w_content_main > div.content886").outerHtml();
            Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "安徽省公共资源交易监管网");
            Thread.sleep(3000);
        } catch (Exception ex) {
            Logger.getLogger(AnHuiCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("anhuiggzy_queue");
            AnHuiCustomer b = new AnHuiCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"http://www.ahggzy.gov.cn/infopublish.do?method=infoPublishView&infoid=62268b6f.15590a9a70e.-7e7d\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
