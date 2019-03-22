/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.hubei;

import com.google.gson.Gson;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author lh
 */
public class HuBeiProduct extends DereplicateExtProduct {

    private Gson gson = new Gson();

    public HuBeiProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        try {
            Connection conn = Jsoup.connect("http://www.hbggzy.cn/zcfg/005001/moreinfo3.html");
            conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.header("Accept-Encoding", "gzip, deflate, br");
            conn.header("Accept-Language", "zh-CN,zh;q=0.9");
            conn.header("Host", "www.bjggzyfw.gov.cn");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            conn.method(Connection.Method.GET).timeout(10000);
            Connection.Response response = conn.execute().charset("utf-8");
            Document doc = Jsoup.parse(response.body());
            Elements eles = doc.select("body > div.ewb-container > div.ewb-page-main > div.ewb-row > div.ewb-span20.r > div > div > div:nth-child(2) > ul > li");
            for (Element ele : eles) {
                String title = ele.select("a").attr("title");
                String url = "http://www.hbggzy.cn" + ele.select("a").attr("href");
                String pubtime = ele.select("span").text();
                NewsJson json = new NewsJson(title, pubtime, url);
                product(gson.toJson(json));
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.getStackTrace(ex));
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("hubeiggzy_queue");
        HuBeiProduct b = new HuBeiProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}