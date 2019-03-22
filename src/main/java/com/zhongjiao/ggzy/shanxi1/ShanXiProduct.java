/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.shanxi1;

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
public class ShanXiProduct extends DereplicateExtProduct {

    private final Gson gson = new Gson();

    public ShanXiProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        String[] uls = {
            "http://prec.sxzwfw.gov.cn/PolicyRegulations/getNewsList.do"
        };
        for (String ul : uls) {
            try {
                Connection conn = Jsoup.connect(ul);
                conn.header("Accept", "text/html, */*; q=0.01");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Connection", "keep-alive");
                conn.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.header("Host", "prec.sxzwfw.gov.cn");
                conn.header("X-Requested-With", "XMLHttpRequest");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
                conn.data("currentPage", "1");
                conn.data("columnId", "fdaf2128-9869-4ff0-84a3-494b076c1016");
                conn.method(Connection.Method.POST).timeout(10000);
                Connection.Response response = conn.execute().charset("utf-8");
                String body = response.body();
                Document doc = Jsoup.parse(body);
                Elements eles = doc.select("body > li");
                for (Element ele : eles) {
                    String title = ele.select("a").attr("title");
                    String url = "http://prec.sxzwfw.gov.cn" + ele.select("a").attr("href");
                    String pubtime = ele.select("a > i").text().trim().replace("[ ", "").replace(" ]", "");
                    NewsJson json = new NewsJson(title, pubtime, url);
                    product(gson.toJson(json));
                }
            } catch (Exception ex) {
                Logger.getLogger(ShanXiProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("shanxiggzy_queue");
        ShanXiProduct b = new ShanXiProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}