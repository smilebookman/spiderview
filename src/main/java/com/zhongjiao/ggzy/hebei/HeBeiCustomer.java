/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.hebei;

import com.zhongjiao.config.redis.RedisQueue;
import com.zhongjiao.config.tools.BidCustomer;
import com.zhongjiao.config.tools.ExceptionUtil;
import com.zhongjiao.config.tools.NewsJson;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author lh
 */
public class HeBeiCustomer  extends BidCustomer {

    public HeBeiCustomer(RedisQueue queue) throws Exception {
        super(queue);
       
    }


    @Override
    public void process(String jsonStr) {
        try {
            NewsJson json = gson.fromJson(jsonStr, NewsJson.class);
            Connection conn = Jsoup.connect(json.getUrl());
            conn.method(Connection.Method.GET).timeout(10000);
            Response response = conn.execute().charset("utf-8");
            Document doc = Jsoup.parse(response.body());
            String info = doc.select("#mainContent").outerHtml();
            Save(json.getTitle(), json.getPubtime(), info, json.getUrl(), "河北省公共资源交易中心");
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    public static void main(String args[]) {
        try {
            RedisQueue queue = new RedisQueue("hebeiggzy_queue");
            HeBeiCustomer b = new HeBeiCustomer(queue);
            b.process("{\"title\":\"中华人民共和国招标投标法\",\"pubtime\":\"1999-08-30\",\"url\":\"https://www.bjggzyfw.gov.cn/cmsbj/zcfgqt/19990830/159456.html\"}");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
