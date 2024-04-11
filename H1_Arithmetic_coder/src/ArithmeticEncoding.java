import java.io.IOException;

public class ArithmeticEncoding {

    private static long low, high;
    private static long bitsToFollow;

    /**
     * it is start_encoding method
     */
    public static void startEncoding() {
        low = 0;
        high = ArithmeticParameters.TOP_VALUE;
        bitsToFollow = 0;
    }

    public static void encodeSymbol(int symbol, int[] cumFreq) throws IOException {
        long range = high - low + 1;
        high = low + (range * cumFreq[symbol - 1]) / cumFreq[0] - 1;
        low = low + (range * cumFreq[symbol]) / cumFreq[0];

      while (true){
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

    public static void doneEncoding() throws IOException {
       bitsToFollow += 1;
        if (low < ArithmeticParameters.FIRST_QTR) {
           bitsPlusFollow(0);
        } else {
           bitsPlusFollow(1);
        }
    }

    public static void bitsPlusFollow(int bit) throws IOException {
        BitOutput.outputBit(bit);
        while (bitsToFollow > 0) {
            BitOutput.outputBit(bit);
            bitsToFollow -= 1;
        }
    }
}
