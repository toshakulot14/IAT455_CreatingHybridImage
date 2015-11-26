package ca.iat455.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This class shows four different sets of hybrid images, using different source
 * images with varying combinations of shape and alignment.
 * 
 * @author Melissa Wang
 */
public class HybridResults extends HybridAbstractClass {	
	private static final long serialVersionUID = 1L;
	
	// Constants for output display
	private final static Dimension PANEL_SIZE = new Dimension(1000, 1700);
	private final static int LABEL_Y_OFFSET = 50;
	private final static int IMAGE_X_OFFSET = 10;
	private final static int IMAGE_Y_OFFSET = 40;
	private final static int IMAGES_PER_ROW = 5;

	// Fields for output display
	private JPanel panel;
	private JScrollPane scrollPane;
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public HybridResults() {
		String[] imgNames = {"lion", "tiger"};
		loadImages(imgNames);
		
		for (int i = 0; i < inputImages.size(); i += 2) {
			createHybridImage(inputImages.get(i), inputImages.get(i + 1));
		}
		
//		createHybridImage(imgB1, imgB2); // similar shape, different alignment
//		createHybridImage(imgC1, imgC2); // different shape, similar alignment
//		createHybridImage(imgD1, imgD2); // different shape, different alignment
		
		drawImages();
		setupWindow();
	} // Constructor

	///////////////////////////////////////// Process /////////////////////////////////////////
	
	private void createHybridImage(BufferedImage img1, BufferedImage img2) {
		// Low frequency image
		BufferedImage filteredImg1 = convolve(img1, Filters.LOW_FREQ);
		
		// High frequency image
		BufferedImage filteredImg2a = convolve(img2, Filters.HIGH_FREQ);
		BufferedImage filteredImg2b = grayscale(img2);
		BufferedImage filteredImg2c = dissolve(filteredImg2b, filteredImg2a, 0.5f);
		BufferedImage filteredImg2d = brighten(filteredImg2c, 1.5f);
		
		// Hybrid image
		BufferedImage hybridImg = dissolve(filteredImg1, filteredImg2d, 0.5f);
		
		// Add hybrid image to ArrayList for output display
		for (int i = 0; i < IMAGES_PER_ROW; i++) {
			outputImages.add(hybridImg);
		}
	} // createHybridImage
	
	///////////////////////////////////////// Display /////////////////////////////////////////
	
	private void drawImages() {
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Set image size and position values
                int labelIndex = 0;
                int w = width;
        		int h = height;
        		int x = IMAGE_X_OFFSET;
        		int y = IMAGE_Y_OFFSET;
        		
        		// Set labels
        		Font font = new Font("Verdana", Font.PLAIN, 20);
        		g.setFont(font);
        		String[] labels = {  "Similar shape and alignment",
									 "Similar shape, different alignment",
									 "Different shape, similar alignment",
									 "Different shape and alignment" };

        		for (int i = 0; i < outputImages.size(); i++) {
        			// Draw labels and images
        			g.setColor(Color.BLACK);
        			if (i % 5 == 0) {
        				g.drawString(labels[labelIndex], x, y - (LABEL_Y_OFFSET / 4));
        				labelIndex++;
        			}
        			g.drawImage(outputImages.get(i), x, y, w, h, this);
        			
        			// Set values to next image in row
        			// And make next image half the size of previous image
        			x += w + IMAGE_X_OFFSET;
        			w /= 2;
        			h /= 2;

        			// Reset values to draw next row of images
        			// And draw row-separating line
        			if (i == 4 || i == 9 || i == 14) {
        				w = width;
        				h = height;
        				x = IMAGE_X_OFFSET;
        				y += h + (IMAGE_Y_OFFSET / 2);
        				g.setColor(Color.GRAY);
        				g.drawLine(0, y, getWidth(), y);
        				y += IMAGE_Y_OFFSET;
        			}
        		} // for
            } // paintComponent
        }; // JPanel
	} // drawImages
	
	private void setupWindow() {
		panel.setPreferredSize(PANEL_SIZE);
		scrollPane = new JScrollPane(panel);
		add(scrollPane, BorderLayout.CENTER);
		super.setupWindow("Hybrid Image Comparison");
	} // setupWindow
} // TestHybrid