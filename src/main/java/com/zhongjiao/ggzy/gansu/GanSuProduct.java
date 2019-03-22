/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.gansu;

import com.google.gson.Gson;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.NewsJson;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author cxyue
 */
public class GanSuProduct extends DereplicateExtProduct {

    private final Gson gson = new Gson();

    public GanSuProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        String[] urls = {
            "http://ggzyjy.gansu.gov.cn/f/gov/govList"
        };
        for (String ul : urls) {
            try {
                Connection conn = Jsoup.connect(ul);
                conn.header("Accept", "text/html, */*; q=0.01");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Connection", "keep-alive");
                conn.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.header("Host", "ggzyjy.gansu.gov.cn");
                conn.header("Origin", "http://ggzyjy.gansu.gov.cn");
                conn.header("X-Requested-With", "XMLHttpRequest");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
                conn.data("pageNo", "");
                conn.data("pageSize", "");
                conn.data("siteitemId", "193");
                conn.method(Connection.Method.POST).timeout(10000);
                Connection.Response response = conn.execute().charset("utf-8");
                String body = response.body();
                Document doc = Jsoup.parse(body);
                Elements eles = doc.select("body > div.party_RgihtConChild.clear > ul");
                for (Element ele : eles) {
                    String title = ele.select("li > div > p").text();
                    String id = ele.select("li").attr("onclick");
                    id = id.substring(id.indexOf("(") + 1, id.lastIndexOf(",'')"));
                    String url = "http://ggzyjy.gansu.gov.cn/f/gov/goverInfoDetail?selected=1&informationid=" + id;
                    String pubtime = ele.select("li > i").text().substring(0,10);
                    NewsJson json = new NewsJson(title, pubtime, url);
                    product(gson.toJson(json));
                }
            } catch (Exception ex) {
                Logger.getLogger(GanSuProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("gansuggzy_queue");
        GanSuProduct b = new GanSuProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
