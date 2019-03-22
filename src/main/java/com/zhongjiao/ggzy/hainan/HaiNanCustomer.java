/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.hainan;

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
public class HaiNanCustomer extends BidCustomer {

    public HaiNanCustomer(RedisQueue queue) throws Exception {
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
            conn.header("Host", "zw.hainan.gov.cn");
            conn.header("Upgrade-Insecure-Requests", "1");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            conn.method(Connection.Method.GET).timeout(10000);
            Connection.Response response = conn.execute().charset("utf-8");
            String body = response.body();
            Document doc = Jsoup.parse(body);
            String info = doc.select("body > div.container > div > div.newsTex > div.newsCon").outerHtml();
            Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "海南省公共资源交易服务中心");
            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(HaiNanCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("hainanggzy_queue");
            HaiNanCustomer b = new HaiNanCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"http://zw.hainan.gov.cn/ggzy/ggzy/dfj/11026.jhtml\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
