package com.h3_fractal_image_coder;

/**
 * The details of the range block
 */
public class RangeDetails {
    /**
     * x coordinate of domain related to specific range
     */
    private int xDomain;

    /**
     * y coordinate of domain related to specific range
     */
    private int yDomain;

    /**
     * isometric type
     */
    private int izoType;

    /**
     * scale
     */
    private int scale;

    /**
     * offset
     */
    private int offset;

    /**
     * Constructor
     *
     * @param xDomain x coordinate of domain related to specific range
     * @param yDomain y coordinate of domain related to specific range
     * @param izoType isometric type
     * @param scale   scale
     * @param offset  offset
     */
    public RangeDetails(int xDomain, int yDomain, int izoType, int scale, int offset) {
        this.xDomain = xDomain;
        this.yDomain = yDomain;
        this.izoType = izoType;
        this.scale = scale;
        this.offset = offset;
    }

    /**
     * Constructor
     * @param rangeDetails the range details
     */
    public RangeDetails(RangeDetails rangeDetails){
        this.xDomain= rangeDetails.getXDomain();
        this.yDomain=rangeDetails.getYDomain();
        this.izoType= rangeDetails.getIzoType();
        this.scale= rangeDetails.getScale();
        this.offset=rangeDetails.getOffset();

    }

    /**
     * get x coordinate
     *
     * @return x coordinate
     */
    public int getXDomain() {
        return xDomain;
    }

    /**
     * get y coordinate
     *
     * @return y coordinate
     */
    public int getYDomain() {
        return yDomain;
    }

    /**
     * get izo type
     * @return the izo type
     */
    public int getIzoType() {
        return izoType;
    }

    /**
     * get scale
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * get offset
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }
}
