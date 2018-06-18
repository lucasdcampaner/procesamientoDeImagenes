package ar.edu.untref.imagenes.sift;

import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.BasicMatcher;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.MatchingUtilities;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.model.fit.RANSAC;

public class Sift {

    private static int iteraciones = 1500;
    private static Double limite = 5.0;
    private static Double porcentaje = 0.5;
    private static int limiteMatches = 8;

    public void aplicar(BufferedImage img1, BufferedImage img2) throws Exception {

        MBFImage query = ImageUtilities.createMBFImage(img1, true);
        MBFImage target = ImageUtilities.createMBFImage(img2, true);

        DoGSIFTEngine engine = new DoGSIFTEngine();

        // encuentra los puntos caracteristicos de la imagen 1
        LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
        // encuentra los puntos caracteristicos de la imagen 2
        LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());

        LocalFeatureMatcher<Keypoint> matcher = new BasicMatcher<Keypoint>(75);

        matcher.setModelFeatures(queryKeypoints);
        matcher.findMatches(targetKeypoints);

        RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(limite, iteraciones,
                new RANSAC.PercentageInliersStoppingCondition(porcentaje));
        matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(new FastBasicKeypointMatcher<Keypoint>(limiteMatches),
                modelFitter);

        matcher.setModelFeatures(queryKeypoints);
        matcher.findMatches(targetKeypoints);

        MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.BLUE);

        JOptionPane.showMessageDialog(null,
                "Descriptores de la imagen 1" /* + img1.getName() */ + ": " + String.valueOf(queryKeypoints.size()) + "\n"
                        + "Descriptores de la imagen 2 " /* + img2.getName() */ + ": "
                        + String.valueOf(targetKeypoints.size()) + "\n" + "Coincidencias entre descriptores: "
                        + String.valueOf(matcher.getMatches().size()));
        DisplayUtilities.display(consistentMatches);
    }
    public void ingresarValores(int cantidadIteraciones, double limiteEstimador, double porcentajeParada,
            int limiteComparacion) {
        iteraciones = cantidadIteraciones;
        limite = limiteEstimador;
        porcentaje = porcentajeParada;
        limiteMatches = limiteComparacion;
    }

}
