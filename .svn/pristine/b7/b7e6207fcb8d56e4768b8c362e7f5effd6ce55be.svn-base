/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.jiangsu;

/**
 *
 * @author tangyu
 */
import com.google.gson.Gson;
import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.DereplicateExtProduct;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSProduct extends DereplicateExtProduct {

    private HostnameVerifier hv;
    private Gson gson = new Gson();
    private String[] urls = {"http://jsggzy.jszwfw.gov.cn/zcfg/008001/lawInfo2.html", "http://jsggzy.jszwfw.gov.cn/zcfg/008002/lawInfo2.html",
        "http://jsggzy.jszwfw.gov.cn/zcfg/008003/lawInfo2.html", "http://jsggzy.jszwfw.gov.cn/zcfg/008004/lawInfo2.html"};

    public JSProduct(RedisQueue queue) {
        super(queue);
        this.hv = new HostnameVerifier() {
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

    @Override
    public void run() {
        try {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            for (String url1 : urls) {
                Connection conn = Jsoup.connect(url1);
                conn.method(Connection.Method.GET).timeout(10000);
                Response response = conn.execute().charset("utf-8");
                Document doc = Jsoup.parse(response.body());
                Elements eles = doc.select("#main > table > tbody > tr");
                for (Element ele : eles) {
                    String url = "http://jsggzy.jszwfw.gov.cn" + ele.select("td:nth-child(2) > a").attr("href");
                    String title = ele.select("td:nth-child(2) > a").attr("title");
                    String pubtime = ele.select("td:nth-child(3)").text().trim();
                    NewsJson json = new NewsJson(title, pubtime, url);
                    product(gson.toJson(json));
                }
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.getStackTrace(ex));
        }
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("jsggzy_queue");
        JSProduct b = new JSProduct(queue);
        Thread t = new Thread(b);
        t.start();
    }

}
