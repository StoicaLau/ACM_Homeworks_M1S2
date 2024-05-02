package com.h3_fractal_image_coder.usecases;

import com.h3_fractal_image_coder.RangeDetails;
import com.h3_fractal_image_coder.coderanddecoder.Coder;
import com.h3_fractal_image_coder.mytools.BitReader;
import com.h3_fractal_image_coder.mytools.BitWriter;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Coder TestCase
 */
//TODO minim la DI la fiecare ri
public class CoderUseCase {

    /**
     * image header
     */
    private List<Integer> imageHeader;

    /**
     * File name
     */
    String fileName;



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
    public CoderUseCase(File file) throws IOException {
        this.fileName = file.getName();
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
            imageValues[511 - i / 512][i % 512] = value;
        }

        this.coder = new Coder(imageValues);
        bitReader.close();
    }

    /**
     * get range image
     *
     * @param x x index in range table
     * @param y y index in range table
     * @return an image
     */
    public WritableImage getRangeImage(int x, int y) {
        int[][] rangeValues = this.coder.getRangeBlock(x, y).getValues();


        WritableImage writableImage = new WritableImage(80, 80);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 80; j++) {
                int value = rangeValues[i / 10][j / 10];//i=x j=x
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, i, grayColor);
            }
        }

        return writableImage;
    }

    /**
     * get domain image
     *
     * @param x x index in range table
     * @param y y index in range table
     * @return an image
     */
    public WritableImage getDomainImage(int x, int y) {
        int[][] rangeValues = this.coder.get16By16DomainValues(x, y);


        WritableImage writableImage = new WritableImage(160, 160);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < 160; i++) {
            for (int j = 0; j < 160; j++) {
                int value = rangeValues[i / 10][j / 10];//i=x j=x
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, i, grayColor);
            }
        }

        return writableImage;
    }

    /**
     * get the domain for specific range
     *
     * @param xRange x coordinate of range
     * @param yRange y coordinate of range
     * @return the domain
     */
    public RangeDetails getRangeDetailsCorrespondingToRankCoordinates(int xRange, int yRange) {
        return this.coder.getRangeDetailsCorrespondingToRangeCoordinates(xRange, yRange);
    }

    /**
     * create range details corresponding to range coordinates
     *
     * @param xRange x coordinates of range
     * @param yRange y coordinates of range
     */
    public void createRangeDetailsCorrespondingToRangeCoordinates(int xRange, int yRange) {
        this.coder.createRangeDetailsCorrespondingToRangeCoordinates(xRange, yRange);
    }

    /**
     * save as fic file data about ranges details
     *
     * @param selectedDirectory directory file
     * @throws IOException an exception
     */
    public void saveRangeDetailsAsFic(File selectedDirectory) throws IOException {
        File file = new File(selectedDirectory.getPath() + File.separator + this.fileName + ".fic");
        BitWriter bitWriter = new BitWriter(file);
        for (int value : this.imageHeader) {
            bitWriter.writeNBits(value, 8);
        }

        RangeDetails[][] rangeDetails = this.coder.getRangeDetails();
        for (int i = 0; i < rangeDetails.length; i++) {
            for (int j = 0; j < rangeDetails[i].length; j++) {
                int xDomain = rangeDetails[i][j].getXDomain();
                int yDomain = rangeDetails[i][j].getYDomain();
                int izo = rangeDetails[i][j].getIzoType();
                int scale = rangeDetails[i][j].getScale();
                int offset = rangeDetails[i][j].getOffset();
                bitWriter.writeNBits(xDomain, 6);
                bitWriter.writeNBits(yDomain, 6);
                bitWriter.writeNBits(izo, 3);
                bitWriter.writeNBits(scale, 5);
                bitWriter.writeNBits(offset, 7);
            }
        }

        bitWriter.close();
    }

    /**
     * get original image values
     */
    public int[][] getOriginalImageValues() {
        return this.coder.getImageValues();
    }
}

