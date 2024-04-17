//TODO custom EOF 27
public class AdaptiveModel {

    /**
     * MODEL PARAMETERS model.h
     */
    private static final int MAX_FREQUENCY = 16383;
    private static final int NO_OF_CHARS = 256;
    public static final int EOF_SYMBOL = NO_OF_CHARS + 1;
    private static final int NO_OF_SYMBOLS = NO_OF_CHARS + 1;

    /**
     * Translation tables between characters and symbol indexes ,nu mai e nevoie ,orice simbol e si index
     */
    public static int[] charToIndex=new int[NO_OF_SYMBOLS];
    public static char[] indexToChar=new char[NO_OF_SYMBOLS+1];


    /**
     * cumulative frequencies , sums
     */
    public static int[] cumFreq=new int[NO_OF_SYMBOLS+1];
    /**
     * ----------------------------------------------------------------------------------------
     */

    private static int[] freq=new int[NO_OF_SYMBOLS+1];


    /**
     * Constructor
     */
    public AdaptiveModel() {


    }


     public void startModel() {

        //tables that translate between symbol index and characters
        for (int i = 0; i < NO_OF_CHARS; i++) {
            charToIndex[i] = i + 1;
            indexToChar[i + 1] = (char) i;
        }

        //frequents of symbols 1, cumulative frequents = NO_Of_SYMBOLS - i
        for (int i = 0; i <= NO_OF_SYMBOLS; i++) {
            freq[i] = 1;
            cumFreq[i] = NO_OF_SYMBOLS - i;
        }

        freq[0] = 0;
    }

    public void updateModel(int symbol) {
        int i;
        //if cumulative frequents is at the maximum,update de frequents and cumulative frequents
        if (cumFreq[0] == MAX_FREQUENCY) {
            int cum = 0;
            for (i = NO_OF_SYMBOLS; i >= 0; i--) {
                freq[i] = (freq[i] + 1) / 2;
                cumFreq[i] = cum;
                cum += freq[i];
            }
        }

        for (i = symbol; freq[i] == freq[i - 1]; i--) ;

        //update the translation table;
        if (i < symbol) {
            int chI = indexToChar[i];
            int chSymbol = indexToChar[symbol];
            indexToChar[i] = (char) chSymbol;
            indexToChar[symbol] = (char) chI;
            charToIndex[chI] = symbol;
            charToIndex[chSymbol] = i;
        }

        //increment the frequency;
        freq[i] += 1;
        while (i > 0) {
            i -= 1;
            cumFreq[i] += 1;
        }

    }

}
