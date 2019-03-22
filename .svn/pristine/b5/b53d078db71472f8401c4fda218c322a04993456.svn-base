/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.config.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pr = new PrintWriter(sw);
        e.printStackTrace(pr);
        return sw.toString();
    }

    public static String buildError(String className, Exception e) {
        StringBuilder result = new StringBuilder();
        result.append("<h1>线程名:").append(ThreadUtil.currentThreadName()).append("</h1>");
        result.append("<h1>类名:").append(className).append("</h1>");
        if (e != null) {
            result.append(getStackTrace(e));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        try {
            throw new Exception();
        } catch (Exception e) {
            System.out.println(getStackTrace(e));
            System.out.println("\n");
            e.printStackTrace();
        }
    }
}
