package ca.iat455.project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class shows the process of producing a hybrid image.
 * 
 * @author Melissa Wang
 */
public class HybridProcess extends HybridAbstractClass {
	private static final long serialVersionUID = 1L;
	
	private final static int IMAGE_X_OFFSET = 25;
	private final static int IMAGE_Y_OFFSET = 40;
	private final static int LABEL_Y_OFFSET = 5;

	public HybridProcess() {
		String[] imgNames = {"elephant", "jaguar", "filteredElephant", "filteredJaguar", "hybrid"};
		loadImages(imgNames, false);
		createHybridImages();
		super.setupWindow("Hybrid Image Creation Process");
	} // Constructor

	private void createHybridImages() {
		// Add input images for output display
		for (BufferedImage img : inputImages) {
			outputImages.add(img);
		}
		
		// Attempt #1
		BufferedImage img1 = inputImages.get(0);
		BufferedImage img2 = inputImages.get(1);
		ArrayList<BufferedImage> processImages = super.createHybridImage(img1, img2, true);
		outputImages.addAll(processImages);
		
		// Attempt #2
		BufferedImage filteredImg1 = convolve(img1, Filters.LOW_FREQ);
		BufferedImage filteredImg2 = grayscale(img2);
		BufferedImage hybridImg = dissolve(filteredImg1, filteredImg2, 0.5f);
		outputImages.add(filteredImg1);
		outputImages.add(filteredImg2);
		outputImages.add(hybridImg);
	} // createHybridImages

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
				"Original hybrid", "Blur", "Sobel filter", "Grayscale", "Dissolve (Sobel + gryscl)", "Brightened", "Hybrid 1",
				"Blur", "Grayscale", "Hybrid 2" };

		for (int i = 0; i < outputImages.size(); i++) {
			// Draw labels and images
			g.setColor(Color.BLACK);
			g.drawString(labels[i], x, y - LABEL_Y_OFFSET);
			g.drawImage(outputImages.get(i), x, y, w, h, this);
			
			// Set values to next image in row
			x += w + IMAGE_X_OFFSET;

			// Reset values to draw next row of images
			if (i == 4 || i == 10) {
				x = IMAGE_X_OFFSET;
				y += h + IMAGE_Y_OFFSET / 2;
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, y, getWidth(), y);
				y += IMAGE_Y_OFFSET / 2;
			}
		}
	} // paint
} // HybridProcess
