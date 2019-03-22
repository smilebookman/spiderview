/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.config.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleBloomFilter {

    private String path;
    private String filename;
    private static final int DEFAULT_SIZE = 2 << 24;
    private static final int[] seeds = new int[]{7, 11, 13, 31, 37, 61};

    private BitSet bits = new BitSet(DEFAULT_SIZE);
    private SimpleHash[] func = new SimpleHash[seeds.length];

    public static void main(String[] args) {
        String value = "sdf";
        String[] words = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random r = new Random();
        SimpleBloomFilter filter = new SimpleBloomFilter("d://a.txt", "");
        System.out.println(filter.contains(value));
        long a = System.currentTimeMillis();
        filter.load(filter);
        long b = System.currentTimeMillis();
        System.out.println(b - a);
    }

    public SimpleBloomFilter(String path, String filename) {
        this.path = path;
        this.filename = filename;
        for (int i = 0; i < seeds.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }
    }

    public void add(String value) {
        for (SimpleHash f : func) {
            bits.set(f.hash(value), true);
            serialize(String.valueOf(f.hash(value)));
        }
    }

    public boolean contains(String value) {
        if (value == null) {
            return false;
        }
        boolean ret = true;
        
        for (SimpleHash f : func) {
            ret = ret && bits.get(f.hash(value));
        }
        return ret;
    }

    public void load(SimpleBloomFilter filter) {
        BufferedReader br = null;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(path + File.separator + filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line;
            Boolean flag = true;
            List list = new ArrayList();
            while ((line = br.readLine()) != null) {
                //限制读取到内存中文件的大小
                if (file.length() > 1048576*5 && flag) {
                    for (int i = 0; i < (file.length() / 58 - 18078) * 6; i++) {
                        br.readLine();
                    }
                    flag = false;
                }
                list.add(line);
                bits.set(Integer.valueOf(line), true);
            }
            System.out.println("反序列化完成！");
            if (file.length() > 1048576*5) {
                System.out.println("文件" + filename + " 已达到1MB，最早部分已被删除！");
                file.delete();
                for (int i = 0; i < list.size(); i++) {
                    serialize((String) list.get(i));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimpleBloomFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimpleBloomFilter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(SimpleBloomFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void serialize(String value) {
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            fos = new FileOutputStream(new File(path + File.separator + filename), true);
            OutputStreamWriter osr = new OutputStreamWriter(fos);
            pw = new PrintWriter(osr);
            pw.println(value);
        } catch (IOException ex) {
            Logger.getLogger(SimpleBloomFilter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }

    }

    public static class SimpleHash {

        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        public int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
            return (cap - 1) & result;
        }

    }

}
