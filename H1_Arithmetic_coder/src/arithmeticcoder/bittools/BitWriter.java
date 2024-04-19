/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arithmeticcoder.bittools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author stoic
 */

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
        buff |= ((bit) << count);
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