import java.io.IOException;

public class ArithmeticDecoding {
    private static long value;
    private static long low, high;

    /**
     * it is the start_decoding method
     */
    public ArithmeticDecoding() throws IOException {
        value = 0;
        for (int i = 1; i <= ArithmeticParameters.CODE_VALUE_BITS; i++) {
            value = 2 * value + BitInput.inputBit();
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
            low = 2 * low;
            high = 2 * high + 1;
            value = 2 * value + BitInput.inputBit();
        }

        return symbol;
    }

}
