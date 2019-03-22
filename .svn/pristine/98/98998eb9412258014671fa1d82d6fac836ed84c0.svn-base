/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.hunan;

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
public class HuNanProduct extends DereplicateExtProduct {

    private Gson gson = new Gson();

    public HuNanProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        try {
            Connection conn = Jsoup.connect("http://www.hnsggzy.com/zcfg/index.jhtml");
            conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.header("Accept-Encoding", "gzip, deflate, br");
            conn.header("Accept-Language", "zh-CN,zh;q=0.9");
            conn.header("Host", "www.bjggzyfw.gov.cn");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            conn.method(Connection.Method.GET).timeout(10000);
            Connection.Response response = conn.execute().charset("utf-8");
            Document doc = Jsoup.parse(response.body());
            Elements eles = doc.select("body > div.content-warp > div.jyxxcontent > div > ul > li");
            for (Element ele : eles) {
                String title = ele.select("div.article-list3-t > a").text();
                String url = ele.select("div.article-list3-t > a").attr("href");
                String pubtime = ele.select("div.article-list3-t > div").text();
                NewsJson json = new NewsJson(title, pubtime, url);
                product(gson.toJson(json));
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.getStackTrace(ex));
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("hunanggzy_queue");
        HuNanProduct b = new HuNanProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}