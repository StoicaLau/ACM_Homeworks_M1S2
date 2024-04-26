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


    public Coder(int[][] imageValues) {
        this.imageValues = imageValues;
        this.ranges = new Block[NUMBER_OF_RANGES][NUMBER_OF_RANGES];
        this.domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.rangeDetails = new RangeDetails[NUMBER_OF_RANGES][NUMBER_OF_RANGES];

        initializeRangeAndDomains();

        for (int i = 0; i < NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < NUMBER_OF_DOMAINS; j++) {
                System.out.println("range=" + ranges[i][j].toString());
                System.out.println("domains=" + domains[i][j].toString());
            }
        }
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
                        sumSquared += Math.pow(this.imageValues[yImage][xImage], 2);
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
                        result=result/4;
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

    /**
     * get isometric range block
     * @param x x coordinate range block
     * @param y y coordinate range block
     * @param isometricType the isomatric type
     * @return the isometric range value
     */
    public Block getIsometricRangeBlock(int x, int y, int isometricType) {
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
        Pair<Integer, Integer> result = null;
        switch (indexType) {
            case 0:
                result = new Pair<>(x, y);
                break;
            case 1:
                result = new Pair<>(size - 1 - x, y);
                break;
            case 2:
                result = new Pair<>(x, size - 1 - y);
                break;
            case 4://orig 3
                result = new Pair<>(y, x);
                break;
            case 3://orig 4
                result = new Pair<>(size - 1 - y, size - 1 - x);
                break;
            case 5:
                result = new Pair<>(y, size - 1 - x);//test//original 7
                break;
            case 6:
                result = new Pair<>(size - 1 - x, size - 1 - y);
                break;
            case 7:
                result = new Pair<>(size - 1 - y, x);//test//original 5

        }
        return result;
    }


    public void searchMinimumError(int xRange, int yRange) {
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
//                        System.out.println("Result:"+xRange+" "+yRange+"izo"+izo+j+" "+i+set.getValue2());
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
     * Compute error
     *
     * @param domain domain block
     * @param range  range block
     * @return
     */
    private Triplet<Integer, Integer, Double> computeError(Block domain, Block range) {
        double rdSum = 0;
        for (int i = 0; i < BLOCK_SIZE; i++) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                rdSum += range.getValue(i, j) * domain.getValue(i, j);

            }
        }
        int sum1 = (int) Math.pow(BLOCK_SIZE, 2);

        double det = sum1 * domain.getSumSquared() - Math.pow(domain.getSum(), 2);
        double alpha = det == 0.0 ? 0.0 : (double) (sum1 * rdSum - range.getSum() * domain.getSum()) / det;

        int scale = (int) (0.5 + (alpha + MAX_SCALE) / (2.0 * MAX_SCALE) * (1 << S_BITS));

        //normalize
        if (scale < 0) {
            scale = 0;
        }
        if (scale >= (1 << S_BITS)) {
            scale = (1 << S_BITS) - 1;
        }

        alpha = (double) scale / (double) (1 << S_BITS) * (2.0 * MAX_SCALE) - MAX_SCALE;
        double beta = (double) (range.getSum() - alpha * domain.getSum()) / sum1;

        if (alpha > 0.0) {
            beta += alpha * GREY_LEVELS;
        }

        //TODO fabs?
        int offset = (int) (0.5 + (double) beta / ((1.0 + Math.abs(alpha)) * GREY_LEVELS) * ((1 << O_BITS) - 1));
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
        double sqerr = (Double)(range.getSumSquared() + alpha * (alpha * domain.getSumSquared() - 2.0 * rdSum + 2.0 * beta * domain.getSum()) + beta * (beta * sum1 - 2.0 * range.getSum()));
        return new Triplet<>(scale, offset, sqerr);

    }

    /**
     * get the coordinates of domain for specific range
     *
     * @param xRange x coordinate of range
     * @param yRange y coordinate of range
     * @return the coordinates
     */
    public Pair<Integer, Integer> getCoordinatesOfDomainForRange(int xRange, int yRange) {
        System.out.println("Range:" + xRange + ":" + yRange);
        int xDomain = this.rangeDetails[yRange][xRange].getXDomain();
        int yDomain = this.rangeDetails[yRange][xRange].getYDomain();
        System.out.println("domain:" + xDomain + ":" + yDomain + "izo:" + this.rangeDetails[yRange][xRange].getIzoType());
        return new Pair<>(xDomain, yDomain);
    }

    /**
     * get 16x16 matrix with domain values
     *
     * @param xDomain x coordinates to begin
     * @param yDomain y coordinates to begin
     * @return
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

}
