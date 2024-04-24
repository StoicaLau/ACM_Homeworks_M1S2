package com.myapp.h2_nlpc.arithmeticcoder.model;

//TODO custom EOF 27
public class AdaptiveModel {

    /**
     * MODEL PARAMETERS model.h
     */
    private static final int MAX_FREQUENCY = 16383;
    private static final int NO_OF_CHARS = 511;
    private static final int EOF_SYMBOL = NO_OF_CHARS;//256
    private static final int NO_OF_SYMBOLS = NO_OF_CHARS + 1;


    /**
     * cumulative frequencies , sums
     */
    private static int[] sums = new int[NO_OF_SYMBOLS + 1];
    /**
     * ----------------------------------------------------------------------------------------
     */

    private static int[] counts = new int[NO_OF_SYMBOLS + 1];


    /**
     * Constructor
     * start model
     */
    public AdaptiveModel() {
        for (int i = 0; i <= NO_OF_SYMBOLS; i++) {
            counts[i] = 1;
            sums[i] = i;
        }

        counts[NO_OF_SYMBOLS] = 0;//S
    }

    public void updateModel(int symbol) {
        int i;
        //if cumulative frequents is at the maximum,update de frequents and cumulative frequents
        if (sums[NO_OF_SYMBOLS] == MAX_FREQUENCY) {
            int cum = 0;
            for (i = 0; i <= NO_OF_SYMBOLS; i++) {
                counts[i] = (counts[i] + 1) / 2;
                sums[i] = cum;
                cum += counts[i];
            }
        }

//        for (i = symbol; counts[i] == counts[i + 1]; i++) ;
        boolean search = true;
        i = symbol;
        while (search) {
            if (i == 0) {
                search = false;
            } else if (counts[i] == counts[i - 1]) {
                i--;
            } else {
                search = false;
            }
        }

        //increment the frequency;
        counts[i] += 1;

        while (i < NO_OF_SYMBOLS) {
            i += 1;
            sums[i] += 1;
        }
    }

    public int getEOF_SYMBOL() {
        return EOF_SYMBOL;
    }

    public int[] getSums() {
        return sums;
    }

}
