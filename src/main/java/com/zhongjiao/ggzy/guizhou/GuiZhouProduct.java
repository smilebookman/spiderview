/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.guizhou;

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
public class GuiZhouProduct extends DereplicateExtProduct {

    private final Gson gson = new Gson();

    public GuiZhouProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        String[] urls = {
            "http://www.gzsggzyjyzx.cn/province/index.jhtml"
        };
        for (String ul : urls) {
            try {
                Connection conn = Jsoup.connect(ul);
                conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Cache-Control", "max-age=0");
                conn.header("Connection", "keep-alive");
                conn.header("Host", "www.gzsggzyjyzx.cn");
                conn.header("Upgrade-Insecure-Requests", "1");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
                conn.method(Connection.Method.GET).timeout(10000);
                Connection.Response response = conn.execute().charset("utf-8");
                String body = response.body();
                Document doc = Jsoup.parse(body);
                Elements eles = doc.select("body > div.article_box > div > div.article_content > div.article_listbox > ul > li");
                for (Element ele : eles) {
                    String title = ele.select("div.content_left > a").text();
                    if (!title.equals("")) {
                        String url = ele.select("div.content_left > a").attr("href");
                        String pubtime = ele.select("div.content_right > span").text().trim();
                        NewsJson json = new NewsJson(title, pubtime, url);
                        product(gson.toJson(json));
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(GuiZhouProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("guizhouggzy_queue");
        GuiZhouProduct b = new GuiZhouProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
