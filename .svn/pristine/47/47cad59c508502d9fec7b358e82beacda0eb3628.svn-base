/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.config.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class IOUtil {

    public static String toString(Reader r) {
        BufferedReader reader = new BufferedReader(r);
        CharArrayWriter baos = new CharArrayWriter();
        char[] buf = new char[1024];
        int bytesRead = 0;
        try {
            while (bytesRead >= 0) {
                baos.write(buf, 0, bytesRead);
                bytesRead = reader.read(buf);
            }
            return new String(baos.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            close(baos);
        }
    }

    public static String toString(InputStream inputStream, String charset) {
        if (inputStream == null) {
            return "";
        }
        BufferedInputStream stream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int bytesRead = 0;
        try {
            while (bytesRead >= 0) {
                baos.write(buf, 0, bytesRead);
                bytesRead = stream.read(buf);
            }
            return new String(baos.toByteArray(), charset);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
