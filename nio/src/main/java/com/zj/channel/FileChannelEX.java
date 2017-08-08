package com.zj.channel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by  zhangjian on 2017/6/6.
 */
public class FileChannelEX {

    public void readFile(String filePath) {

        RandomAccessFile randomAccessFile = null;
        FileChannel fileChannel = null;
        try {
            randomAccessFile = new RandomAccessFile(filePath, "rw");
            fileChannel = randomAccessFile.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(8);

            int bytesRead = fileChannel.read(byteBuffer);
            while (bytesRead != -1) {
                System.out.println(" read size : " + bytesRead);
            /*
            表示Buffer从写状态切换到读状态。即把limit设置成当前位置，
            即写操作写到位置；position设置为0，表示从头读，mark标记清除掉。
             */
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    System.out.println((char) byteBuffer.get());
                }
                byteBuffer.clear();
                bytesRead = fileChannel.read(byteBuffer);
            }
        } catch (FileNotFoundException e) {
            System.out.println("notfound msg : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("io msg : " + e.getMessage());
        } finally {
            try {
                if (fileChannel != null) {
                    if (fileChannel.isOpen()) {
                        fileChannel.close();
                    }
                }
                /**
                 * 其实RandomAccessFile 在进行clonse时，会一并将
                 * 其打开的管道（channel）一起close掉
                 */
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
