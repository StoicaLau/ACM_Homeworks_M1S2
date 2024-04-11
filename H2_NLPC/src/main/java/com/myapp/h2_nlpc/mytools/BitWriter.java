/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myapp.h2_nlpc.mytools;

import java.io.*;

/**
 *
 * @author stoic
 */
//public class BitWriter {
//
//    byte buff;
//    int count;
//    BufferedWriter bufferedWriter;
//
//    public BitWriter(File file) {
//        this.buff = 0;
//        this.count = 0;
//
//        try {
//            this.bufferedWriter = new BufferedWriter(new FileWriter(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
//    }
//
//    public void close() throws IOException {
//        this.bufferedWriter.close();
//    }
//
//    /**
//     * do not put 0 when count==8
//     *
//     * @param bit
//     */
//    public void writeBit(int bit) throws IOException {
//        buff |= (bit << this.count);
//        this.count++;
//        if (count == 8) {
//            bufferedWriter.write(buff);
//            System.out.println("buffWrite = " + buff);
//            buff = 0;
//            count = 0;
//
//        }
//    }
//
//    public void writeNBits(int bits, int nr) throws IOException {
//        for (int i = 0; i <nr; i++) {
//            int bit = (bits >> i)&1;
//            writeBit(bit);
//
//        }
//    }
//}

public class BitWriter {
    private byte buff;
    private int count;
    private BufferedOutputStream outputStream;

    public BitWriter(File file) throws IOException {
        this.count = 0;
        this.outputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    public void close() throws IOException {
        if (count > 0) {
            outputStream.write(buff);
        }
        outputStream.close();
    }

    public void writeBit(int bit) throws IOException {
        buff |= (bit << (7 - count));
        count++;
        if (count == 8) {
            outputStream.write(buff);
            buff = 0;
            count = 0;
        }
    }

    public void writeNBits(int bits, int nr) throws IOException {
        for (int i = nr - 1; i >= 0; i--) {
            int bit = (bits >> i) & 1;
            writeBit(bit);
        }
    }
}