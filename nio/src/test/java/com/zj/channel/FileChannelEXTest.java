package com.zj.channel;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;

import static org.junit.Assert.*;

/**
 * Created by  zhangjian on 2017/6/6.
 */
public class FileChannelEXTest {

    private final static String FILE = "F:\\b.txt";

    private FileChannelEX fileChannelEX;

    @Before
    public void create() throws Exception {
        fileChannelEX = new FileChannelEX();
    }

    @Test
    public void readFile() throws Exception {
        fileChannelEX.readFile(FILE);
    }

    @Test
    public void yz() throws FileNotFoundException {
        File f = new File(FILE);
        if (!f.exists()) {
            throw new FileNotFoundException(FILE);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        AbstractSelector selector = null;
        try {

            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
                System.out.println(" read... ");
            }
            System.out.println("read finish");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    System.out.println("channel not null");
                    if (channel.isOpen()) {
                        System.out.println("channel is open");
                        channel.close();
                    }
                }
                if (fs != null) {
                    System.out.println("fs not null");
                    fs.close();
                }
                if (f != null) {
                    System.out.println("file not null");
                    if (f.exists()) {
                        System.out.println("file exists");
                        boolean b = f.delete();
                        System.out.println("file delete : " + b);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}