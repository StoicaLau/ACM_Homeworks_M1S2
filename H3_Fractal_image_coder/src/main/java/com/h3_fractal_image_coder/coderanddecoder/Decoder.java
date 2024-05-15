package com.h3_fractal_image_coder.coderanddecoder;

import com.h3_fractal_image_coder.AppParameters;
import com.h3_fractal_image_coder.Block;
import com.h3_fractal_image_coder.RangeDetails;
import com.h3_fractal_image_coder.mytools.BitReader;

import java.io.File;
import java.io.IOException;

/**
 * Decoder class
 */
public class Decoder {


    /**
     * current values
     */
    int[][] currentValues;

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
     * constructor
     *
     * @param imageValues imageValues
     */
    public Decoder(int[][] imageValues) throws IOException {

        this.currentValues = imageValues;
        this.ranges = Tools.initializeRanges(this.currentValues);
        this.domains = Tools.initializeDomains(this.currentValues);
        this.rangeDetails = new RangeDetails[Tools.NUMBER_OF_RANGES][Tools.NUMBER_OF_RANGES];
    }

    /**
     * get range details from a file
     *
     * @param file the file that contains range details
     * @throws IOException an exception
     */
    public void getRangesDetailsFromFile(File file) throws IOException {
        BitReader bitReader = new BitReader(file);

        int headerInfoSize = 1078;
        for (int i = 0; i < headerInfoSize; i++) {
            bitReader.readNBits(8);
        }

        for (int i = 0; i < rangeDetails.length; i++) {
            for (int j = 0; j < rangeDetails[i].length; j++) {
                int xDomain = bitReader.readNBits(6);
                int yDomain = bitReader.readNBits(6);
                int izo = bitReader.readNBits(3);
                int scale = bitReader.readNBits(5);
                int offset = bitReader.readNBits(7);
                this.rangeDetails[i][j] = new RangeDetails(xDomain, yDomain, izo, scale, offset);
            }
        }
    }

    /**
     * decoding current values according to domains
     */
    public void decode() {
        int[][] newValues = new int[512][512];

        for (int i = 0; i < Tools.NUMBER_OF_RANGES; i++) {
            for (int j = 0; j < Tools.NUMBER_OF_RANGES; j++) {
                int xDomain = this.rangeDetails[i][j].getXDomain();
                int yDomain = this.rangeDetails[i][j].getYDomain();
                int izo = this.rangeDetails[i][j].getIzoType();

                double scale = this.rangeDetails[i][j].getScale();
                double offset = this.rangeDetails[i][j].getOffset();

                scale = (double) scale / (double) (1 << AppParameters.S_BITS) * (2.0 * AppParameters.MAX_SCALE) - AppParameters.MAX_SCALE;
                offset = offset / (double) ((1 << AppParameters.O_BITS) - 1) * ((1.0 + Math.abs(scale)) * AppParameters.GREY_LEVELS);

                if (scale > 0.0) {
                    offset -= scale * AppParameters.GREY_LEVELS;
                }

                Block isometricDomain = Tools.getIsometricDomainBlock(this.domains[yDomain][xDomain], izo);
                int[][] domainValues = isometricDomain.getValues();

                int xBegin = j * 8;
                int yBegin = i * 8;
                for (int yImage = yBegin; yImage < yBegin + Tools.BLOCK_SIZE; yImage++) {
                    for (int xImage = xBegin; xImage < xBegin + Tools.BLOCK_SIZE; xImage++) {
                        int xBlock = xImage - xBegin;
                        int yBlock = yImage - yBegin;

                        newValues[yImage][xImage] = (int) ((scale * domainValues[yBlock][xBlock]) + offset);
//                        newValues[yImage][xImage] = Tools.normalizeValue(newValues[yImage][xImage]);
                        if (newValues[yImage][xImage] > 255) {
                            System.out.println(scale);
                            System.out.println(offset);
                            System.out.println(newValues[yImage][xImage] + " ");
                        }
                    }
//                    System.out.println();
                }
            }
        }

        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                this.currentValues[i][j] = newValues[i][j];
            }
        }
        this.ranges = Tools.initializeRanges(this.currentValues);
        this.domains = Tools.initializeDomains(this.currentValues);
    }

    /**
     * get current values
     *
     * @return the current values
     */
    public int[][] getCurrentValues() {
        return this.currentValues;
    }


}
