/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.zhejiang;

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
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ZJProduct extends DereplicateExtProduct {

    private HostnameVerifier hv;
    private Gson gson = new Gson();
    private String[] urls = {"http://manager.zjzfcg.gov.cn/cms/api/cors/getRemoteResults"};

    public ZJProduct(RedisQueue queue) {
        super(queue);
        this.hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                        + session.getPeerHost());
                return true;
            }
        };
    }

    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

    /* 
     * 科学计数法转换为字符串
     */
    public static String Publish(Double pubtime1) {
        String publish = "";
        BigDecimal bd = new BigDecimal(pubtime1);
        publish = bd.toPlainString();
        return publish;
    }

    /* 
     * 将时间戳转换为时间
     */
    public static String stampToDate(String pubtime2) {
        String pubtime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = new Long(pubtime2);
        Date date = new Date(lt);
        pubtime = simpleDateFormat.format(date);
        return pubtime;
    }

    @Override
    public void run() {
        try {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            for (String url1 : urls) {
                Connection conn = Jsoup.connect(url1);
                conn.header("Accept", "application/json, text/javascript, */*; q=0.01");
                conn.header("Accept-Encoding", "gzip, deflate");
                conn.header("Accept-Language", "zh-CN,zh;q=0.9");
                conn.header("Connection", "keep-alive");
                conn.header("Host", "manager.zjzfcg.gov.cn");
                conn.header("Origin", "http://www.zjzfcg.gov.cn");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36");
                conn.data("pageSize", "15");
                conn.data("pageNo", "2");
                conn.data("url", "http://manager.zjzfcg.gov.cn/cms/api/common/getCmsLawsList");
                conn.data("categoryId", "38,39,40");
                Document doc = conn.timeout(30000).post();
                String content = String.valueOf(doc).replace(" </body>\n" + "</html>", "").replace("<html>\n"
                        + " <head></head>\n"
                        + " <body>\n"
                        + " ", "");
                HashMap map1 = gson.fromJson(content, HashMap.class);
                ArrayList array_list = (ArrayList) map1.get("articles");
                for (Object obj : array_list) {
                    LinkedTreeMap map3 = (LinkedTreeMap) obj;
                    String url = "http:" + (String) map3.get("url");
                    String title = (String) map3.get("title");
                    Double pubtime1 = (Double) map3.get("publishDate");
                    String pubtime2 = Publish(pubtime1);
                    String pubtime = stampToDate(pubtime2);
                    NewsJson json = new NewsJson(title, pubtime, url);
                    product(gson.toJson(json));
                }
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.getStackTrace(ex));
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("zjggzy_queue");
        ZJProduct b = new ZJProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
