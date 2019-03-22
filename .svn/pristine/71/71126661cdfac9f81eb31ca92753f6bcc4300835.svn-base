/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.jilin;

import com.google.gson.Gson;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import static org.jsoup.Jsoup.connect;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author lh
 */
public class JiLinProduct extends DereplicateExtProduct {

    private Gson gson = new Gson();

    public JiLinProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        try {
            Connection conn = Jsoup.connect("http://www.ggzyzx.jl.gov.cn/zcfg/1.html");
            conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.header("Accept-Encoding", "gzip, deflate, br");
            conn.header("Accept-Language", "zh-CN,zh;q=0.9");
            conn.header("Host", "www.bjggzyfw.gov.cn");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            Document doc = conn.timeout(10000).get();
            Elements eles = doc.select("#jt > ul > li");
            for (Element ele : eles) {
                String title = ele.select("div > a").attr("title");
                String url = "http://www.ggzyzx.jl.gov.cn"+ele.select("div > a").attr("href");
                String pubtime = ele.select("span").text().substring(1, 11);
                NewsJson json = new NewsJson(title, pubtime, url);

                product(gson.toJson(json));
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.getStackTrace(ex));
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("jilinggzy_queue");
        JiLinProduct b = new JiLinProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
