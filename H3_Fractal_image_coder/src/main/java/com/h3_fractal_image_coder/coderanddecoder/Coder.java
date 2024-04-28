package com.h3_fractal_image_coder.coderanddecoder;

import com.h3_fractal_image_coder.Block;
import com.h3_fractal_image_coder.RangeDetails;
import org.javatuples.Pair;
import org.javatuples.Triplet;


/**
 * The coder Class
 */
public class Coder {

    /**
     * number of bits for scale
     */
    private static final int S_BITS = 5;
    /**
     * number of bits for offset
     */
    private static final int O_BITS = 7;
    /**
     * maximum grey level
     */
    private static final int GREY_LEVELS = 255;

    /**
     * maximum allowed value for scale
     */
    private static final double MAX_SCALE = 1.0;


    /**
     * Number of isometrics
     */
    private static final int ISOMETRIC_LENGTH = 8;

    /**
     * block size
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

    /**
     * ranges
     */
    private Block[][] ranges;

    /**
     * domains
     */
    private Block[][] domains;

    /**
     * range details
     */
    private RangeDetails[][] rangeDetails;


    /**
     * Constructor
     *
     * @param imageValues image values
     */
    public Coder(int[][] imageValues) {
        this.imageValues = imageValues;
        this.ranges = new Block[NUMBER_OF_RANGES][NUMBER_OF_RANGES];
        this.domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.rangeDetails = new RangeDetails[NUMBER_OF_RANGES][NUMBER_OF_RANGES];

        initializeRangeAndDomains();
    }

    public Block getRangeBlock(int x, int y) {
        return this.ranges[y][x];
    }

    /**
     * get image value
     *
     * @return image value
     */
    public int[][] getImageValues() {
        return imageValues;
    }

    /**
     * get the range details
     *
     * @return the range details
     */
    public RangeDetails[][] getRangeDetails() {
        return rangeDetails;
    }

    /**
     * get the range for specific range
     *
     * @param xRange x coordinate of range
     * @param yRange y coordinate of range
     * @return the domain
     */
    public RangeDetails getRangeDetailsCorrespondingToRangeCoordinates(int xRange, int yRange) {
        return new RangeDetails(this.rangeDetails[yRange][xRange]);
    }

    /**
     * get 16x16 matrix with domain values
     *
     * @param xDomain x coordinates to begin
     * @param yDomain y coordinates to begin
     * @return the image values,16 by 16 pixels
     */
    public int[][] get16By16DomainValues(int xDomain, int yDomain) {
        int xBegin = xDomain * 8;
        int yBegin = yDomain * 8;
        int size = 16;
        int[][] domainValues = new int[size][size];
        for (int i = yBegin; i < yBegin + size; i++) {
            for (int j = xBegin; j < xBegin + size; j++) {
                domainValues[i - yBegin][j - xBegin] = this.imageValues[i][j];
            }
        }
        return domainValues;
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
                        blockValues[yBlock][xBlock] = this.imageValues[yImage][xImage];
                        sum += this.imageValues[yImage][xImage];
                        sumSquared += this.imageValues[yImage][xImage]* this.imageValues[yImage][xImage];
                    }

                }

                ranges[i][j] = new Block(BLOCK_SIZE, blockValues, sum, sumSquared);
            }
        }

        //Domains
        for (int i = 0; i < NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < NUMBER_OF_DOMAINS; j++) {
                int xBegin = j * 8;
                int yBegin = i * 8;

                int[][] blockValues = new int[BLOCK_SIZE][BLOCK_SIZE];
                int sum = 0;
                int sumSquared = 0;
                for (int yImage = yBegin; yImage < yBegin + 2 * BLOCK_SIZE; yImage += 2) {
                    for (int xImage = xBegin; xImage < xBegin + 2 * BLOCK_SIZE; xImage += 2) {
                        int result = this.imageValues[yImage][xImage] + this.imageValues[yImage + 1][xImage] + this.imageValues[yImage][xImage + 1] + this.imageValues[yImage + 1][xImage + 1];
                        result = result / 4;
                        int xBlock = (xImage - xBegin) / 2;
                        int yBlock = (yImage - yBegin) / 2;
                        blockValues[yBlock][xBlock] = result;
                        sum += result;
                        sumSquared += result * result;
                    }

                }

                domains[i][j] = new Block(BLOCK_SIZE, blockValues, sum, sumSquared);
            }
        }

    }

    /**
     * create range details corresponding to range coordinates
     *
     * @param xRange x range coordinate
     * @param yRange y range coordinate
     */
    public void createRangeDetailsCorrespondingToRangeCoordinates(int xRange, int yRange) {
        RangeDetails currentRangeDetails = null;
        Double minimumError = Double.MAX_VALUE;

        for (int i = 0; i < NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < NUMBER_OF_DOMAINS; j++) {
                Block domain = this.domains[i][j];
                for (int izo = 0; izo < ISOMETRIC_LENGTH; izo++) {
                    Block isometricRange = this.getIsometricRangeBlock(xRange, yRange, izo);
                    Triplet<Integer, Integer, Double> set = this.computeError(domain, isometricRange);
                    if (set.getValue2() < minimumError) {
                        minimumError = set.getValue2();

                        int xDomain = j;
                        int yDomain = i;
                        int scale = set.getValue0();
                        int offset = set.getValue1();
                        currentRangeDetails = new RangeDetails(xDomain, yDomain, izo, scale, offset);
                    }
                }
            }
        }

        this.rangeDetails[yRange][xRange] = new RangeDetails(currentRangeDetails);
    }


    /**
     * get isometric range block
     *
     * @param x             x coordinate range block
     * @param y             y coordinate range block
     * @param isometricType the isometric type
     * @return the isometric range value
     */
    private Block getIsometricRangeBlock(int x, int y, int isometricType) {
        Block rangeBlock = this.getRangeBlock(x, y);
        int size = rangeBlock.getSize();
        int sum = rangeBlock.getSum();
        int sumSquared = rangeBlock.getSumSquared();
        int[][] currentValues = rangeBlock.getValues();
        int[][] newValues = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Pair<Integer, Integer> coordinates = this.useIsometric(j, i, isometricType, size);
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
    //TODO cum stiu care izometrie e buna daca originala arata ca una din cele 7 sau invers ?
    private Pair<Integer, Integer> useIsometric(int x, int y, int indexType, int size) {
        return switch (indexType) {
            case 0 -> new Pair<>(x, y);
            case 1 -> new Pair<>(size - 1 - x, y);
            case 2 -> new Pair<>(x, size - 1 - y);
            case 4 ->//orig 3
                    new Pair<>(y, x);
            case 3 ->//orig 4
                    new Pair<>(size - 1 - y, size - 1 - x);
            case 5 -> new Pair<>(y, size - 1 - x);//test//original 7
            case 6 -> new Pair<>(size - 1 - x, size - 1 - y);
            case 7 -> new Pair<>(size - 1 - y, x);
            default -> null;//test//original 5

        };
    }


    /**
     * Compute error
     *
     * @param domain domain block
     * @param range  range block
     * @return scale, offset ,error
     */
    private Triplet<Integer, Integer, Double> computeError(Block domain, Block range) {
        double rdSum = 0;
        for (int i = 0; i < BLOCK_SIZE; i++) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                rdSum += range.getValue(i, j) * domain.getValue(i, j);

            }
        }
        int sum1 =  BLOCK_SIZE*BLOCK_SIZE;

        double det = sum1 * domain.getSumSquared() - (domain.getSum()*domain.getSum());
        double alpha = det == 0.0 ? 0.0 : (sum1 * rdSum - range.getSum() * domain.getSum()) / det;

        int scale = (int) (0.5 + (alpha + MAX_SCALE) / (2.0 * MAX_SCALE) * (1 << S_BITS));

        //normalize
        if (scale < 0) {
            scale = 0;
        }
        if (scale >= (1 << S_BITS)) {
            scale = (1 << S_BITS) - 1;
        }

        alpha = (double) scale / (double) (1 << S_BITS) * (2.0 * MAX_SCALE) - MAX_SCALE;
        double beta = (range.getSum() - alpha * domain.getSum()) / sum1;

        if (alpha > 0.0) {
            beta += alpha * GREY_LEVELS;
        }

        //TODO fabs?
        int offset = (int) (0.5 + beta / ((1.0 + Math.abs(alpha)) * GREY_LEVELS) * ((1 << O_BITS) - 1));
        if (offset < 0) {
            offset = 0;
        }
        if (offset >= (1 << O_BITS)) {
            offset = (1 << O_BITS) - 1;
        }

        beta = (double) offset / (double) ((1 << O_BITS) - 1) * ((1.0 + Math.abs(alpha)) * GREY_LEVELS);
        if (alpha > 0.0) {
            beta -= alpha * GREY_LEVELS;
        }

        //TODO /sum1?
        double sqErr = range.getSumSquared() + alpha * (alpha * domain.getSumSquared() - 2.0 * rdSum + 2.0 * beta * domain.getSum()) + beta * (beta * sum1 - 2.0 * range.getSum());
        return new Triplet<>(scale, offset, sqErr);
    }
}
