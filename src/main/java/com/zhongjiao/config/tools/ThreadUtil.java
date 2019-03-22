/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.config.tools;

public class ThreadUtil {

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepSecond(int second) {
        sleep(1l * second * 1000);
    }

    public static void sleepMinute(int minute) {
        sleep(1l * minute * 60 * 1000);
    }

    public static void sleepHour(int hour) {
        sleep(1l * hour * 60 * 60 * 1000);
    }

    public static void sleepDay(int day) {
        sleep(1l * day * 24 * 60 * 60 * 1000);
    }

    public static String currentThreadName() {
        return Thread.currentThread().getName();
    }
}
