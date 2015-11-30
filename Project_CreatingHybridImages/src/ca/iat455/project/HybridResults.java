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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
	private JPanel panel;
	private JPanel panel2;
	private JScrollPane scrollPane;
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public HybridResults() {
		loadImages(new String[]{"lion", "tiger", "lion", "tiger2", "SFU", "tiger"}, true);
		createHybridImages();
		drawImages();
		setupWindow();
	} // Constructor
	
	private void createHybridImages() {
		for (int i = 0; i < inputImages.size(); i += 2) {
			BufferedImage hybridImg = createHybridImage(inputImages.get(i), inputImages.get(i + 1), false, HIGH_PASS).get(0);
			
			// Add hybrid image to ArrayList for output display
			for (int j = 0; j < IMAGES_PER_ROW; j++) {
				outputImages.add(hybridImg);
			}
			
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
        			g.setColor(Color.BLACK);
        			if (i % 4 == 0) {
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
        			if (i == 3 || i == 7) {
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
        		String[] labels = {  "Image 0",
									 "Image 1",
									 "Image 2",
									 "Image 3",
									 "Image 4",
									 "Image 5"};
	    		
	    		for (int i = 0; i < inputImages.size(); i++) {
	    			// Draw labels and images
	    			int init = 20;
	    			g.drawImage(inputImages.get(i), x, y+init+(i*20)+5, w/3, h/3, this);
	    			g.drawString(labels[i], x, y+init+(i*20));

	    			y += h/3 + 10;
	    		} //for
			} // paintComponent	
		};
		panel2.setPreferredSize(new Dimension(width/3+10, 1250));
		JScrollPane scrollPane2 = new JScrollPane(panel2);
		
		add(scrollPane2, BorderLayout.EAST);
		super.setupWindow("Hybrid Image Comparison");
	} // setupWindow

	
	//TODO: MUST THROW ERROR WHEN INCORRECT IMG DIMENSIONS
	private JButton createBrowseBtn(ArrayList<BufferedImage> list, int inputImagesIndex) {
		JButton browseBtn = new JButton("Image " + inputImagesIndex);
		
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
							String msg = "Dimensions of the image do not match. Images must be " + width + "x" + height;
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
	}
	
	
	private void update(){
		outputImages = new ArrayList<BufferedImage>();	//reset output images
		createHybridImages();
		panel.revalidate();
		panel.repaint();
	}
}