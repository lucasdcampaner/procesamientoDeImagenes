package ar.edu.untref.imagenes.LevelSet;

import java.util.LinkedList;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.process.AutoThresholder;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Handles the creation of an initialisation for the level set.
 */
public class Initialiser {
    /**
     * The string representing the use ROI method
     */
    private static final String useRoiStr = "Use ROI";

    /**
     * The string used to prefix image titles
     */
    private static final String imagePrefix = "Image: ";

    /**
     * Create an initialisation image
     * 
     * @param imp
     *            The ImagePlus object holding the image to be thresholded (needed
     *            because ROIs seem to be attached to this)
     * @param im
     *            The image to be thresholded (will be duplicated)
     * @param method
     *            The name of the method from AutoThresholder
     */
    public static BinaryProcessor getInitialisation(ImagePlus imp, ImageProcessor im, String method) {
        BinaryProcessor init;

        init = initFromRoi(imp);
        return init;
    }

    /**
     * Get the list of supported initialisation methods
     * 
     * @return a list of the names of initialisation/thresholding methods
     */
    public static String[] getInitialisationMethods() {
        LinkedList<String> methods = new LinkedList<String>();
        methods.add(useRoiStr);
        for (String s : AutoThresholder.getMethods()) {
            methods.add(s);
        }

        for (String s : getOpenImageTitles()) {
            methods.add(imagePrefix + s);
        }

        return methods.toArray(new String[0]);
    }

    /**
     * Create an initialisation image from an ROI
     * 
     * @param imp:
     *            The image plus object (for some reason selected ROIs seem to be
     *            attached to the ImagePlus instead of the ImageProcessor)
     * @return The binary initialisation
     */
    private static BinaryProcessor initFromRoi(ImagePlus imp) {
        ByteProcessor init = new ByteProcessor(imp.getWidth(), imp.getHeight());
        ImageProcessor mask = imp.getMask();
        Roi roi = imp.getRoi();

        if (roi == null) {
            return null;
        }

        java.awt.Rectangle rect = roi.getBounds();

        if (mask == null) {
            for (int x = 0; x < rect.width; ++x) {
                for (int y = 0; y < rect.height; ++y) {
                    init.set(x + rect.x, y + rect.y, 255);
                }
            }
        } else {
            for (int x = 0; x < rect.width; ++x) {
                for (int y = 0; y < rect.height; ++y) {
                    init.set(x + rect.x, y + rect.y, mask.get(x, y));
                }
            }
        }

        return new BinaryProcessor(init);
    }

    /**
     * Get a list of open images that can be used as an initialisation
     * 
     * @return an array of image titles
     */
    private static String[] getOpenImageTitles() {
        int[] windowIds = WindowManager.getIDList();
        String[] titles = new String[windowIds.length];

        for (int i = 0; i < windowIds.length; ++i) {
            titles[i] = WindowManager.getImage(windowIds[i]).getTitle();
        }

        return titles;
    }
}
