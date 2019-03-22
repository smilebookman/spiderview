/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.liaoning;

/**
 *
 * @author tangyu
 */
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


public class LNCustomer extends BidCustomer {

    private HostnameVerifier hv;

    public LNCustomer(RedisQueue queue) throws Exception {
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
            if (json.getUrl().contains(".pdf") || json.getUrl().contains(".doc")) {
                Save(json.getTitle(), json.getPubtime(), "", json.getUrl(), "辽宁省公共资源交易网");
            } else {
                conn.method(Connection.Method.GET).timeout(10000);
                Response response = conn.execute().charset("utf-8");
                Document doc = Jsoup.parse(response.body());
                String info = doc.select("#tblInfo > tbody > tr:nth-child(3)").outerHtml();
                Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "辽宁省公共资源交易网");
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("lnggzy_queue");
            LNCustomer b = new LNCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"https://www.bjggzyfw.gov.cn/cmsbj/zcfgqt/19990830/159456.html\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
