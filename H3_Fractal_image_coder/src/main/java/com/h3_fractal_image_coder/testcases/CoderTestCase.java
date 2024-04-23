package com.h3_fractal_image_coder.testcases;

import com.h3_fractal_image_coder.coderanddecoder.Coder;
import com.h3_fractal_image_coder.mytools.BitReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Coder TestCase
 */
//TODO minim la DI la fiecare ri
public class CoderTestCase {

    /**
     * image header
     */
    private List<Integer> imageHeader;

    /**
     * the coder
     */
    private Coder coder;

    /**
     * Constructor
     *
     * @param file the image file
     * @throws IOException an exception
     */
    public CoderTestCase(File file) throws IOException {
        this.imageHeader = new ArrayList<>();
        int[][] imageValues = new int[512][512];
        BitReader bitReader = new BitReader(file);
        int headerInfoSize = 1078;
        int imageValuesSize = (int) file.length() - headerInfoSize;

        for (int i = 0; i < headerInfoSize; i++) {
            this.imageHeader.add(bitReader.readNBits(8));
        }

        for (int i = 0; i < imageValuesSize; i++) {
            int value = bitReader.readNBits(8);
            imageValues[i / 512][i % 512] = value;
        }

        this.coder = new Coder(imageValues);
        bitReader.close();
    }
}
