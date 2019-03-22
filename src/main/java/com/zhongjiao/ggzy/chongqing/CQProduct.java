/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.chongqing;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CQProduct extends DereplicateExtProduct {

    private Gson gson = new Gson();
    private String[] urls = {"https://www.cqggzy.com/web/services/PortalsWebservice/getZcfgInfoList?response=application/json&pageIndex=1&pageSize=20&siteguid=d7878853-1c74-4913-ab15-1d72b70ff5e7&categorynum=006002&title=&infoC="};

    public CQProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        try {
            for (String url1 : urls) {
                Connection conn = Jsoup.connect(url1);
                conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                conn.header("Accept-Encoding", "gzip, deflate, br");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Host", "www.cqggzy.com");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
                conn.method(Connection.Method.GET).timeout(5000).ignoreContentType(true);
                Response response = conn.execute().charset("utf-8");
                String body = response.body();
                HashMap<String, Object> map = gson.fromJson(body, HashMap.class);
                List<LinkedTreeMap<String, Object>> list = gson.fromJson((String) map.get("return"), List.class);
                for (LinkedTreeMap<String, Object> lmap : list) {
                    String title = (String) lmap.get("title");
                    String url = "https://www.cqggzy.com" + (String) lmap.get("infourl");
                    String pubtime = (String) lmap.get("infodate");
                    NewsJson json = new NewsJson(title, pubtime, url);
                    product(gson.toJson(json));
                }
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.getStackTrace(ex));
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("cqggzy_queue");
        CQProduct b = new CQProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
