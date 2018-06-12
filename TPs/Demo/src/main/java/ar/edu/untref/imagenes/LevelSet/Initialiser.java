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

        // init = autoThreshold(im, "Default");//esta es toda la imagen
        init = initFromRoi(imp); // falla xq viene imp.roi en null, sin seleccion
        return init;

        /*
         * if (method == null) { init = autoThreshold(im, "Otsu"); }
         * 
         * if (method == useRoiStr) { init = initFromRoi(imp); } else if
         * (method.startsWith(imagePrefix)) { init =
         * initFromBinaryImage(method.substring(imagePrefix.length())); } else { init =
         * autoThreshold(im, method); }
         * 
         * return init;
         */
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
     * Create a binary image using {@link ij.process.AutoThresholder
     * AutoThresholder}
     * 
     * @param im
     *            The image to be thresholded (will be duplicated)
     * @param method
     *            The name of the method from AutoThresholder
     */
    private static BinaryProcessor autoThreshold(ImageProcessor im, String method) {
        AutoThresholder thresholder = new AutoThresholder();
        int threshold = thresholder.getThreshold(method, im.getHistogram());

        ImageProcessor init = im.duplicate();
        init.threshold(threshold);
        // init.convertToByte(false);
        // return new BinaryProcessor((ByteProcessor)init);
        ImageProcessor dd = init.convertToByte(true);
        ByteProcessor init2 = dd.convertToByteProcessor();
        return new BinaryProcessor(init2);
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

        // roi and mask: roi is the bounding box for the mask
        // roi only: rectangular roi

        if (roi == null) {
            // IJ.error("No ROI found");
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

    /**
     * Create an initialisation image from an existing ImageJ window
     * 
     * @param imageTitle:
     *            The title of the ImageJ window holding a single (non-stack) binary
     *            image
     * @return The binary initialisation
     * @todo Consistent handling of binary images
     */
    private static BinaryProcessor initFromBinaryImage(String imageTitle) {
        ImagePlus imp = WindowManager.getImage(imageTitle);

        if (imp == null || imp.getImageStackSize() != 1 || !imp.getProcessor().isBinary()) {
            // IJ.error("Initialisation image must be a single binary image");
            return null;
        }

        ImageProcessor im = imp.getProcessor();
        if (imp.getType() != ImagePlus.GRAY8) {
            im = im.convertToByte(false);
        }

        return new BinaryProcessor((ByteProcessor) im);
    }

}
