/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.config.tools;

/**
 *
 * @author yorixh
 */
public class PDFTools {

    private String url;
    private static String before = "<embed width=\"100%\" height=\"100%\" name=\"plugin\" id=\"plugin\"\n"
            + "src=\"";
    private static String after = "\" type=\"application/pdf\" internalinstanceid=\"60\">";

    public PDFTools(String url) {
        this.url = url;
    }

    public String getDetail() {
        return before + url + after;
    }

}
