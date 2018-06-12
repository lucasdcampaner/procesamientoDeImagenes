package ar.edu.untref.imagenes.LevelSet;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.BinaryProcessor;
import ij.process.ImageProcessor;

public class LevelContours {

    /**
     * The image to be segmented
     */
    protected ImagePlus imp;

    /**
     * The initialisation image
     */
    protected ImagePlus impInit = null;

    /**
     * The segmentation image
     */
    protected ImagePlus impSeg = null;

    /**
     * The window for displaying intermediate level set results
     */
    LevelSetListDisplay lsDisplay = null;

    /**
     * Maximum number of iterations
     */
    protected int countIteration = 10;

    private List<ar.edu.untref.imagenes.LevelSet.Point> lin = new ArrayList<>();
    private List<ar.edu.untref.imagenes.LevelSet.Point> lout = new ArrayList<>();

    public ImagePlus getImpSeg() {
        return impSeg;
    }

    public LevelContours(ImagePlus imp, Point point1, Point point2) {
        imp.setRoi(point1.x, point1.y, point2.x - point1.x, point2.y - point1.y);// x, y, width, height
        this.imp = imp;
    }

    /**
     * All plugin parameters
     */
    public static class Parameters {
        /**
         * The speed field method
         */
        public String sfmethod;

        /**
         * The initialisation method
         */
        public String initMethod;

        /**
         * Should subsequent frames be initialised from the previous segmentation (true) or independently using the
         * specified method (false)
         */
        public boolean initFromPrevious;

        /**
         * Should the initialisation for each slice be displayed?
         */
        public boolean displayInit;

        /**
         * Should each iteration of the level set be plotted?
         */
        public boolean plotProgress;

        /**
         * Fast level set parameters
         */
        public FastLevelSet.Parameters lsparams;

        /**
         * Set parameters to defaults
         */
        public Parameters(int countIteration) {
            sfmethod = null;
            initMethod = null;
            initFromPrevious = true;
            displayInit = false;
            plotProgress = true;

            lsparams = new FastLevelSet.Parameters();
            lsparams.speedIterations = 5;
            lsparams.smoothIterations = 2;
            lsparams.maxIterations = countIteration;
            lsparams.gaussWidth = 7;
            lsparams.gaussSigma = 3;
        }
    }

    public void run(ImageProcessor ip, int countIteration) {
        ImageStack stack = imp.getStack();
        Parameters params = new Parameters(countIteration);

        try {
            int stackSize = stack.getSize();
            BinaryProcessor prevSeg = null;

            for (int i = 1; i <= stackSize; ++i) {

                ImageProcessor im = stack.getProcessor(i);

                BinaryProcessor init;
                if (params.initFromPrevious && prevSeg != null) {
                    init = prevSeg;
                } else {
                    init = Initialiser.getInitialisation(imp, im, params.initMethod);
                }

                BinaryProcessor seg = levelset(params, im, init);
                prevSeg = seg;
            }
        } catch (Error e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Run the fast level set
     * 
     * @param params
     *            The fast level set parameters
     * @param im
     *            The image to be segmented
     * @param init
     *            The binary initialisation
     * @param hsfp
     *            The hybrid speed field parameters (may be null)
     * @params addparams Additional algorithm/plugin parameters
     * @return The binary segmentation
     */
    protected BinaryProcessor levelset(Parameters params, ImageProcessor im, BinaryProcessor init) {
        assert params != null;
        assert im != null;
        assert init != null;

        SpeedField speed = SpeedFieldFactory.create(params.sfmethod, im, init); 

        FastLevelSet fls = new FastLevelSet(params.lsparams, im, init, speed);

        boolean b = fls.segment();

        if (!b) {
            return null;
        }

        lin = fls.getLIn();
        lout = fls.getLOut();
        return fls.getSegmentation();
    }

    public List<ar.edu.untref.imagenes.LevelSet.Point> getLin() {
        return lin;
    }

    public List<ar.edu.untref.imagenes.LevelSet.Point> getLout() {
        return lout;
    }
}