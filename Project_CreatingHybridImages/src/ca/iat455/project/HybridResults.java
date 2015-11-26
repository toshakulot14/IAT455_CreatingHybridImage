package ca.iat455.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class HybridResults extends JFrame {
	// Constants for output display
	private final static int LABEL_Y_OFFSET = 50;
	private final static int IMAGE_X_OFFSET = 10;
	private final static int IMAGE_Y_OFFSET = 40;
	private final static int IMAGES_PER_ROW = 5;
	private final static String[] labels = { "Similar shape and alignment",
											 "Similar shape, different alignment",
											 "Different shape, similar alignment",
											 "Different shape and alignment" };

	// Source images for hybrid image process
	private BufferedImage imgA1;
	private BufferedImage imgA2;
	private BufferedImage imgB1;
	private BufferedImage imgB2;
	private BufferedImage imgC1;
	private BufferedImage imgC2;
	private BufferedImage imgD1;
	private BufferedImage imgD2;

	// Fields for output display
	private ArrayList<BufferedImage> hybridImgs = new ArrayList<BufferedImage>();
	private int width, height;
	private JPanel panel;
	private JScrollPane scrollPane;
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public HybridResults() {
		loadImages();
		createHybridImage(imgA1, imgA2); // similar shape, similar alignment
		createHybridImage(imgA1, imgA2); // similar shape, different alignment
		createHybridImage(imgA1, imgA2); // different shape, similar alignment
		createHybridImage(imgA1, imgA2); // different shape, different alignment
		
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
		
		// Hybrid image
		BufferedImage hybridImg = brighten(dissolve(filteredImg1, filteredImg2c, 0.5f), 1.5f);
		
		// Add hybrid image to ArrayList for output display
		for (int i = 0; i < IMAGES_PER_ROW; i++) {
			hybridImgs.add(hybridImg);
		}
	} // createHybridImage
	
	private BufferedImage convolve(BufferedImage img, Filters filter) {
		WritableRaster wRaster = img.copyData(null);
		BufferedImage result = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);

		// Determine kernel size
		int kernelSize;
		switch (filter) {
		case LOW_FREQ:
			kernelSize = 7;
			break;
		case HIGH_FREQ:
			kernelSize = 3;
			break;
		default:
			kernelSize = 0;
			break;
		} // switch

		// Apply spatial convolution to image
		int edge = kernelSize / 2;
		for (int x = edge; x < result.getWidth() - edge; x++) {
			for (int y = edge; y < result.getHeight() - edge; y++) {
				ArrayList<Integer> rgbs = new ArrayList<Integer>();

				switch (filter) {
				case LOW_FREQ:
					int[] indicesBlur = { x-3, x-2, x-1, x, x+1, x+2, x+3,
										  y-3, y-2, y-1, y, y+1, y+2, y+3 };
					rgbs = getRGBs(img, rgbs, indicesBlur);
					break;
				case HIGH_FREQ:
					int[] indicesEdge = { x-1, x, x+1,
										  y-1, y, y+1 };
					rgbs = getRGBs(img, rgbs, indicesEdge);
					break;
				default:
					break;
				} // switch

				result.setRGB(x, y, computeRGB(rgbs, filter));
			}
		}
		return result;
	} // convolve

	private ArrayList<Integer> getRGBs(BufferedImage img, ArrayList<Integer> rgbs, int[] indices) {
		// Determine the boundaries between x and y in the array parameter
		int xBegin = 0;
		int xEnd = indices.length / 2 - 1;
		int yBegin = indices.length / 2;
		int yEnd = indices.length - 1;

		// Get the RGB values of the kernel's pixels
		for (int x = xBegin; x <= xEnd; x++) {
			for (int y = yBegin; y <= yEnd; y++) {
				rgbs.add(img.getRGB(indices[x], indices[y]));
			}
		}

		return rgbs;
	} // getRGBs

	private int computeRGB(ArrayList<Integer> rgbs, Filters filter) {
		ArrayList<Integer> reds = new ArrayList<Integer>();
		ArrayList<Integer> greens = new ArrayList<Integer>();
		ArrayList<Integer> blues = new ArrayList<Integer>();

		// Separate all of the kernel pixels' channels
		for (int i = 0; i < rgbs.size(); i++) {
			int rgb = rgbs.get(i);
			reds.add(getRed(rgb));
			greens.add(getGreen(rgb));
			blues.add(getBlue(rgb));
		}

		// Apply filter to each channel
		int r = convolveChannel(reds, filter);
		int g = convolveChannel(greens, filter);
		int b = convolveChannel(blues, filter);

		return new Color(r, g, b).getRGB();
	} // computeRGB

	private int convolveChannel(ArrayList<Integer> channel, Filters filter) {
		int c = 0;

		switch (filter) {
		case LOW_FREQ:
			// Blur: Average the pixels
			for (int i = 0; i < channel.size(); i++) {
				c += channel.get(i);
			}
			return c / channel.size();
		case HIGH_FREQ:
			// Gaussian derivative? Multiply kernel with Sobel (edge-emphasizing) filter
			int[] sobelFilter = { 1, 2, 1, 0, 0, -0, -1, -2, -1 };
			for (int i = 0; i < channel.size(); i++) {
				c += channel.get(i) * sobelFilter[i];
			}
			return clip(c);
		default:
			return 0;
		}
	} // convolveChannel

	private BufferedImage grayscale(BufferedImage img) {
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				int rgb = img.getRGB(x, y);
				float[] hsv = new float[3];
				Color.RGBtoHSB(getRed(rgb), getGreen(rgb), getBlue(rgb), hsv);
				result.setRGB(x, y, Color.HSBtoRGB(0, 0, hsv[2]));
			}
		}

		return result;
	} // grayscale

	private BufferedImage dissolve(BufferedImage imgA, BufferedImage imgB, float mixVal) {
		WritableRaster wRaster = imgA.copyData(null);
		BufferedImage result = new BufferedImage(imgA.getColorModel(), wRaster, imgA.isAlphaPremultiplied(), null);

		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				int rgbA = imgA.getRGB(x, y);
				int rgbB = imgB.getRGB(x, y);

				// O = (MV * A) + [(1 – MV) * B]
				int r = clip((int) ((mixVal * getRed(rgbA)) + (1 - mixVal) * getRed(rgbB)));
				int g = clip((int) ((mixVal * getGreen(rgbA)) + (1 - mixVal) * getGreen(rgbB)));
				int b = clip((int) ((mixVal * getBlue(rgbA)) + (1 - mixVal) * getBlue(rgbB)));

				result.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}

		return result;
	} // dissolve
	
	public BufferedImage brighten(BufferedImage img, float factor) {
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

		for (int i = 0; i < result.getWidth(); i++) {
			for (int j = 0; j < result.getHeight(); j++) {
				int rgb = img.getRGB(i, j);
				int r = clip((int)(getRed(rgb) * factor));
				int g = clip((int)(getGreen(rgb) * factor));
				int b = clip((int)(getBlue(rgb) * factor));
				result.setRGB(i, j, new Color(r, g, b).getRGB());
			}
		}

		return result;
	} // brighten
	
	///////////////////////////////////////// Helper /////////////////////////////////////////

	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	private int getRed(int rgb) {
		return new Color(rgb).getRed();
	}

	private int getGreen(int rgb) {
		return new Color(rgb).getGreen();
	}

	private int getBlue(int rgb) {
		return new Color(rgb).getBlue();
	}
	
	///////////////////////////////////////// Setup /////////////////////////////////////////

	private void loadImages() {
		try {
			imgA1 = ImageIO.read(new File("lion.jpg"));
			imgA2 = ImageIO.read(new File("tiger.jpg"));

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}

		width = imgA1.getWidth();
		height = imgA1.getHeight();
	} // loadImages

	private void setupWindow() {
		// JPanel and JScrollPane
        scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);
		
        // JFrame
		setTitle("Hybrid Images");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	} // setupWindow
	
	///////////////////////////////////////// Draw /////////////////////////////////////////
	
	private void drawImages() {
		panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Initialize image size and position values, and font
                int labelIndex = 0;
                int w = width;
        		int h = height;
        		int x = IMAGE_X_OFFSET;
        		int y = IMAGE_Y_OFFSET;
        		Font font = new Font("Verdana", Font.PLAIN, 20);
        		g.setFont(font);

        		for (int i = 0; i < hybridImgs.size(); i++) {
        			// Draw labels and images
        			g.setColor(Color.BLACK);
        			if (i % 5 == 0) {
        				g.drawString(labels[labelIndex], x, y - (LABEL_Y_OFFSET / 4));
        				labelIndex++;
        			}
        			g.drawImage(hybridImgs.get(i), x, y, w, h, this);
        			
        			// Set values to next image in row
        			// And make next image half the size of previous image
        			x += w + IMAGE_X_OFFSET;
        			w /= 2;
        			h /= 2;

        			// Reset values to draw next row of images
        			// And draw row-separating line
        			if (i == 4 || i == 9 || i == 14 || i == 19) {
        				w = width;
        				h = height;
        				x = IMAGE_X_OFFSET;
        				y += h + (IMAGE_Y_OFFSET / 2);
        				g.setColor(Color.GRAY);
        				g.drawLine(0, y, getWidth(), y);
        				y += IMAGE_Y_OFFSET;
        			} // if
        		} // for
            } // paintComponent
        }; // JPanel
	} // drawImages
	
	///////////////////////////////////////// Main /////////////////////////////////////////

	public static void main(String[] args) {
		HybridResults hybridDisplay = new HybridResults();
		hybridDisplay.repaint();
	}
} // TestHybrid
