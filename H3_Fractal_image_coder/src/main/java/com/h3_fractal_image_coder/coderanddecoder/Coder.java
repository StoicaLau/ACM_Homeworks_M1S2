package com.h3_fractal_image_coder.coderanddecoder;

import com.h3_fractal_image_coder.Block;
import com.h3_fractal_image_coder.IsometricDomains;
import com.h3_fractal_image_coder.RangeDetails;
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
     * isometric domains
     */
    private IsometricDomains isometricDomains;


    /**
     * Constructor
     *
     * @param imageValues image values
     */
    public Coder(int[][] imageValues) {
        this.imageValues = imageValues;
        this.ranges = Tools.initializeRanges(this.imageValues);
        this.domains = Tools.initializeDomains(this.imageValues);
        this.rangeDetails = new RangeDetails[Tools.NUMBER_OF_RANGES][Tools.NUMBER_OF_RANGES];
        this.isometricDomains = new IsometricDomains(this.domains);
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
     * create range details corresponding to range coordinates
     *
     * @param xRange x range coordinate
     * @param yRange y range coordinate
     */
    public void createRangeDetailsCorrespondingToRangeCoordinates(int xRange, int yRange) {
        RangeDetails currentRangeDetails = null;
        Double minimumError = Double.MAX_VALUE;
        Block range = this.getRangeBlock(xRange, yRange);
        for (int i = 0; i < Tools.NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < Tools.NUMBER_OF_DOMAINS; j++) {
                for (int izo = 0; izo < Tools.ISOMETRIC_LENGTH; izo++) {
                    Block isometricDomain = this.isometricDomains.getIsometricDomainBlock(i, j, izo);
                    Triplet<Integer, Integer, Double> set = this.computeError(isometricDomain, range);
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
     * Compute error
     *
     * @param domain domain block
     * @param range  range block
     * @return scale, offset ,error
     */
    private Triplet<Integer, Integer, Double> computeError(Block domain, Block range) {
        double rdSum = 0;
        for (int i = 0; i < Tools.BLOCK_SIZE; i++) {
            for (int j = 0; j < Tools.BLOCK_SIZE; j++) {
                rdSum += range.getValue(i, j) * domain.getValue(i, j);

            }
        }
        int sum1 = Tools.BLOCK_SIZE * Tools.BLOCK_SIZE;

        double det = sum1 * domain.getSumSquared() - (domain.getSum() * domain.getSum());
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
