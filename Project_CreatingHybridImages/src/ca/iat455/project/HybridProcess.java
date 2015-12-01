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
		String[] imgNames = {"lion", "wolf"};
		loadImages(imgNames);
		createHybridImages();
		super.setupWindow("Hybrid Image Creation Process");
	} // Constructor

	@Override
	protected void createHybridImages() {		
		BufferedImage img1 = inputImages.get(0);
		BufferedImage img2 = inputImages.get(1);
		ArrayList<BufferedImage> processImages = super.createHybridImage(img1, img2, HIGH_PASS, 0.5f, true, false, false);
		outputImages.addAll(processImages);
	} // createHybridImages

	public void paint(Graphics g) {
		super.paint(g);
		
		// Set image size and position values
		int w = width / 2;
		int h = height / 2;
		int x = IMAGE_X_OFFSET;
		int y = IMAGE_Y_OFFSET + LABEL_Y_OFFSET * 3;

		// Set main labels
		Font font = new Font("Verdana", Font.PLAIN, 20);
		g.setFont(font);
		g.setColor(Color.BLACK);
		String[] mainLabels = {"Low-frequency Image", "High-frequency Image", "Hybrid Image"};
		for (String label : mainLabels) {
			g.drawString(label, x, y - LABEL_Y_OFFSET);
			y += h + IMAGE_Y_OFFSET * 2 - LABEL_Y_OFFSET * 4;
		}
		
		// Set image size and position values
		x = IMAGE_X_OFFSET;
		y = IMAGE_Y_OFFSET + LABEL_Y_OFFSET * 6;
		
		// Set step labels
		font = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(font);
		String[] stepLabels = {
				"Source A", "1. Grayscale", "2. Blur",
				"Source B", "3. Grayscale", "4. Edge detection", "5. Dissolve steps 3 & 4",
				"6. Dissolve steps 2 & 5"};

		for (int i = 0; i < outputImages.size(); i++) {
			// Draw labels and images
			g.setColor(Color.BLACK);
//			g.drawString(stepLabels[i], x, y - LABEL_Y_OFFSET);
			g.drawImage(outputImages.get(i), x, y, w, h, this);
			
			// Set values to next image in row
			x += w + IMAGE_X_OFFSET;

			// Reset values to draw next row of images
			if (i == 2 || i == 6) {
				x = IMAGE_X_OFFSET;
				y += h + IMAGE_Y_OFFSET / 2;
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, y, getWidth(), y);
				y += IMAGE_Y_OFFSET;
			}
		}
	} // paint
} // HybridProcess
