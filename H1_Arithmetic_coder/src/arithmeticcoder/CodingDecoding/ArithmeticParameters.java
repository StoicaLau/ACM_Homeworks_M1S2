package arithmeticcoder.CodingDecoding;

/**
 * This class is used only for accessing constant values
 */
public class ArithmeticParameters {
    /**
     * code value bits
     */
    public static final int CODE_VALUE_BITS = 16;

    /**
     * biggest value 2^16(code_value_bits
     */
    public static final long TOP_VALUE = (1L << CODE_VALUE_BITS) - 1;
    /**
     * first qtr ,first segment
     */


    public static final long FIRST_QTR = TOP_VALUE / 4 + 1;
    /**
     * half
     */
    public static final long HALF = 2 * FIRST_QTR;
    /**
     * third qtr
     */
    public static final long THIRD_QTR = 3 * FIRST_QTR;

    /**
     * mask 32 bits
     */
    public static final long MASK32B = 0xFFFFFFFFL;
}
