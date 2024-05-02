package com.h3_fractal_image_coder;

import com.h3_fractal_image_coder.coderanddecoder.Tools;

/**
 * A class that has all isometric domains
 */
public class IsometricDomains {
    /**
     * number of domains
     */
    private static final int NUMBER_OF_DOMAINS = 63;

    /**
     * domains with isometric type 0
     */
    Block[][] isometricType0Domains;
    /**
     * domains with isometric type 1
     */
    Block[][] isometricType1Domains;
    /**
     * domains with isometric type 2
     */
    Block[][] isometricType2Domains;
    /**
     * domains with isometric type 3
     */
    Block[][] isometricType3Domains;
    /**
     * domains with isometric type 4
     */
    Block[][] isometricType4Domains;
    /**
     * domains with isometric type 5
     */
    Block[][] isometricType5Domains;
    /**
     * domains with isometric type 6
     */
    Block[][] isometricType6Domains;
    /**
     * domains with isometric type 7
     */
    Block[][] isometricType7Domains;

    public IsometricDomains(Block[][] domains) {
        this.isometricType0Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.isometricType1Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.isometricType2Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.isometricType3Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.isometricType4Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.isometricType5Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.isometricType6Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];
        this.isometricType7Domains = new Block[NUMBER_OF_DOMAINS][NUMBER_OF_DOMAINS];

        for (int i = 0; i < NUMBER_OF_DOMAINS; i++) {
            for (int j = 0; j < NUMBER_OF_DOMAINS; j++) {
                Block domain = domains[i][j];
                this.isometricType0Domains[i][j] = Tools.getIsometricDomainBlock(domain, 0);
                this.isometricType1Domains[i][j] = Tools.getIsometricDomainBlock(domain, 1);
                this.isometricType2Domains[i][j] = Tools.getIsometricDomainBlock(domain, 2);
                this.isometricType3Domains[i][j] = Tools.getIsometricDomainBlock(domain, 3);
                this.isometricType4Domains[i][j] = Tools.getIsometricDomainBlock(domain, 4);
                this.isometricType5Domains[i][j] = Tools.getIsometricDomainBlock(domain, 5);
                this.isometricType6Domains[i][j] = Tools.getIsometricDomainBlock(domain, 6);
                this.isometricType7Domains[i][j] = Tools.getIsometricDomainBlock(domain, 7);
            }
        }
    }

    /**
     * get isometric domain block
     *
     * @param i             the y coordinate
     * @param j             the x coordinate
     * @param isometricType the isometric type
     * @return the isometric domain
     */
    public Block getIsometricDomainBlock(int i, int j, int isometricType) {

        return switch (isometricType) {
            case 0 -> this.isometricType0Domains[i][j];
            case 1 -> this.isometricType1Domains[i][j];
            case 2 -> this.isometricType2Domains[i][j];
            case 3 -> this.isometricType3Domains[i][j];
            case 4 -> this.isometricType4Domains[i][j];
            case 5 -> this.isometricType5Domains[i][j];
            case 6 -> this.isometricType6Domains[i][j];
            case 7 -> this.isometricType7Domains[i][j];
            default -> null;
        };
    }


}
