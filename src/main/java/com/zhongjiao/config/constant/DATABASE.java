/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.config.constant;

/**
 *
 * @author Administrator
 */
public enum DATABASE {

    URL("jdbc:mysql://127.0.0.1:3306/suidaobig?zeroDateTimeBehavior=convertToNull&characterEncoding=utf8&allowMultiQueries=true"),
    USERNAME("root"),
    PASSWORD("");

    private final String temp;

    private DATABASE(String temp) {
        this.temp = temp;
    }

    public String getDetail() {
        return temp;
    }

}
