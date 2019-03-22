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
public enum FILEPATH {

    REDIS("zhongjiaoplan_queue"),
    SIMPLEBLOOMFILTER("zhongjiaoplan_spider");

    private final String temp;

    private FILEPATH(String temp) {
        this.temp = temp;
    }

    public String getDetail() {
        return temp;
    }

}
