package ca.iat455.project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * This class shows the process of producing a hybrid image.
 * 
 * @author Melissa Wang
 */
public class HybridProcess extends HybridClass {
	private static final long serialVersionUID = 1L;
	
	private final static int IMAGE_X_OFFSET = 25;
	private final static int IMAGE_Y_OFFSET = 40;
	private final static int LABEL_Y_OFFSET = 5;

	private BufferedImage imgA;
	private BufferedImage imgB;
	private BufferedImage origFilteredImgA;
	private BufferedImage origFilteredImgB;
	private BufferedImage origHybridImg;

	public HybridProcess() {
		loadImages();

		BufferedImage filteredImgA = convolve(imgA, Filters.LOW_FREQ);
		BufferedImage filteredImgB1 = convolve(imgB, Filters.HIGH_FREQ);
		BufferedImage filteredImgB2 = grayscale(imgB);
		BufferedImage filteredImgB3 = dissolve(filteredImgB2, filteredImgB1, 0.5f);
		BufferedImage hybridImg = brighten(dissolve(filteredImgA, filteredImgB3, 0.5f), 1.5f);
		BufferedImage hybridImg2 = dissolve(filteredImgA, filteredImgB2, 0.5f);
		
		// Row 1
		images.add(imgA);
		images.add(imgB);

		// Row 2
		images.add(origFilteredImgA);
		images.add(origFilteredImgB);
		images.add(origHybridImg);

		// Row 3
		images.add(filteredImgA);
		images.add(filteredImgB1);
		images.add(filteredImgB2);
		images.add(filteredImgB3);
		images.add(hybridImg);
		
		// Row 4
		images.add(hybridImg2);

		setupWindow();
	} // Constructor
	
	///////////////////////////////////////// Setup /////////////////////////////////////////

	private void loadImages() {
		try {
			imgA = ImageIO.read(new File("elephant.png"));
			imgB = ImageIO.read(new File("jaguar.png"));
			origFilteredImgA = ImageIO.read(new File("filteredElephant.png"));
			origFilteredImgB = ImageIO.read(new File("filteredJaguar.png"));
			origHybridImg = ImageIO.read(new File("hybrid.png"));
		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}

		width = imgA.getWidth();
		height = imgA.getHeight();
	}

	private void setupWindow() {
		super.setupWindow("Hybrid Image Creation Process");
	}

	///////////////////////////////////////// Display /////////////////////////////////////////

	public void paint(Graphics g) {
		super.paint(g);
		
		// Set image size and position values
		int w = width;
		int h = height;
		int x = IMAGE_X_OFFSET;
		int y = IMAGE_Y_OFFSET + LABEL_Y_OFFSET;

		// Set labels
		Font font = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(font);
		String[] labels = {
				"Source1", "Source 2", "Original filter 1", "Original filter 2",
				"Original hybrid", "Blur", "Sobel filter", "Grayscale", "Dissolve (Sobel + gryscl)", "Hybrid (& brightened)",
				"Hybrid 2 (w/ gryscl)" };

		for (int i = 0; i < images.size(); i++) {
			// Draw labels and images
			g.setColor(Color.BLACK);
			g.drawString(labels[i], x, y - LABEL_Y_OFFSET);
			g.drawImage(images.get(i), x, y, w, h, this);
			
			// Set values to next image in row
			x += w + IMAGE_X_OFFSET;

			// Reset values to draw next row of images
			if (i == 1 || i == 4 || i == 9) {
				x = IMAGE_X_OFFSET;
				y += h + IMAGE_Y_OFFSET / 2;
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, y, getWidth(), y);
				y += IMAGE_Y_OFFSET / 2;
			}
		}
	} // paint
} // HybridProcess
