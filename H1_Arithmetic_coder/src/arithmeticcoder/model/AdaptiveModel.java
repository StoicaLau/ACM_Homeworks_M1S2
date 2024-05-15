package arithmeticcoder.model;

//TODO custom EOF 27
public class AdaptiveModel {

    /**
     * cumulative frequencies , sums
     */
    private static int[] sums = new int[AdaptiveModelConstants.NO_OF_SYMBOLS + 1];
    /**
     * ----------------------------------------------------------------------------------------
     */

    private static int[] counts = new int[AdaptiveModelConstants.NO_OF_SYMBOLS + 1];


    /**
     * Constructor
     * start model
     */
    public AdaptiveModel() {
        for (int i = 0; i <= AdaptiveModelConstants.NO_OF_SYMBOLS; i++) {
            counts[i] = 1;
            sums[i] = i;
        }

        counts[AdaptiveModelConstants.NO_OF_SYMBOLS] = 0;
    }

    public void updateModel(int symbol) {
        int i;
        //if cumulative frequents is at the maximum,update de frequents and cumulative frequents
//        if (sums[NO_OF_SYMBOLS] == MAX_FREQUENCY) {
//            int cum = 0;
//            for (i = 0; i <= NO_OF_SYMBOLS; i++) {
//                counts[i] = (counts[i] + 1) / 2;
//                sums[i] = cum;
//                cum += counts[i];
//            }
//        }

//        for (i = symbol; counts[i] == counts[i + 1]; i++) ;
        boolean search = true;
        i = symbol;
        for (i = symbol; i > 0 && (counts[i] == counts[i - 1]); i--) ;

        //increment the frequency;
        counts[i] += 1;

        while (i < AdaptiveModelConstants.NO_OF_SYMBOLS) {
            i += 1;
            sums[i] += 1;
        }
    }



    public int[] getSums() {
        return sums;
    }

}
