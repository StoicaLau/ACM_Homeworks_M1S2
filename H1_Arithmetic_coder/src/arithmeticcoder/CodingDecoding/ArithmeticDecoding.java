package arithmeticcoder.CodingDecoding;

import arithmeticcoder.bittools.BitReader;

import java.io.IOException;

//TODO arithmetic
public class ArithmeticDecoding {
    private long value;
    private long low, high;
    //TODO faci masca pe 32 de biti

    /**
     * bit reader
     */
    private BitReader bitReader;

    /**
     * it is the start_decoding method
     */
    public ArithmeticDecoding(BitReader bitReader) throws IOException {
        this.bitReader = bitReader;
        value = 0;
        for (int i = 1; i <= ArithmeticParameters.CODE_VALUE_BITS; i++) {
            value = 2 * value + bitReader.readBit();
        }
        low = 0;
        high = ArithmeticParameters.TOP_VALUE;
    }

    /**
     * decode bit by bit
     *
     * @param cumFreq cumulative frequent
     * @return the decoded symbol
     * @throws IOException
     */
    public int decodeSymbol(int[] cumFreq) throws IOException {

        long range = high - low + 1;
        int cum = (int) ((((value - low) + 1) * cumFreq[0] - 1) / range);
        int symbol;
        for (symbol = 1; cumFreq[symbol] > cum; symbol++) ;

        high = low + ((range * cumFreq[symbol - 1]) / cumFreq[0] - 1);
        low = low + (range * cumFreq[symbol]) / (cumFreq[0]);

        while (true) {
            if (high < ArithmeticParameters.HALF) {
                //nothing
            } else if (low >= ArithmeticParameters.HALF) {
                value -= ArithmeticParameters.HALF;
                low -= ArithmeticParameters.HALF;
                high -= ArithmeticParameters.HALF;
            } else if (low >= ArithmeticParameters.FIRST_QTR && high < ArithmeticParameters.THIRD_QTR) {
                value -= ArithmeticParameters.FIRST_QTR;
                low -= ArithmeticParameters.FIRST_QTR;
                high -= ArithmeticParameters.FIRST_QTR;
            } else {
                break;
            }
            low = (low << 1) & ArithmeticParameters.MASK32B;
            high = (high << 1 | 1) & ArithmeticParameters.MASK32B;
            value = (value << 1) | bitReader.readBit();
        }

        return symbol;
    }

}
