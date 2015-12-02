package ca.iat455.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This class shows the differences in effective blurring between Java's
 * ConvolveOp and Kernel, and our custom convolve functions.
 * 
 * @author Melissa Wang & Andy Tang
 */
public class HybridCustomConvolve extends HybridAbstractClass {	
	private static final long serialVersionUID = 1L;
	
	// Constants for output display
	private final static Dimension PANEL_SIZE = new Dimension(1000, 870);
	private final static int LABEL_Y_OFFSET = 50;
	private final static int IMAGE_X_OFFSET = 10;
	private final static int IMAGE_Y_OFFSET = 60;
	private final static String[] SOURCE_IMAGE_NAMES = new String[] { "lion", "wolf", "lion", "wolf" };

	// Fields for output display
	private JPanel panel;
	private JScrollPane scrollPane;
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public HybridCustomConvolve() {
		loadImages(SOURCE_IMAGE_NAMES);
		createHybridImages();
		drawImages();
		setupWindow();
	} // Constructor
	
	@Override
	protected void createHybridImages() {
		// Java convolve
		BufferedImage img1 = inputImages.get(0);
		BufferedImage img2 = inputImages.get(1);
		ArrayList<BufferedImage> processImages = createHybridImage(img1, img2, high_pass, 0.5f, false, false, true);
		outputImages.addAll(processImages);
		
		// Custom convolve
		BufferedImage img3 = inputImages.get(2);
		BufferedImage img4 = inputImages.get(3);
		processImages = createHybridImage(img3, img4, high_pass, 0.5f, false, true, true);
		outputImages.addAll(processImages);
	} // createHybridImages
		
	private void drawImages() {
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Set image size and position values
                int labelIndex = 0;
                int w = width / 2;
        		int h = height / 2;
        		int x = IMAGE_X_OFFSET;
        		int y = IMAGE_Y_OFFSET;
        		
        		// Set labels
        		Font font = new Font("Verdana", Font.PLAIN, 20);
        		g.setFont(font);
        		String[] labels = {  "Java ConvolveOp() & Kernel()",
        							 "Custom convolve()"};

        		for (int i = 0; i < outputImages.size(); i++) {
        			g.setColor(Color.BLACK);
        			if (i % 4 == 0) {
        				g.drawString(labels[labelIndex], x, y - (LABEL_Y_OFFSET / 4));
        				labelIndex++;
        			}
        			g.drawImage(outputImages.get(i), x, y, w, h, this);
        			
        			// Set position to next image in row
        			x += w + IMAGE_X_OFFSET;
    				
    				// Reset values to draw next row of images
        			// And draw row-separating line
        			if (i == 2) {
        				w = width / 2;
        				h = height / 2;
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

	///////////////////////////////////////// Setup /////////////////////////////////////////
	
	private void setupWindow() {
		panel.setPreferredSize(PANEL_SIZE);
		scrollPane = new JScrollPane(panel);
		add(scrollPane, BorderLayout.CENTER);
		super.setupWindow("Convolve Function Comparison");
	} // setupWindow
}