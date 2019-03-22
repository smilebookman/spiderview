/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.guangxi;

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
public class GuangXiProduct extends DereplicateExtProduct {

    private final Gson gson = new Gson();

    public GuangXiProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        String[] urls = {
            "http://gxggzy.gxzf.gov.cn/gxzbw/zxjs/002005/MoreInfo.aspx?CategoryNum=002005"
        };
        for (String ul : urls) {
            try {
                Connection conn = Jsoup.connect(ul);
                conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Host", "gxggzy.gxzf.gov.cn");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
                conn.method(Connection.Method.GET).timeout(10000);
                Connection.Response response = conn.execute().charset("utf-8");
                String body = response.body();
                Document doc = Jsoup.parse(body);
                Elements eles = doc.select("#MoreInfoList1_DataGrid1 > tbody > tr");
                for (Element ele : eles) {
                    String url = "http://gxggzy.gxzf.gov.cn" + ele.select("a").attr("href");
                    String title = ele.select("a").attr("title");
                    String pubtime = ele.select("td:eq(2)").text();
                    NewsJson json = new NewsJson(title, pubtime, url);
                    product(gson.toJson(json));
                }
            } catch (Exception ex) {
                Logger.getLogger(GuangXiProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("guangxiggzy_queue");
        GuangXiProduct b = new GuangXiProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
