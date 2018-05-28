package ar.edu.untref.imagenes;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Canny {

    private final static float CIENFLOAT = 100F;
    private final static float MILFLOAT = 1000F;
    private final static int CIENMIL = 100000;
    
    //Contructor
    private BufferedImage imageOriginal;
    private float t1;
    private float t2;
    private float sigma;
    private int gaussianWidth;

    //Filtrado
    private int height;
    private int width;
    private int totalPixels;
    private int[] pixelsLuminance;
    
    //Gradientes
    private int[] magnitude;
    private float[] convolutionX;
    private float[] convolutionY;
    private float[] gradientX;
    private float[] gradientY;

    //Imagen filtrada
    private BufferedImage imageFilter;

    public Canny(Image imageOriginal, float sigma, float t1, float t2, int gaussianWidth) {
        this.imageOriginal = SwingFXUtils.fromFXImage(imageOriginal, null);
        this.sigma = sigma;
        this.t1 = t1;
        this.t2 = t2;
        this.gaussianWidth = gaussianWidth;
    }

    public void filter() {
        width = imageOriginal.getWidth();
        height = imageOriginal.getHeight();
        totalPixels = width * height;
        initializeVectors();
        luminanceRGB();
        calculateGradients(sigma, gaussianWidth);
        int low = Math.round(t1 * CIENFLOAT);
        int high = Math.round( t2 * CIENFLOAT);
        hysteresis(low, high);
        thresholdBorders();
        writeImageBordered(pixelsLuminance);
    }
    
    private void initializeVectors() {
        if (pixelsLuminance == null || totalPixels != pixelsLuminance.length) {
            pixelsLuminance = new int[totalPixels];
            magnitude = new int[totalPixels];
            convolutionX = new float[totalPixels];
            convolutionY = new float[totalPixels];
            gradientX = new float[totalPixels];
            gradientY = new float[totalPixels];
        }
    }
       
    private void luminanceRGB() {
        int[] pixels = (int[]) imageOriginal.getData().getDataElements(0, 0, width, height, null);
        for (int i = 0; i < totalPixels; i++) {
            int p = pixels[i];
            int r = (p & 0xff0000) >> 16;
            int g = (p & 0xff00) >> 8;
            int b = p & 0xff;
            pixelsLuminance[i] = luminance(r, g, b);
        }
    }    
    
    private int luminance(float r, float g, float b) {
        return Math.round(0.299f * r + 0.587f * g + 0.114f * b);
    }    
        
    private void calculateGradients(float sigma, int kernelWidth) {
        
        float kernel[] = new float[kernelWidth];
        float diffKernel[] = new float[kernelWidth];
        int kwidth;
        for (kwidth = 0; kwidth < kernelWidth; kwidth++) {
            float g1 = gaussian(kwidth, sigma);
            if (g1 <= 0.005f && kwidth >= 2) break;
            float g2 = gaussian(kwidth - 0.5f, sigma);
            float g3 = gaussian(kwidth + 0.5f, sigma);
            kernel[kwidth] = (g1 + g2 + g3) / 3f / (2f * (float) Math.PI * sigma * sigma);
            diffKernel[kwidth] = g3 - g2;
        }
        
        int initX = kwidth - 1;
        int maxX = width - (kwidth - 1);
        int initY = width * (kwidth - 1);
        int maxY = width * (height - (kwidth - 1));
        
        
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                int index = x + y;
                float sumX = pixelsLuminance[index] * kernel[0];
                float sumY = sumX;
                int xOffset = 1;
                int yOffset = width;
                for(; xOffset < kwidth ;) {
                    sumY += kernel[xOffset] * (pixelsLuminance[index - yOffset] + pixelsLuminance[index + yOffset]);
                    sumX += kernel[xOffset] * (pixelsLuminance[index - xOffset] + pixelsLuminance[index + xOffset]);
                    yOffset += width;
                    xOffset++;
                }
                
                convolutionY[index] = sumY;
                convolutionX[index] = sumX;
            }
            
        }
        
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                float sum = 0f;
                int index = x + y;
                for (int i = 1; i < kwidth; i++)
                    sum += diffKernel[i] * (convolutionY[index - i] - convolutionY[index + i]);
                
                gradientX[index] = sum;
            }
            
        }
        
        for (int x = kwidth; x < width - kwidth; x++) {
            for (int y = initY; y < maxY; y += width) {
                float sum = 0.0f;
                int index = x + y;
                int yOffset = width;
                for (int i = 1; i < kwidth; i++) {
                    sum += diffKernel[i] * (convolutionX[index - yOffset] - convolutionX[index + yOffset]);
                    yOffset += width;
                }
                
                gradientY[index] = sum;
            }
            
        }
        
        initX = kwidth;
        maxX = width - kwidth;
        initY = width * kwidth;
        maxY = width * (height - kwidth);
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                int index = x + y;
                int indexN = index - width;
                int indexS = index + width;
                int indexW = index - 1;
                int indexE = index + 1;
                int indexNW = indexN - 1;
                int indexNE = indexN + 1;
                int indexSW = indexS - 1;
                int indexSE = indexS + 1;
                
                float xGrad = gradientX[index];
                float yGrad = gradientY[index];
                float gradMag = hypot(xGrad, yGrad);
                
                float nMag = hypot(gradientX[indexN], gradientY[indexN]);
                float sMag = hypot(gradientX[indexS], gradientY[indexS]);
                float wMag = hypot(gradientX[indexW], gradientY[indexW]);
                float eMag = hypot(gradientX[indexE], gradientY[indexE]);
                float neMag = hypot(gradientX[indexNE], gradientY[indexNE]);
                float seMag = hypot(gradientX[indexSE], gradientY[indexSE]);
                float swMag = hypot(gradientX[indexSW], gradientY[indexSW]);
                float nwMag = hypot(gradientX[indexNW], gradientY[indexNW]);
                float tmp;
                
                if (xGrad * yGrad <= (float) 0 
                        ? Math.abs(xGrad) >= Math.abs(yGrad)
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * neMag - (xGrad + yGrad) * eMag)
                        && tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag)
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * neMag - (yGrad + xGrad) * nMag)
                        && tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag)
                        : Math.abs(xGrad) >= Math.abs(yGrad)
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * seMag + (xGrad - yGrad) * eMag)
                        && tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag)
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * seMag + (yGrad - xGrad) * sMag)
                        && tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag)
                        ) {
                    magnitude[index] = gradMag >= MILFLOAT ? CIENMIL : (int) (CIENFLOAT * gradMag);
                    
                } else {
                    magnitude[index] = 0;
                }
            }
        }
    }

    private float gaussian(float x, float sigma) {
        return (float) Math.exp(-(x * x) / (2f * sigma * sigma));
    }
    
    private float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }
    
    private void hysteresis(int low, int high) {
        Arrays.fill(pixelsLuminance, 0);
        int offset = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pixelsLuminance[offset] == 0 && magnitude[offset] >= high) {
                    follow(x, y, offset, low);
                }
                offset++;
            }
        }
    }
    
    private void follow(int x1, int y1, int i1, int threshold) {
        int x0 = x1 == 0 ? x1 : x1 - 1;
        int x2 = x1 == width - 1 ? x1 : x1 + 1;
        int y0 = y1 == 0 ? y1 : y1 - 1;
        int y2 = y1 == height -1 ? y1 : y1 + 1;
        
        pixelsLuminance[i1] = magnitude[i1];
        for (int x = x0; x <= x2; x++) {
            for (int y = y0; y <= y2; y++) {
                int i2 = x + y * width;
                if ((y != y1 || x != x1)
                    && pixelsLuminance[i2] == 0 
                    && magnitude[i2] >= threshold) {
                    follow(x, y, i2, threshold);
                    return;
                }
            }
        }
    }
    
    private void thresholdBorders() {
        for (int i = 0; i < totalPixels; i++) {
            pixelsLuminance[i] = pixelsLuminance[i] > 0 ? -1 : 0xff000000;
        }
    }    
    
    public BufferedImage getImageBordered() {
        return imageFilter;
    }
      
    private void writeImageBordered(int pixels[]) {
        if (imageFilter == null) {
            imageFilter = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        imageFilter.getWritableTile(0, 0).setDataElements(0, 0, width, height, pixels);
    }
 
}