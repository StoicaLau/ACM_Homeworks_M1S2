package com.h3_fractal_image_coder.coderanddecoder;

import com.h3_fractal_image_coder.Block;
import org.javatuples.Pair;

public class Tools {

    /**
     * Number of isometrics
     */
    public static final int ISOMETRIC_LENGTH = 8;

    /**
     * block size
     */
    public static final int BLOCK_SIZE = 8;

    /**
     * number of ranges
     */
    public static final int NUMBER_OF_RANGES = 64;

    /**
     * number of domains
     */
    public static final int NUMBER_OF_DOMAINS = 63;

    /**
     * initialize ranges according to the matrix of values
     *
     * @param values the image values
     * @return the ranges
     */
    public static Block[][] initializeRanges(int[][] values) {
        Block[][] ranges = new Block[NUMBER_OF_RANGES][NUMBER_OF_RANGES];
        for (int i = 0; i < Tools.NUMBER_OF_RANGES; i++) {
            for (int j = 0; j < Tools.NUMBER_OF_RANGES; j++) {
                int xBegin = j * 8;
                int yBegin = i * 8;

                int[][] blockValues = new int[Tools.BLOCK_SIZE][Tools.BLOCK_SIZE];
                int sum = 0;
                int sumSquared = 0;
                for (int yImage = yBegin; yImage < yBegin + Tools.BLOCK_SIZE; yImage++) {
                    for (int xImage = xBegin; xImage < xBegin + Tools.BLOCK_SIZE; xImage++) {
                        int xBlock = xImage - xBegin;
                        int yBlock = yImage - yBegin;
                        blockValues[yBlock][xBlock] = values[yImage][xImage];
                        sum += values[yImage][xImage];
                        sumSquared += values[yImage][xImage] * values[yImage][xImage];
                    }

                }

                ranges[i][j] = new Block(Tools.BLOCK_SIZE, blockValues, sum, sumSquared);
            }
        }

        return ranges;
    }

    /**
     * initialize domains according to the matrix of values
     *
     * @param values the image values
     * @return the domains
     */
    public static Block[][] initializeDomains(int[][] values) {
        Block[][] domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        for (int i = 0; i < Tools.NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < Tools.NUMBER_OF_DOMAINS; j++) {
                int xBegin = j * 8;
                int yBegin = i * 8;

                int[][] blockValues = new int[Tools.BLOCK_SIZE][Tools.BLOCK_SIZE];
                int sum = 0;
                int sumSquared = 0;
                for (int yImage = yBegin; yImage < yBegin + 2 * Tools.BLOCK_SIZE; yImage += 2) {
                    for (int xImage = xBegin; xImage < xBegin + 2 * Tools.BLOCK_SIZE; xImage += 2) {
                        int result = values[yImage][xImage] + values[yImage + 1][xImage] + values[yImage][xImage + 1] + values[yImage + 1][xImage + 1];
                        result = result / 4;
                        int xBlock = (xImage - xBegin) / 2;
                        int yBlock = (yImage - yBegin) / 2;
                        blockValues[yBlock][xBlock] = result;
                        sum += result;
                        sumSquared += result * result;
                    }

                }

                domains[i][j] = new Block(Tools.BLOCK_SIZE, blockValues, sum, sumSquared);
            }
        }
        return domains;
    }

    /**
     * get isometric domain block
     *
     * @param domain        the domain
     * @param isometricType the isometric type
     * @return the isometric range value
     */
    public static Block getIsometricDomainBlock(Block domain, int isometricType) {
        int size = domain.getSize();
        int sum = domain.getSum();
        int sumSquared = domain.getSumSquared();
        int[][] currentValues = domain.getValues();
        int[][] newValues = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Pair<Integer, Integer> coordinates = useIsometric(j, i, isometricType, size);
                int newX = coordinates.getValue0();
                int newY = coordinates.getValue1();
                newValues[newY][newX] = currentValues[i][j];
            }

        }
        return new Block(size, newValues, sum, sumSquared);
    }

    /**
     * use isometric to get the new x and new y
     *
     * @param x         x coordinate
     * @param y         y coordinate
     * @param indexType the index of isometric type
     * @param size      the size of matrix
     * @return a pair with new x coordinate and new y coordinate
     */
    private static Pair<Integer, Integer> useIsometric(int x, int y, int indexType, int size) {
        return switch (indexType) {
            case 0 -> new Pair<>(x, y);
            case 1 -> new Pair<>(size - 1 - x, y);
            case 2 -> new Pair<>(x, size - 1 - y);
            case 4 ->//orig 3
                    new Pair<>(y, x);
            case 3 ->//orig 4
                    new Pair<>(size - 1 - y, size - 1 - x);
            case 5 -> new Pair<>(size - 1 - y, x);//test//original 7
            case 6 -> new Pair<>(size - 1 - x, size - 1 - y);
            case 7 -> new Pair<>(y, size - 1 - x);
            default -> null;//test//original 5

        };
    }

    /**
     * bring the value between 0 and 255
     *
     * @param value the value that will be normalized
     * @return normalized value
     */
    public static int normalizeValue(int value) {

      while (value > 255) {
          value = value / 256;
      }
        return value;
    }


}
