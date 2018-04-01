package ar.edu.untref.imagenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ModifiersTest {

	private Modifiers modifiers;

	@Before
	public void initializer() {
		modifiers = new Modifiers();
	}

	@Test
	public void modifyValueOfAPixel() throws IOException {

		File imageFile = new File("src/test/resources/red-test.png");
		BufferedImage bufferedImage = ImageIO.read(imageFile);
		Image imageOriginal = SwingFXUtils.toFXImage(bufferedImage, null);
		String zero = Integer.toString(0);
		Integer intZero = Integer.valueOf(zero);

		Image modifiedImage = modifiers.modifyValueOfAPixel(imageOriginal, zero, zero, zero);

		Assert.assertEquals(intZero.intValue(), modifiedImage.getPixelReader().getArgb(intZero, intZero));
	}

}