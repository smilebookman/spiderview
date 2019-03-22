package com.zhongjiao.config.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.Gson;
import com.zhongjiao.config.constant.DATABASE;
import com.zhongjiao.config.redis.RedisQueue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BidCustomer extends BaseCustomer {

    protected Gson gson = new Gson();
    protected Connection conn;
    public static final int NO_ATTACHMENT = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public BidCustomer(RedisQueue queue) throws Exception {
        super(queue);
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(DATABASE.URL.getDetail(), DATABASE.USERNAME.getDetail(), DATABASE.PASSWORD.getDetail());
        conn.setAutoCommit(false);
    }

    /**
     * @param e 元素
     */
    protected void removeScript(Element e) {
        Elements scripts = e.select("script");
        for (Element script : scripts) {
            script.remove();
        }
    }

    /**
     * @param es 元素集
     */
    protected void removeScript(Elements es) {
        Elements scripts = es.select("script");
        for (Element script : scripts) {
            script.remove();
        }
    }

    /**
     * 将时间格式标准化
     *
     * @param time
     * @return
     */
    public String changeTime(String time) {
        String[] temp = time.split("-");
        String result = "";
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].length() == 1) {
                temp[i] = "0" + temp[i];
            }
        }
        for (int i = 0; i < temp.length; i++) {
            if (i == temp.length - 1) {
                result = result + temp[i];
            } else {
                result = result + temp[i] + "-";
            }
        }
        return result;
    }

    public String clerData(String str) {
        Pattern p = Pattern.compile("\\s");
        Matcher m = p.matcher(str);
        while (m.find()) {
            str = str.replaceFirst(m.group(), "");
        }
        return str;
    }

    public String getTime(String str) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\d{4}\\s*{1,5}[-|年|.|,|，|\\/]\\s*{1,5}(\\d{1,2})\\s*{1,5}[-|月|.|,|，|\\/]\\s*{1,5}(\\d{1,2})\\s*{1,5}([号|日]?)");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return clerData(m.group());
        }
        return "";
    }

    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 持久化至数据库
     *
     * @param title 标题
     * @param pubdate 发布时间 2015-11-11
     * @param info 招标详细内容HTML片段
     * @param author 来源网站
     * @param outurl
     * @throws Exception
     */
    public void Save(String title, String pubdate, String info, String outurl, String author) throws Exception {
        PreparedStatement pstmt = null;
//        if (daysBetween(pubdate, sdf.format(new Date())) <= 3) {
        try {
            // 写入
            String sql = "insert into stang_zhongjiao_plan(title,pubtime,info,add_time,author,outurl)values(?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, pubdate);
            pstmt.setString(3, info);
            pstmt.setLong(4, System.currentTimeMillis() / 1000);
            pstmt.setString(5, author);
            pstmt.setString(6, outurl);
            int id = 0;
            synchronized (this) {
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            logger.error("持久化错误,错误信息:" + ExceptionUtil.getStackTrace(e));
            conn.rollback();
            throw e;
        } finally {
            IOUtil.close(pstmt);
//            }
        }
    }

}
