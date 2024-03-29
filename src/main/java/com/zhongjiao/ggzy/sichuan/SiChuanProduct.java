/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.sichuan;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lh
 */
public class SiChuanProduct extends DereplicateExtProduct {

    private Gson gson = new Gson();

    public SiChuanProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        try {
            String[] urls = {"http://ggzyjy.sc.gov.cn/News/GetPoliciesList?page=1&type=PurchaseRules", "http://ggzyjy.sc.gov.cn/News/GetPoliciesList?page=1&type=projectrules"};
            for (String url1 : urls) {
                Connection conn = Jsoup.connect(url1).ignoreContentType(true);
                conn.header("Accept", "application/json, text/javascript, */*; q=0.01");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Host", "ggzyjy.sc.gov.cn");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
                Document doc = conn.timeout(10000).get();
                String info = doc.select("body").outerHtml().replace("<body>\n", "").replace("</body>", "");
                HashMap<String, Object> map = gson.fromJson(info, HashMap.class);
                info = map.get("data").toString().replace("\"", "");
                List<String> title = parseTitle(info);
                List<String> url = parseUrl(info);
                String pubtime = "";
                for (int i = 0; i < title.size(); i++) {
                    String ptitle = title.get(i);
                    String purl = "http://ggzyjy.sc.gov.cn" + url.get(i);
                    NewsJson json = new NewsJson(ptitle,pubtime, purl);
                    product(gson.toJson(json));
                }
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.getStackTrace(ex));
        }
    }

    public static List<String> parseUrl(String temp) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("Link:[\\s\\S]{1,100},ArticleId");
        Matcher m = pattern.matcher(temp);
        while (m.find()) {
            list.add(m.group().replace("Link:", "").replace(",ArticleId", ""));
        }
        return list;
    }

    public static List<String> parseTitle(String temp) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("ArticleTitle:[\\s\\S]{1,100},CreateDate");
        Matcher m = pattern.matcher(temp);
        while (m.find()) {
            list.add(m.group().replace("ArticleTitle:", "").replace(",CreateDate", ""));
        }
        return list;
    }

    private void parse(HashMap map) {
        List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) map.get("data");
        int i = 1;
        for (LinkedTreeMap<String, Object> tmap : list) {
            String title = (String) tmap.get("ArticleTitle");
            if (!"".equals(title)) {
                double a = (Double) tmap.get("IsInner");
                String url = "http://ggzyjy.sc.gov.cn" + (String) tmap.get("Link");
                String publish = (String) tmap.get("CreateDate");
                NewsJson json = new NewsJson(url, title, publish);
                product(gson.toJson(json));
            }

        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("sichuanggzy_queue");
        SiChuanProduct b = new SiChuanProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
