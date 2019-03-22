/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.anhui;

import com.google.gson.Gson;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.NewsJson;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author cxyue
 */
public class AnHuiProduct extends DereplicateExtProduct {

    private final Gson gson = new Gson();

    public AnHuiProduct(RedisQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        String[] uls = {
            "http://www.ahggzy.gov.cn/dwr/call/plaincall/projectDWR.queryItemInfoByIndustryType2.dwr"
        };
        for (String ul : uls) {
            try {
                Connection conn = Jsoup.connect(ul).requestBody("callCount=1\n"
                        + "page=/generalpage.do?method=show&type=201603-036&picType=1\n"
                        + "httpSessionId=611e88339440985d544baf16fd4b\n"
                        + "scriptSessionId=D85E110FAC1D4DF7F41B9445A1185464632\n"
                        + "c0-scriptName=projectDWR\n"
                        + "c0-methodName=queryItemInfoByIndustryType2\n"
                        + "c0-id=0\n"
                        + "c0-e1=string:packTable\n"
                        + "c0-e2=string:201603-100\n"
                        + "c0-e3=number:1\n"
                        + "c0-e4=string:20\n"
                        + "c0-e5=string:true\n"
                        + "c0-e6=string:packTable\n"
                        + "c0-e7=string:13\n"
                        + "c0-param0=Object_Object:{flag:reference:c0-e1, name:reference:c0-e2, currentPage:reference:c0-e3, pageSize:reference:c0-e4, isPage:reference:c0-e5, tabId:reference:c0-e6, totalRows:reference:c0-e7}\n"
                        + "batchId=16").ignoreContentType(true);
                conn.header("Accept", "*/*");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Connection", "keep-alive");
                conn.header("Content-Type", "text/plain");
                conn.header("Host", "www.ahggzy.gov.cn");
                conn.header("Origin", "http://www.ahggzy.gov.cn");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
                conn.method(Connection.Method.POST).timeout(10000);
                Connection.Response response = conn.execute().charset("utf-8");
                String body = response.body();
                String str = unicodeToString(body);
                List<String> titles = parseTitle(str);
                List<String> urls = parseUrl(str);
                List<String> time = parseTime(str);
                for (int j = 0; j < 13; j++) {
                    String title = titles.get(j);
                    String url = "http://www.ahggzy.gov.cn/infopublish.do?method=infoPublishView&infoid=" + urls.get(j);
                    String pubtime = time.get(j);
                    NewsJson json = new NewsJson(title, pubtime, url);
                    product(gson.toJson(json));
                }
            } catch (Exception ex) {
                Logger.getLogger(AnHuiProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    //  获取标题
    public static List<String> parseTitle(String temp) {
        List<String> list = new ArrayList<>();
        Pattern pt = Pattern.compile(".titlel=\"[\\s\\S]{0,60}\"");
        Matcher mt = pt.matcher(temp);
        while (mt.find()) {
            list.add(mt.group().replace(".titlel=\"", "").replace("\"", "").replaceAll(";s\\d{0,3}[\\s\\S]+", ""));
        }
        return list;
    }

    //  获取链接id
    public static List<String> parseUrl(String temp) {
        List<String> list = new ArrayList<>();
        Pattern pt = Pattern.compile("\\[\'FILE_ID\'\\]=\"[\\s\\S]{0,50}\";s");
        Matcher mt = pt.matcher(temp);
        while (mt.find()) {
            list.add(mt.group().replace("['FILE_ID']=\"", "").replace("\";s", ""));
        }
        return list;
    }

    //  获取时间
    public static List<String> parseTime(String temp) {
        List<String> list = new ArrayList<>();
        Pattern pt = Pattern.compile(".OPERATORDT=\"\\d{4}[\\s\\S]\\d{2}[\\s\\S]\\d{2}\"");
        Matcher mt = pt.matcher(temp);
        while (mt.find()) {
            list.add(mt.group().replace(".OPERATORDT=\"", "").replace("\"", ""));
        }
        return list;
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("anhuiggzy_queue");
        AnHuiProduct b = new AnHuiProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
