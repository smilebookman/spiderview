/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.shanxi2;

import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.BidCustomer;
import com.zhongjiao.config.tools.NewsJson;
import com.zhongjiao.config.tools.PDFTools;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author cxyue
 */
public class SXCustomer extends BidCustomer {

    public SXCustomer(RedisQueue queue) throws Exception {
        super(queue);
    }

    @Override
    public void process(String jsonStr) {
        String info = "";
        try {
            NewsJson json = gson.fromJson(jsonStr, NewsJson.class);
            Connection conn = Jsoup.connect(json.getUrl());
            if (json.getUrl().contains("pdf")) {
                PDFTools p = new PDFTools(json.getUrl());
                info = p.getDetail();
            } else {
                conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Cache-Control", "max-age=0");
                conn.header("Connection", "keep-alive");
                conn.header("Host", "www.sxggzyjy.cn");
                conn.header("Upgrade-Insecure-Requests", "1");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
                conn.method(Connection.Method.GET).timeout(10000);
                Connection.Response response = conn.execute().charset("utf-8");
                String body = response.body();

                if (body.contains("/uploadfile/")) {
                    body = body.replace("/uploadfile/", "http://www.sxggzyjy.cn/uploadfile/");
                }
                Document doc = Jsoup.parse(body);
                info = doc.select("#mainContent").outerHtml();
            }

            Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "陕西省公共资源交易平台");
//            Thread.sleep(3000);
        } catch (Exception ex) {
            Logger.getLogger(SXCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("sxggzy_queue");
            SXCustomer b = new SXCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"http://www.sxggzyjy.cn/zcfg/003001/003001003/20170904/8c1501cc-9bc9-48da-8340-f875b165f623.html\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
