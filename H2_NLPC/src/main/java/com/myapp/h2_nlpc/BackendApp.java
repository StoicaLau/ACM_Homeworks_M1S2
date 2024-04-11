package com.myapp.h2_nlpc;

import com.myapp.h2_nlpc.CoderAndDecoder.Coder;
import com.myapp.h2_nlpc.CoderAndDecoder.CoderAndDecoderTools;
import com.myapp.h2_nlpc.CoderAndDecoder.Decoder;
import com.myapp.h2_nlpc.mytools.BitReader;
import com.myapp.h2_nlpc.mytools.BitWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Backend app class
 */
public class BackendApp {


    /**
     * File
     */
    private File file;
    /**
     * bit reader
     */
    private BitReader bitReader;

    /**
     * bit writer
     */
    private BitWriter bitWriter;

    /**
     * image header
     */
    private List<Integer> imageHeader;

    /**
     * image values
     */
    private int[][] imageValues;

    /***
     * coder
     */
    private Coder coder;

    /**
     * decoder
     */
    private Decoder decoder;


    /**
     * Constructor
     *
     */
    public BackendApp()  {

        this.imageHeader = new ArrayList<>();
        this.imageValues = new int[256][256];
    }

//    public Image createErrorImage(String imageType, double contrast) throws IOException {
//        int[][] data = new int[256][256];
//        if (imageType.equals("prediction error"))
//            data = this.coder.getErrorValues();
//        if (imageType.equals("Q prediction error"))
//            data = this.coder.getQuantizedErrorValues();
//
//
//        WritableImage writableImage = new WritableImage(256, 256);
//
//
//        for (int i= 0; i < 256; i++) {
//            for (int j = 0; j < 256; j++) {
//                double value = 0;
//                if (imageType.equals("prediction error"))
//                    value = (double) (data[i][j] + 127);
//                else {
//                    value = (double) (data[i][j] * contrast + 128);
//                }
//                Color color = Color.gray(value / 255.0);
//                writableImage.getPixelWriter().setColor(j, 255-i, color);
//            }
//        }
//
//        return writableImage;
//
//    }



    //DOSENT WORK Version

//    public File createErrorImage(String imageType, double contrast) throws IOException {
//        int[][] data = new int[256][256];
//        if (imageType.equals("prediction error"))
//            data = this.coder.getErrorValues();
//        if (imageType.equals("Q prediction error"))
//            data = this.coder.getQuantizedErrorValues();
//        File file = new File("imageError.bmp");
//        data = this.imageValues;
//
//        this.bitWriter = new BitWriter(file);
//        for (int value : imageHeader) {
//            bitWriter.writeNBits(value, 8);
//        }
//
//        for (int i = 0; i < 256; i++) {
//            for (int j = 0; j < 256; j++) {
//                int value = 0;
//                value = data[i][j];
////                if (imageType.equals("prediction error"))
////                    value = (int) (data[i][j] + 127);
////                else {
////                    value = (int) (data[i][j] * contrast + 128);
////                }
////                System.out.print(value + " ");
//                bitWriter.writeNBits(value, 8);
//            }
////            System.out.println();
//        }
//        bitWriter.close();
//        return file;
//
//    }







}
