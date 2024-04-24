package com.h3_fractal_image_coder.coderanddecoder;


import com.h3_fractal_image_coder.Block;

/**
 * The coder Class
 */
public class Coder {

    /**
     * range size
     */
    private static final int BLOCK_SIZE = 8;

    /**
     * number of ranges
     */
    private static final int NUMBER_OF_RANGES = 64;

    /**
     * number of domains
     */
    private static final int NUMBER_OF_DOMAINS = 63;

    /**
     * image values
     */
    private int[][] imageValues;

    private Block[][] ranges;
    private Block[][] domains;


    public Coder(int[][] imageValues) {
        this.imageValues = imageValues;
        this.ranges = new Block[NUMBER_OF_RANGES][NUMBER_OF_RANGES];
        this.domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        initializeRangeAndDomains();
        for (int i = 0; i < NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < NUMBER_OF_DOMAINS; j++) {
                System.out.println("range="+ranges[i][j].toString());
                System.out.println("domains="+ domains[i][j].toString());
            }
        }
    }

    /**
     * initialize range and domains table
     */
    public void initializeRangeAndDomains() {
        //Range
        for (int i = 0; i < NUMBER_OF_RANGES; i++) {
            for (int j = 0; j < NUMBER_OF_RANGES; j++) {
                int xBegin = j * 8;
                int yBegin = i * 8;

                int[][] blockValues = new int[BLOCK_SIZE][BLOCK_SIZE];
                int sum = 0;
                int sumSquared = 0;
                for (int yImage = yBegin; yImage < yBegin + BLOCK_SIZE; yImage++) {
                    for (int xImage = xBegin; xImage < xBegin + BLOCK_SIZE; xImage++) {
                        int xBlock = xImage - xBegin;
                        int yBlock = yImage - yBegin;
                        blockValues[xBlock][yBlock] = this.imageValues[yImage][xImage];
                        sum += this.imageValues[yImage][xImage];
                        sumSquared += Math.pow(this.imageValues[yImage][xImage], 2);
                    }

                }

                ranges[i][j] = new Block(BLOCK_SIZE, blockValues, sum, sumSquared);
            }
        }

        //Domains
        for (int i = 0; i < NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < NUMBER_OF_DOMAINS; j++) {
                int xBegin = j;
                int yBegin = i;

                int[][] blockValues = new int[BLOCK_SIZE][BLOCK_SIZE];
                int sum = 0;
                int sumSquared = 0;
                for (int yImage = yBegin; yImage < yBegin + 2 * BLOCK_SIZE; yImage += 2) {
                    for (int xImage = xBegin; xImage < xBegin + 2 * BLOCK_SIZE; xImage += 2) {
                        int result = this.imageValues[yImage][xImage] + this.imageValues[yImage + 1][xImage] + this.imageValues[yImage][xImage + 1] + this.imageValues[yImage + 1][xImage + 1];
                        int xBlock = (xImage - xBegin) / 2;
                        int yBlock = (yImage - yBegin) / 2;
                        blockValues[yBlock][xBlock] = result;
                        sum += result;
                        sumSquared += Math.pow(result, 2);
                    }

                }

                domains[i][j] = new Block(BLOCK_SIZE, blockValues, sum, sumSquared);
            }
        }

    }




}
