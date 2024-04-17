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
     * it is start_encoding method
     */
    public ArithmeticEncoding() {
        low = 0;
        high = ArithmeticParameters.TOP_VALUE;
        bitsToFollow = 0;
    }

    public void encodeSymbol(int symbol, int[] cumFreq) throws IOException {
        long range = high - low + 1;
        high = low + (range * cumFreq[symbol - 1]) / cumFreq[0] - 1;
        low = low + (range * cumFreq[symbol]) / cumFreq[0];

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
            low *= 2;
            high = high * 2 + 1;
        }
    }

    /**
     * writ the last bits
     * @throws IOException
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
        BitOutput.outputBit(bit);
        while (bitsToFollow > 0) {
            BitOutput.outputBit(bit ^ 1);
            bitsToFollow -= 1;
        }
    }
}
