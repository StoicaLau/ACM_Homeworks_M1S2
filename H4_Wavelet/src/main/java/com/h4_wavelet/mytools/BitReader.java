package com.h4_wavelet.mytools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author stoic
 */
public class BitReader {
    private byte buff;
    private int count;
    private FileInputStream fis;

    public BitReader(File file) throws IOException {
        this.count = 0;
        this.fis = new FileInputStream(file);
    }

    public void close() throws IOException {
        this.fis.close();
    }

    public int readBit() throws IOException {
        if (count == 0) {
            int readByte = fis.read();
            if (readByte == -1)
                return -1;
            buff = (byte) readByte;
//            System.out.println(buff);
        }

        int bit = (buff >> count) & 1;
        count = (count + 1) % 8;
        return bit;
    }

    public int readNBits(int nr) throws IOException {
        int value = 0;
        for (int i = nr - 1; i >= 0; i--) {
            int bit = this.readBit();
            if (bit == -1) {
                return -1;
            }
            value |= ((bit) << (nr - i - 1));
        }
        return value;
    }
}
