/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.zhejiang;

import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.BidCustomer;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ZJCustomer extends BidCustomer {

    private HostnameVerifier hv;

    public ZJCustomer(RedisQueue queue) throws Exception {
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
    public void process(String jsonStr) {
        try {
            NewsJson json = gson.fromJson(jsonStr, NewsJson.class);
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            Connection conn = Jsoup.connect(json.getUrl());
            conn.header("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.header("Accept-Encoding", "gzip, deflate");
            conn.header("Accept-Language", "zh-CN,zh;q=0.9");
            conn.header("Connection", "keep-alive");
            conn.header("Host", "manager.zjzfcg.gov.cn");
            conn.header("Origin", "http://www.zjzfcg.gov.cn");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36");
            if (json.getUrl().contains(".pdf") || json.getUrl().contains(".doc")) {
                Save(json.getTitle(), json.getPubtime(), "", json.getUrl(), "浙江省公共资源交易网");
            } else {
                conn.method(Connection.Method.GET).timeout(10000);
                Response response = conn.execute().charset("utf-8");
                Document doc = Jsoup.parse(response.body());
                String info = doc.select("#gpoz-layout > div.gpoz-detail-content.law-detail > div.rh_container").outerHtml();
                Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "浙江省公共资源交易网");
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("zjggzy_queue");
            ZJCustomer b = new ZJCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"http://www.zjzfcg.gov.cn/laws/2018-02-22/4865.html\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
