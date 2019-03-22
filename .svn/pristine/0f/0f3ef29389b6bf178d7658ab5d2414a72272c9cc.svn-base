/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.shandong;

import com.google.gson.Gson;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.NewsJson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author cxyue
 */
public class ShanDongProduct extends DereplicateExtProduct {

    private final Gson gson = new Gson();
    private static ScriptEngineManager sem = new ScriptEngineManager();
    private static ScriptEngine scriptEngine = sem.getEngineByName("js");

    public ShanDongProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        String[] urls = {
            "http://www.sdggzyjy.gov.cn/gjfg/index.jhtml", //            "http://zw.hainan.gov.cn/ggzy/ggzy/hyj/index.jhtml"
        };
        for (String ul : urls) {
            try {
                Connection conn = Jsoup.connect(ul);
                conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Cache-Control", "max-age=0");
                conn.header("Connection", "keep-alive");
                conn.header("Host", "www.sdggzyjy.gov.cn");
                conn.header("Upgrade-Insecure-Requests", "1");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
                conn.method(Connection.Method.GET).timeout(10000);
                Connection.Response response = conn.execute().charset("utf-8");
                String body = response.body();
                Document doc = Jsoup.parse(body);
                Elements eles = doc.select("body > div.content > div.jyxxcontent > div > ul > li");
                for (Element ele : eles) {
                    String title = ele.select("div > a").attr("title");
                    if (!title.equals("")) {
                        String url = ele.select("div > a").attr("href");
                        String pubtime = ele.select("div > div").text().substring(0, 10);
                        NewsJson json = new NewsJson(title, pubtime, url);
                        product(gson.toJson(json));
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ShanDongProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String args[]) {
        try {
            scriptEngine.eval(WebJs.js);
            Invocable invokeEngine = (Invocable) scriptEngine;
            Object obj = invokeEngine.invokeFunction("getUrl", "http://www.sdggzyjy.gov.cn:80/gjfg/2322573.jhtml");
            int ii = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        RedisQueue queue = new RedisQueue("shandongggzy_queue");
//        ShanDongProduct b = new ShanDongProduct(queue);
//        Thread t = new Thread(b);
//        t.start();
    }

}
