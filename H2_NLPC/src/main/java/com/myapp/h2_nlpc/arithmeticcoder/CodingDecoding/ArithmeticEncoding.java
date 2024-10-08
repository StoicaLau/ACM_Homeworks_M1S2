package com.myapp.h2_nlpc.arithmeticcoder.CodingDecoding;


import com.myapp.h2_nlpc.mytools.BitWriter;

import java.io.IOException;

/**
 * Arithmetic encoding class
 */
public class ArithmeticEncoding {

    /**
     * low
     */
    private long low;

    /**
     * high
     */
    private long high;

    /**
     * bits to follow
     */
    private long bitsToFollow;

    /**
     * bit writer;
     */
    private BitWriter bitWriter;

    /**
     * it is start_encoding method
     */
    public ArithmeticEncoding(BitWriter bitWriter) {
        this.bitWriter = bitWriter;
        low = 0;
        high = ArithmeticParameters.TOP_VALUE;
        bitsToFollow = 0;
    }

    public void encodeSymbol(int symbol, int[] sums) throws IOException {
        long range = high - low + 1;
        int noOfSymbols = sums.length - 1;
       // System.out.println(symbol+":"+noOfSymbols);
        high = low + (range * sums[symbol + 1]) / sums[noOfSymbols] - 1;
        low = low + (range * sums[symbol]) / sums[noOfSymbols];

        while (true) {
            if (high < ArithmeticParameters.HALF) {
                bitsPlusFollow(0);
            } else if (low >= ArithmeticParameters.HALF) {
                bitsPlusFollow(1);
                low -= ArithmeticParameters.HALF;
                high -= ArithmeticParameters.HALF;
            } else if (low >= ArithmeticParameters.FIRST_QTR && high < ArithmeticParameters.THIRD_QTR) {
                bitsToFollow += 1;
                low -= ArithmeticParameters.FIRST_QTR;
                high -= ArithmeticParameters.FIRST_QTR;
            } else {
                break;
            }
            low = (low << 1) & ArithmeticParameters.MASK32B;
            high = ((high << 1) | 1) & ArithmeticParameters.MASK32B;
        }
    }

    /**
     * write the last bits
     *
     * @throws IOException an exception
     */
    public void doneEncoding() throws IOException {
        bitsToFollow += 1;
        if (low < ArithmeticParameters.FIRST_QTR) {
            bitsPlusFollow(0);
        } else {
            bitsPlusFollow(1);
        }
    }

    private void bitsPlusFollow(int bit) throws IOException {
        bitWriter.writeBit(bit);
        while (bitsToFollow > 0) {
            bitWriter.writeBit(bit ^ 1);
            bitsToFollow -= 1;
        }
    }
}
