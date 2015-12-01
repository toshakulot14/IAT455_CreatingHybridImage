package ca.iat455.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This class shows three different sets of hybrid images, using different source
 * images with varying combinations of shape and alignment.
 * 
 * @author Melissa Wang & Andy Tang
 */
public class HybridDisplay extends HybridProcess {	
	private static final long serialVersionUID = 1L;
	
	// Constants for output display
	private final static Dimension PANEL_SIZE = new Dimension(1000, 2210);
	private final static int LABEL_Y_OFFSET = 50;
	private final static int IMAGE_X_OFFSET = 10;
	private final static int IMAGE_Y_OFFSET = 60;
	private final static int IMAGES_PER_ROW = 4;
	private final static String[] SOURCE_IMAGE_NAMES = new String[] { "lion", "tiger", "lion", "tiger2", "SFU",
			"tiger" };

	// Fields for output display
	private JPanel panel;
	private JPanel panel2;
	private JScrollPane scrollPane;
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public HybridDisplay() {
		loadImages(SOURCE_IMAGE_NAMES);
		createHybridImages();
		drawImages();
		setupWindow();
	} // Constructor
	
	private void createHybridImages() {
		boolean showProcess = true;
		for (int i = 0; i < inputImages.size(); i += 2) {
			BufferedImage img1 = inputImages.get(i);
			BufferedImage img2 = inputImages.get(i + 1);
			
			if (i > 0) {
				showProcess = false;
			}
			ArrayList<BufferedImage> processImages = createHybridImage(img1, img2, HIGH_PASS, showProcess);
			
			// Add process images
			outputImages.addAll(processImages);
		}
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
        		String[] labels = {  "Similar shape and alignment (Image 1 and 2 Hybrid)",
									 "Similar shape, different alignment (Image 3 and 4 Hybrid)",
									 "Different shape and alignment (Image 5 and 6 Hybrid)" };

        		for (int i = 0; i < outputImages.size(); i++) {
        			// Draw labels and images
        			g.setColor(Color.BLACK);
        			if (i % 4 == 0) {
//        				g.drawString(labels[labelIndex], x, y - (LABEL_Y_OFFSET / 4));
        				labelIndex++;
        			}
        			g.drawImage(outputImages.get(i), x, y, w, h, this);
        			
        			// Set values to next image in row
        			x += w + IMAGE_X_OFFSET;

        			/////////
        			// Reset position values to draw next row of images
        			if (i == 2 || i == 6) {
        				x = IMAGE_X_OFFSET;
        				y += h + IMAGE_Y_OFFSET;
        			}
        			
        			// Make next hybrid image half the size of previous image
        			if (i > 6 && i < 11) {
        				w /= 2;
            			h /= 2;
    				}
        			
        			///////// Hybrid images only

        			if (i == 6) { // Row 3
        				// Reset hybrid image sizes to source image size
        				w = width;
        				h = height;
        			}
        			
    				// Make next hybrid image half the size of previous image
    				if (i > 12 && i < 17 || i > 18) {
        				w /= 2;
            			h /= 2;
    				}
        			
    				// Reset values to start next row
    				// Make source image half its size
        			if (i == 10 || i == 16) {
        				x = IMAGE_X_OFFSET;
        				y += height + IMAGE_Y_OFFSET;
        				w = width / 2;
        				h = height / 2;
        			}
        			
        			// Reset values to start next row
    				// Make hybrid image full source image size
        			if (i == 12 || i == 18) {
        				x = IMAGE_X_OFFSET;
        				y += h + IMAGE_Y_OFFSET;
        				w = width;
        				h = height;
        			}
      			
//        			g.setColor(Color.GRAY);
//    				g.drawLine(0, y, getWidth(), y);
//    				y += IMAGE_Y_OFFSET;
        		} // for
            } // paintComponent
        }; // JPanel
	} // drawImages

	///////////////////////////////////////// Setup /////////////////////////////////////////
	
	protected void loadImages(String[] imgNames) {
		try {

			for (String name : imgNames) {
				File file = new File(name + ".jpg");
				BufferedImage img = ImageIO.read(file);
				inputImages.add(img);
			}
			
			BufferedImage img = inputImages.get(0);
			width = img.getWidth();
			height = img.getHeight();
		} catch (Exception e) {
			System.out.println("Cannot load image");
		}
	} // loadImages
	
	private void setupWindow() {
		panel.setPreferredSize(PANEL_SIZE);
		scrollPane = new JScrollPane(panel);
		add(scrollPane, BorderLayout.CENTER);
		////////
		
		String[] filters = {"Emboss", "Sharpen", "Top Sobel", "Right Sobel"};
		JComboBox<String> filterSelector = new JComboBox<String>(filters);
		filterSelector.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String str = (String) filterSelector.getSelectedItem();
				
				float[] filter = new float[9];
				if(str.equals(filters[0])){
					float[] tmp = {-2, -1, 0, -1, 1, 0, 0, 1, 2};
					filter = tmp;
				} else if(str.equals(filters[1])){
					float[] tmp = {0, -1 ,0, -1, 4, -1, 0, -1, 0};
					filter = tmp;
				} else if(str.equals(filters[2])){
					float[] tmp = {1, 2, 1 , 0, 0 ,0, -1, -2, -1};
					filter = tmp;
				} else if(str.equals(filters[3])){
					float[] tmp = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
					filter = tmp;
				}
				setFilter(filter);

				update();
			
			}//anonymous listener	
		}); //fileSelector
		
		//create browse buttons
		ArrayList<JButton> btnList = new ArrayList<JButton>();
		for(int i=0; i < inputImages.size(); i++){
			btnList.add(createBrowseBtn(inputImages, i));
		}
		
		//create panel to hold buttons
		JPanel btnPanel = new JPanel(new FlowLayout());
		for(int i=0; i < inputImages.size(); i++){
			btnPanel.add(btnList.get(i));
		}		
		
		//create panel for BorderNorth
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(filterSelector, BorderLayout.CENTER);
		panel.add(btnPanel, BorderLayout.NORTH);
		
		add(panel, BorderLayout.NORTH);
		
		//////
		
		panel2 = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            
	            int w = width;
	    		int h = height;
	    		int x = 0;
	    		int y = 0;
	
	    		//labels
	    		Font font = new Font("Verdana", Font.PLAIN, 20);
        		g.setFont(font);
        		String[] labels = {  "Image 1",
									 "Image 2",
									 "Image 3",
									 "Image 4",
									 "Image 5",
									 "Image 6"};
	    		
	    		for (int i = 0; i < inputImages.size(); i++) {
		    		g.setColor(Color.BLACK);
		    		
	    			// Draw labels and images
	    			int init = 20;
	    			g.drawImage(inputImages.get(i), x, y+init+(i*20)+5, w/3, h/3, this);
	    			g.drawString(labels[i], x, y+init+(i*20));

	    			// draw line
	    			if(i%2 == 1){
		    			g.setColor(Color.GRAY);
		    			g.drawLine(0, y+init+(i*20)+10+h/3, getWidth(), y+init+(i*20)+10+h/3);	    				
	    			}
	    			y += h/3 + 10;
	    		} //for
			} // paintComponent	
			
		};
		panel2.setPreferredSize(new Dimension(width/3+10, 1250));
		JScrollPane scrollPane2 = new JScrollPane(panel2);
		add(scrollPane2, BorderLayout.EAST);
		
		setTitle("Hybrid Images");
		setMinimumSize(new Dimension(1300, 700));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	} // setupWindow

	
	// TODO: MUST THROW ERROR WHEN INCORRECT IMG DIMENSIONS
	private JButton createBrowseBtn(ArrayList<BufferedImage> list, int inputImagesIndex) {
		JButton browseBtn = new JButton("Image " + (inputImagesIndex+1));
		
		browseBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int result = fc.showOpenDialog(null);
				
				if (result == JFileChooser.APPROVE_OPTION) {
				    File file = fc.getSelectedFile(); 
				    try {
						BufferedImage newInput = ImageIO.read(file);
						
						if(newInput.getWidth() == width || newInput.getHeight() == height){
							list.set(inputImagesIndex, newInput);	//bind input to button
							
							String msg = file.getName() + " read successfully";
							JOptionPane.showMessageDialog(null, msg);
							update();							
						} else {
							String msg = "Dimensions of the image do not match. Images must be " + height + "x" + width;
							JOptionPane.showMessageDialog(null, msg);
						}
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error reading image.");
						e1.printStackTrace();
					}
				}//end if

			}//end anonymous listener
		});
		
		return browseBtn;
	} // createBrowseBtn
	
	private void update(){
		outputImages = new ArrayList<BufferedImage>();	//reset output images
		createHybridImages();
		panel.revalidate();
		panel.repaint();
		panel2.revalidate();
		panel2.repaint();
	}
}