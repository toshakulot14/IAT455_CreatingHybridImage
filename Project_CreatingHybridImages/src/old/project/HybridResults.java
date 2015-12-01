package old.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class shows three different sets of hybrid images, using different source
 * images with varying combinations of shape and alignment.
 * 
 * @author Melissa Wang
 */
public class HybridResults extends HybridAbstractClass {	
	private static final long serialVersionUID = 1L;
	
	// Constants for output display
	private final static Dimension PANEL_SIZE = new Dimension(1000, 1250);
	private final static int LABEL_Y_OFFSET = 50;
	private final static int IMAGE_X_OFFSET = 10;
	private final static int IMAGE_Y_OFFSET = 40;
	private final static int IMAGES_PER_ROW = 4;

	// Fields for output display
	private JPanel demoPanel;
	private JScrollPane demoScrollPane;
	private boolean isUserInput = false;
	
	// Fields for menu
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu = new JMenu("File");
	private ActionListener menuListener;
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public HybridResults() {
		loadImages(new String[]{"lion", "tiger", "lion", "tiger2", "SFU", "tiger"}, true);
		createHybridImages();
		drawImages();
		setupWindow();
		setupMenu();
		setupMenuListener();
	} // Constructor
	
	private void createHybridImages() {
		for (int i = 0; i < inputImages.size(); i += 2) {
			BufferedImage hybridImg = createHybridImage(inputImages.get(i), inputImages.get(i + 1), false).get(0);
			
			// Add hybrid image to ArrayList for output display
			for (int j = 0; j < IMAGES_PER_ROW; j++) {
				outputImages.add(hybridImg);
			}
		}
	} // createHybridImages
		
	private void drawImages() {
		demoPanel = new JPanel() {
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
									 "Different shape and alignment" };

        		for (int i = 0; i < outputImages.size(); i++) {
        			// Draw labels and images
        			if (!isUserInput) {
	        			g.setColor(Color.BLACK);
	        			if (i % 4 == 0) {
	        				g.drawString(labels[labelIndex], x, y - (LABEL_Y_OFFSET / 4));
	        				labelIndex++;
	        			}
        			}
        			g.drawImage(outputImages.get(i), x, y, w, h, this);
        			
        			// Set values to next image in row
        			// And make next image half the size of previous image
        			x += w + IMAGE_X_OFFSET;
        			
        			if (!isUserInput) {
        				w /= 2;
        				h /= 2;
        				
        				// Reset values to draw next row of images
            			// And draw row-separating line
            			if (i == 3 || i == 7) {
            				w = width;
            				h = height;
            				x = IMAGE_X_OFFSET;
            				y += h + (IMAGE_Y_OFFSET / 2);
            				g.setColor(Color.GRAY);
            				g.drawLine(0, y, getWidth(), y);
            				y += IMAGE_Y_OFFSET;
            			}
        			} else {
        				if (i % 2 == 0) {
        					x = IMAGE_X_OFFSET;
        					y += h + IMAGE_Y_OFFSET;
        				}
        			} // if
        		} // for
        		
        		revalidate();
				repaint();
            } // paintComponent
        }; // JPanel
	} // drawImages
	
	///////////////////////////////////////// Window Setup /////////////////////////////////////////
	
	private void setupWindow() {
		demoPanel.setPreferredSize(PANEL_SIZE);
		demoScrollPane = new JScrollPane(demoPanel);
		add(demoScrollPane, BorderLayout.CENTER);
		super.setupWindow("Hybrid Image Comparison");
	}
	
	///////////////////////////////////////// Menu Setup /////////////////////////////////////////
	
	private void setupMenu() {
		addMenuItem("Open...", "Select two images from your computer to produce your own hybrid image");
		addMenuItem("Exit", "Exit application");
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}
	
	private void addMenuItem(String name, String tooltip) {
		JMenuItem item = new JMenuItem(name);
		item.setToolTipText(tooltip);
		menu.add(item);
	}
	
	private void setupMenuListener() {
		menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Open...")) {
					processUserImages();
				} else if (e.getActionCommand().equals("Exit")) {
					System.exit(0);
				}
			} // actionperformed
		}; // menuListener
		
		menu.setMnemonic(KeyEvent.VK_F);
		for (int i = 0; i < menu.getItemCount(); i++) {
			JMenuItem item = menu.getItem(i);
			if (item.getText().equals("Open..."))
				item.setMnemonic(KeyEvent.VK_O);
			else
				item.setMnemonic(KeyEvent.VK_E);
			item.addActionListener(menuListener);
		} // for
	} // setupMenuListener
	
	///////////////////////////////////////// User Image Selection/Processing /////////////////////////////////////////
	
	private BufferedImage selectImage(String title) {
		BufferedImage img = null;

		// Setup chooser
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		chooser.setDialogTitle(title);
		
		// Setup image filter
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
		chooser.setFileFilter(filter);
		
		int returnVal = chooser.showOpenDialog(this);
		File file = null;

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}

		try {
			img = ImageIO.read(file);
		} catch (Exception e) {
			System.out.println("Cannot retrieve image");
		}

		return img;
	} // selectImage
	
	private void processUserImages() {
		BufferedImage img1, img2;
		
		img1 = selectImage("Select first source image");
		if (img1 != null) {
			img2 = selectImage("Select second source image");
			
			if (img2 != null) {
				// Produce hybrid image if user's source images are the same size
				if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
					isUserInput = true;
					
					// Adjust image sizes for display
					width = img1.getWidth() / 3;
					height = img1.getHeight() / 3;
					
					ArrayList<BufferedImage> processImages = createHybridImage(img1, img2, true);
					outputImages.clear();
					outputImages.add(img1);
					outputImages.add(img2);
					outputImages.addAll(processImages);
					
					// Update the window
					drawImages();
				} else {
					JOptionPane.showMessageDialog(null,
							"Selected images are not the same size. Please try again.");
				}
			} // if img2 != null
		} // if img1 != null
	} // processUserImages
} // TestHybrid
