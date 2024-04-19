package arithmeticcoder.model;

//TODO custom EOF 27
public class AdaptiveModel {

    /**
     * MODEL PARAMETERS model.h
     */
    private static final int MAX_FREQUENCY = 16383;
    private static final int NO_OF_CHARS = 256;
    private static final int EOF_SYMBOL = NO_OF_CHARS + 1;
    private static final int NO_OF_SYMBOLS = NO_OF_CHARS + 1;


    /**
     * cumulative frequencies , sums
     */
    private static int[] cumFreq = new int[NO_OF_SYMBOLS + 1];
    /**
     * ----------------------------------------------------------------------------------------
     */

    private static int[] freq = new int[NO_OF_SYMBOLS + 1];


    /**
     * Constructor
     * start model
     */
    public AdaptiveModel() {
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

        //increment the frequency;
        freq[i] += 1;

        while (i > 0) {
            i -= 1;
            cumFreq[i] += 1;
        }
    }

    public int getEOF_SYMBOL() {
        return EOF_SYMBOL;
    }

    public int[] getCumFreq(){
        return cumFreq;
    }

}
