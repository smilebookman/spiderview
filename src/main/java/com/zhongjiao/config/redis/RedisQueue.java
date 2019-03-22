/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.config.redis;

import com.zhongjiao.config.constant.FILEPATH;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.FileChannelImpl;

/**
 *
 * @author Administrator
 */
public class RedisQueue {

    public File file;              //队列文件
    public File sizeFile;          //计数文件
    public FileChannel fileChannel;           //通道
    public FileChannel sizeFileChannel;       //通道
    private String fileContent;            //文件内容
    private static final Logger logger = LoggerFactory.getLogger(RedisQueue.class);       //日志打印
    public MappedByteBuffer mappedByteBuffer;        //此对象用于在内存中映射文件
    public MappedByteBuffer sizeMappedByteBuffer;
    private static final String CODE = "utf-8";       //设置文件编码
    private static final int BUFFER_SIZE = 1024;      //设置读取文件时的缓冲区大小

    public RedisQueue(String queueName) {
        try {
            logger.info("正在获取队列通道......");
            String Info = System.getProperties().toString();
            if (Info.contains("Windows")) {
                this.file = new File("C:\\" + FILEPATH.REDIS.getDetail(), queueName + ".txt");
                this.sizeFile = new File("C:\\" + FILEPATH.REDIS.getDetail(), queueName + "_nioSize.txt");
            } else if (Info.contains("suitang01")) {
                this.file = new File("/home/suitang01/" + FILEPATH.REDIS.getDetail(), queueName + ".txt");
                this.sizeFile = new File("/home/suitang01/" + FILEPATH.REDIS.getDetail(), queueName + "_nioSize.txt");
            } else {
                this.file = new File("/home/hadoop/" + FILEPATH.REDIS.getDetail(), queueName + ".txt");
                this.sizeFile = new File("/home/hadoop/" + FILEPATH.REDIS.getDetail(), queueName + "_nioSize.txt");
            }

            //打开对应的队列文件,如果不存在则新建文件
            Path path = this.file.toPath();
            Path sizePath = this.sizeFile.toPath();
            try {
                //直接创建并打开文件
                this.fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
                this.sizeFileChannel = sizeFileChannel.open(sizePath, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
            } catch (Exception e) {
                //如果创建失败,则先创建上级目录,再创建和打开文件
                Files.createDirectory(path.getParent());
                this.fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
                this.sizeFileChannel = sizeFileChannel.open(sizePath, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
            }
            logger.info("队列通道获取完毕......");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        //在程序运行结束时,将文本内容由内存映射到文件中,并释放通道
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    logger.info("队列通道已关闭......");
                    sizeFileChannel.close();
                    fileChannel.close();
                } catch (Exception ex) {
                    logger.error(ex.toString());
                }
            }
        });
    }

    public void delete() {
        file.delete();
        sizeFile.delete();
    }

    /**
     * 这个构造函数是为了兼容老版本的代码,实质上host,port,password这些参数已经没有意义了
     *
     * @param host
     * @param port
     * @param password
     * @param queueName
     */
    public RedisQueue(String host, int port, String password, String queueName) {
        this(queueName);
    }

    /**
     * 释放MappedByteBuffer
     *
     * @param mappedByteBuffer
     */
    public void unmap(MappedByteBuffer mappedByteBuffer) {
        try {
            Method m = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
            m.setAccessible(true);
            m.invoke(FileChannelImpl.class, mappedByteBuffer);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    /**
     * 推入操作
     */
    public void push(Object object) {
        try {
            fileChannel.position(file.length());
            //直接从文件末尾推入即可
            if (fileChannel.position() == 0) {
                fileContent = (String) object;
            } else {
                fileContent = "[fileCursor]\n" + (String) object;
            }
            ByteBuffer buff = ByteBuffer.wrap(fileContent.getBytes(CODE));
            while (buff.hasRemaining()) {
                fileChannel.write(buff);
            }
            fileChannel.force(true);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    /**
     * 推出操作
     *
     * @return
     */
    public String pop() {
        String result = "";
        try {
            //将文件中的内容映射到内存中
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, this.file.length());
            this.sizeMappedByteBuffer = this.sizeFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, this.sizeFile.length());

            //在这里计算当前次扫描的出发点在哪里
            int sizeFileLength = (int) sizeFile.length();
            byte[] dst = new byte[sizeFileLength];
            int count = 0;
            if (dst.length != 0) {
                for (int offset = 0; offset < sizeFileLength; offset++) {
                    this.sizeMappedByteBuffer.position(0);
                    byte b = this.sizeMappedByteBuffer.get(offset);
                    dst[offset] = b;
                }
                count = Integer.valueOf(new String(dst, CODE));
            }
            //这里处理队列消费完毕后的情况,此时count应该是比file.length要大13,这个13是自定义游标转成的字节数组的长度
            //此时取元素返回为空
            if (count > (int) file.length() || file.length() == 0) {
                return null;
            }
            //这里只对文件从未处理过的字节开始往后的1k进行读写操作
            byte[] ds;
            if (count + BUFFER_SIZE >= (int) file.length()) {
                ds = new byte[(int) file.length() - count];
                for (int offset = 0; offset < (int) file.length() - count; offset++) {
                    byte b = this.mappedByteBuffer.get(offset + count);
                    ds[offset] = b;
                }
            } else {
                ds = new byte[BUFFER_SIZE];
                for (int offset = 0; offset < BUFFER_SIZE; offset++) {
                    byte b = this.mappedByteBuffer.get(offset + count);
                    ds[offset] = b;
                }
            }
            fileContent = new String(ds, CODE);
            String[] tempStr = fileContent.split("\\[fileCursor\\]\n");
            result = tempStr[0];       //取出第一个完整的元素
//            logger.info("取出元素： " + result);
            int size = result.getBytes(CODE).length + "[fileCursor]\n".getBytes().length;   //读取出来的第一个元素在字节数组中的长度

            sizeFileChannel.position(0);
            ByteBuffer buff = ByteBuffer.wrap(String.valueOf(size + count).getBytes(CODE));
            while (buff.hasRemaining()) {
                sizeFileChannel.write(buff);     //将下标写入计数文件中
            }
            sizeFileChannel.force(true);
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            unmap(mappedByteBuffer);
            unmap(sizeMappedByteBuffer);
        }
        return result;
    }

    /**
     * 把一个元素从队列底部加入队列
     *
     * @param url 字符串
     */
    public void add(String url) {
        synchronized (this) {
            push(url);
        }
    }

    /**
     * 在队列首部取出一个元素
     *
     * @return 字符串
     */
    public String get() {
        synchronized (this) {
            return (String) pop();
        }
    }

    /**
     * 得到队列文件的名称
     *
     * @return
     */
    public String getQueueName() {
        return file.getName();
    }

    public static void main(String args[]) {
        RedisQueue queue = new RedisQueue("test_queue");
//        while (true) {
//            queue.pop();
//        }
//        for (int i = 0; i <= 2; i++) {
//            logger.info(String.valueOf(i));
//            queue.push("{\"url\":\"http://www.jxsggzy.cn/web/jyxx/002006/002006004/20171127/5ee7452e-1a2a-44d7-984e-e6e8da2f4a59.html\","
//                    + "\"title\":\"[开发区]赣州市九鼎招标代理有限公司关于江西省赣州经济技术开发区公用事业服务管理处、赣州经济技术开发区凤岗镇人民政府、"
//                    + "赣州经济技术开发区农业农村工作办公室、赣州经济技术开发区招商局、赣州经济技术开发区黄金岭街道办事处、赣州经济技术开发区经济发展局、"
//                    + "赣州经济技术开发区党政办公室、赣州经济技术开发区工商行政管理局、赣州经济技术开发区湖边镇人民政府电脑、打印机、健身器材、监控等办公设备项目"
//                    + "（项目编号：GZJD2017-KF-X036）询价的成交结果公告\",\"publish\":\"2017-11-27\"}");
//        }
    }
}
