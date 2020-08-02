import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.ArrayList;
import java.awt.event.*;

public class View extends JFrame {
	public static final int WIDTH = 1280;//width, user input screen
	public static final int HEIGHT = 800;//height, user input screen 
	public static final int LABELHEIGHT = 16;//label height
	public static final int LABELWIDTH = 400;//label height
	public static final int X_INDENT = 20;//indent spacing in links column
	public static final int LINKS_MAX = (LABELWIDTH - 2 * X_INDENT) / (LABELHEIGHT/2);//max num of links in links column
	public static final int X_SPACING = LABELHEIGHT/2;//horizontal spacing between links 
	public static final int X_OFFSET = LABELHEIGHT/2 + 1;//offset value helps to create non overlapping links
	
	public static final int H1 = (int)(HEIGHT*0.25);
	public static final int H2 = (int)(HEIGHT*0.15);
	public static final int H3 = (int)(HEIGHT*0.00);
	public static final int H4 = (int)(HEIGHT*0.5);
	
	//settings panel objects
	protected JLabel menulabel1;
	protected JLabel menulabel2;
	
	String [] items = { "3rd edition (ISBN-10: 0534359450)", "4th edition (ISBN-10: 0495391328)"};
	JComboBox<String> combobox1 = new JComboBox<>(items);
	JComboBox<String> combobox2 = new JComboBox<>(items);
	
	protected JPanel settingsPanel = new JPanel();//hold user settings
	
	protected JLabel sliderlabel1;
	protected JLabel sliderlabel2;
	protected JSlider slider1;
	protected JSlider slider2;
	protected JButton comparebutton;
	
	//results: panels and objects;
	protected JPanel summaryPanel = new JPanel();
	protected JPanel displayPanel = new JPanel();//holds labels and links
	protected JPanel outerPanel = new JPanel();//holds scrollbar, inner panel
	protected JLayeredPaneCustom innerPanel;//replaces displayPanel, holds labels and links 

	protected JScrollPane scrollbar;//holds printpanel
	
	protected ArrayList<JLabel> newbooklabels = new ArrayList<JLabel>();//keep storage of title labels
	protected ArrayList<JLabel> oldbooklabels = new ArrayList<JLabel>();//keep storage of title labels

	//protected int[][] new_to_old_indices;//stores pairs of (new, old) indices for title labels
	protected Point[][] pair;//hold pairs of points, do not initialize here because size unknown 
	
	//summary statistics labels
	protected JLabel chapternum = new JLabel("Total chapter #, new/old (%): ");
	protected JLabel subsectionnum = new JLabel("Total subsection #, new/old (%): ");
	protected JLabel pagenum = new JLabel("Total page #, new/old (%): (in progress)");
	protected JLabel similarityvalue1 = new JLabel("(User set) Similarity level 1(%): ");
	protected JLabel similarityvalue2 = new JLabel("(User set) Similarity level 2(%): ");
	protected JLabel sectionmatch = new JLabel("Sections meeting similarity level (%): ");
	protected JLabel compositescore = new JLabel("COMPOSITE SCORE (%): ", SwingConstants.CENTER);//average of available numbers
	//colour coded legends, 2 so far, 1 in progress
	protected JLabel greenlegend = new JLabel("Minor changes in content, Similarity level 1(%): ");
	protected JLabel yellowlegend = new JLabel("Some changes in content, Similarity level 2(%): ");
	protected JLabel redlegend = new JLabel("Major changes in content/new content");

	protected JLabel newbookname = new JLabel("NEW EDITION", SwingConstants.CENTER);
	protected JLabel oldbookname = new JLabel("OLD EDITION", SwingConstants.CENTER);
	//protected GridBagConstraints c; //constraints for gridbag layout summary panel
	
	/**
	 * default constructor run on first call
	 */
	public View(){
		//set resultview window properties
		setTitle("View");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 0, WIDTH, HEIGHT);
		getContentPane().setLayout(null);
		
		//set settingspanel properties
		settingsPanel.setBounds(0, 0, WIDTH-40, H1);
		settingsPanel.setBackground(new Color(245, 245, 220));
		settingsPanel.setLayout(new GridBagLayout());
		addSettings();
		getContentPane().add(settingsPanel);//summary panel added to window
		
		this.setVisible(true);//set window visible
		
		//assign booklabels vector to input parameters
				//this.newbooklabels = newbook;
				//this.oldbooklabels = oldbook;
	}
	
	public void updateView(ArrayList<JLabel> newbook, ArrayList<JLabel> oldbook, int chapternum_change_num, int sectionnum_change_num, 
			int similaritylevel1, int similaritylevel2, int totalsection_match_num, int compositescore_num,int[][] matchedindices) {
		
		//need to check for existing components; if present, clear then add new components
		
		//assign booklabels vector to input parameters
		this.newbooklabels = newbook;
		this.oldbooklabels = oldbook;
		
		//set summarypanel properties
		summaryPanel.setBounds(0, H1, WIDTH-40, H2);
		summaryPanel.setBackground(new Color(245, 245, 220));
		summaryPanel.setLayout(new GridBagLayout());	
		addSummaryLabels(chapternum_change_num, sectionnum_change_num, 
				similaritylevel1, similaritylevel2, totalsection_match_num, compositescore_num);//add labels to summary
		getContentPane().add(summaryPanel);//summary panel added to window
		
		//initialize outerpanel to hold scrollbar and innerpanel
		outerPanel.setBounds(0, H1+H2+H3, WIDTH-40, H4);
		outerPanel.setBackground(SystemColor.inactiveCaption);
		outerPanel.setLayout( new BorderLayout());
		getContentPane().add(outerPanel);
		
		//initialize inner panel
		innerPanel = new JLayeredPaneCustom( newbooklabels, oldbooklabels, matchedindices );
		displayPanel.add(innerPanel);
		
		//add scrollbar
		scrollbar = new JScrollPane(displayPanel);
		scrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outerPanel.add(scrollbar);
		this.getContentPane().add(outerPanel);
		//System.out.println(this.oldbooklabels.get(0).getLocation());
		//System.out.println( getRightMidpointOfLabel(this.newbooklabels.get(0)));
		
		this.revalidate();//refreseh JFrame
	}//end of updateview

	/**
	 * Create the frame.
	 */
	public View(ArrayList<JLabel> newbook, ArrayList<JLabel> oldbook, int chapternum_change_num, int sectionnum_change_num, 
			int similaritylevel1, int similaritylevel2, int totalsection_match_num, int compositescore_num,int[][] matchedindices) {
		//set resultview window properties
		setTitle("Resultview");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 0, WIDTH, HEIGHT);
		getContentPane().setLayout(null);
		
		//assign booklabels vector to input parameters
		this.newbooklabels = newbook;
		this.oldbooklabels = oldbook;
		
		//set settingspanel properties
		settingsPanel.setBounds(0, 0, WIDTH-40, H1);
		settingsPanel.setBackground(new Color(245, 245, 220));
		settingsPanel.setLayout(new GridBagLayout());
		addSettings();
		getContentPane().add(settingsPanel);//summary panel added to window
		
		//set summarypanel properties
		summaryPanel.setBounds(0, H1, WIDTH-40, H2);
		summaryPanel.setBackground(new Color(245, 245, 220));
		summaryPanel.setLayout(new GridBagLayout());	
		addSummaryLabels(chapternum_change_num, sectionnum_change_num, 
				similaritylevel1, similaritylevel2, totalsection_match_num, compositescore_num);//add labels to summary
		getContentPane().add(summaryPanel);//summary panel added to window
		
		//initialize outerpanel to hold scrollbar and innerpanel
		outerPanel.setBounds(0, H1+H2+H3, WIDTH-40, H4);
		outerPanel.setBackground(SystemColor.inactiveCaption);
		outerPanel.setLayout( new BorderLayout());
		getContentPane().add(outerPanel);
		
		//initialize inner panel
		innerPanel = new JLayeredPaneCustom( newbooklabels, oldbooklabels, matchedindices );
		displayPanel.add(innerPanel);
		
		//add scrollbar
		scrollbar = new JScrollPane(displayPanel);
		scrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outerPanel.add(scrollbar);
		this.getContentPane().add(outerPanel);
		//System.out.println(this.oldbooklabels.get(0).getLocation());
		//System.out.println( getRightMidpointOfLabel(this.newbooklabels.get(0)));
		
	}//end of result view

	
	public void addSettings(){
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		menulabel1 = new JLabel("Select current edition");
		menulabel1.setFont(new Font("Tahoma", Font.PLAIN, LABELHEIGHT));
		menulabel1.setPreferredSize(new Dimension(400, LABELHEIGHT));
		
		menulabel2 = new JLabel("Select reference edition");
		menulabel2.setFont(new Font("Tahoma", Font.PLAIN, LABELHEIGHT));
		menulabel2.setPreferredSize(new Dimension(400, LABELHEIGHT));
		
		combobox1.setEditable(false);
		combobox1.setPreferredSize(new Dimension(400, LABELHEIGHT));
		
		combobox2.setEditable(false);
		combobox2.setPreferredSize(new Dimension(400, LABELHEIGHT));
		
		sliderlabel1 = new JLabel("Set SIMILARITY THRESHOLD high (%)");
		sliderlabel1.setFont(new Font("Tahoma", Font.PLAIN, LABELHEIGHT));
		sliderlabel1.setPreferredSize(new Dimension(400, LABELHEIGHT));
				
		slider1 = new JSlider();
		slider1.setMajorTickSpacing(10);
		slider1.setMinorTickSpacing(1);
		slider1.setValue(65);
		slider1.setSnapToTicks(true);
		slider1.setPaintTicks(true);
		slider1.setPaintLabels(true);
		slider1.setPreferredSize(new Dimension(300, 50));
		
		sliderlabel2 = new JLabel("Set SIMILARITY THRESHOLD low (%)");
		sliderlabel2.setFont(new Font("Tahoma", Font.PLAIN, LABELHEIGHT));
		sliderlabel2.setPreferredSize(new Dimension(400, LABELHEIGHT));
		
		slider2 = new JSlider();
		slider2.setMajorTickSpacing(10);
		slider2.setMinorTickSpacing(1);
		slider2.setValue(50);
		slider2.setSnapToTicks(true);
		slider2.setPaintTicks(true);
		slider2.setPaintLabels(true);
		slider2.setPreferredSize(new Dimension(300, 50));
		
		comparebutton = new JButton("Click to COMPARE contents");
		comparebutton.setFont(new Font("Tahoma", Font.PLAIN, LABELHEIGHT));
		comparebutton.setPreferredSize(new Dimension(200, LABELHEIGHT));
		
		//add to settingspanel
		//column1
		c.gridx = 0;
		c.gridy = 0;
		settingsPanel.add(menulabel1, c); 
		
		c.gridx = 0;
		c.gridy = 1;
		settingsPanel.add(combobox1, c); 
		
		c.gridx = 0;
		c.gridy = 2;
		settingsPanel.add(sliderlabel1, c);
		
		c.gridx = 0;
		c.gridy = 3;
		settingsPanel.add(slider1, c);
		
		c.gridx = 0;
		c.gridy = 4;
		settingsPanel.add(sliderlabel2, c);
		
		c.gridx = 0;
		c.gridy = 5;
		settingsPanel.add(slider2, c);

		//column3
		c.gridx = 2;
		c.gridy = 0;
		settingsPanel.add(menulabel2, c); 
		
		c.gridx = 2;
		c.gridy = 1;
		settingsPanel.add(combobox2, c); 
		
		c.gridx = 2;
		c.gridy = 5;
		settingsPanel.add(comparebutton, c); 
		
		
		//set slider2 constraint when slider1 change
		slider1.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent e) {
		        if( slider1.getValue() < slider2.getValue() && slider1.getValue() > 0 ){
		        	slider2.setValue( slider1.getValue() - 1 );
		        }//end of if
		      }
		});
		
		//set slider1 constraint when slider2 change
		slider2.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent e) {
		        if( slider1.getValue() < slider2.getValue() && slider1.getValue() < 100 ){
		        	slider1.setValue( slider2.getValue() + 1 );
		        }//end of if
		      }
		});
	}
	
	/**
	 * add action listeners to: comparebutton
	 * @param listener
	 */
	public void addEventListener(ActionListener listener){
		comparebutton.addActionListener(listener);
	}
	
	/**
	 * add summary statistics to summary
	 * @param c GridBagConstraints
	 */
	public void addSummaryLabels(int chapternum_change_num, int sectionnum_change_num, 
			int similaritylevel1, int similaritylevel2, int totalsection_match_num, int compositescore_num){
		//update setting numbers in labels
		chapternum.setText(chapternum.getText() + chapternum_change_num);
		subsectionnum.setText(subsectionnum.getText() + sectionnum_change_num);
		similarityvalue1.setText(similarityvalue1.getText() + similaritylevel1);
		similarityvalue2.setText(similarityvalue2.getText() + similaritylevel2);
		sectionmatch.setText(sectionmatch.getText() + totalsection_match_num);
		compositescore.setText(compositescore.getText() + compositescore_num);
		compositescore.setForeground(Color.BLUE);
		greenlegend.setText(greenlegend.getText() + similaritylevel1);
		yellowlegend.setText(yellowlegend.getText() + similaritylevel2);
		
		chapternum.setPreferredSize(new Dimension(400, 20));
		subsectionnum.setPreferredSize(new Dimension(400, 20));
		pagenum.setPreferredSize(new Dimension(400, 20));
		newbookname.setPreferredSize(new Dimension(400, 20));
		
		similarityvalue1.setPreferredSize(new Dimension(400, 20));
		similarityvalue2.setPreferredSize(new Dimension(400, 20));
		sectionmatch.setPreferredSize(new Dimension(400, 20));
		compositescore.setPreferredSize(new Dimension(400, 20));
		
		greenlegend.setPreferredSize(new Dimension(400, 20));
		yellowlegend.setPreferredSize(new Dimension(400, 20));
		redlegend.setPreferredSize(new Dimension(400, 20));
		oldbookname.setPreferredSize(new Dimension(400, 20));
		
		//set legend labels color
		greenlegend.setBackground(Color.GREEN);
		yellowlegend.setBackground(Color.YELLOW);
		redlegend.setBackground(Color.RED);
		greenlegend.setOpaque(true);
		yellowlegend.setOpaque(true);
		redlegend.setOpaque(true);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.5;	
		
		//add labels to summarypanel
		//column1
		c.gridx = 0;
		c.gridy = 0;
		summaryPanel.add(chapternum, c);
		
		c.gridx = 0;
		c.gridy = 1;
		summaryPanel.add(subsectionnum, c);
		
		c.gridx = 0;
		c.gridy = 2;
		summaryPanel.add(pagenum, c);
		
		c.gridx = 0;
		c.gridy = 3;
		summaryPanel.add(newbookname, c);
		//column2
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		summaryPanel.add(similarityvalue1, c);
		
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 1;
		summaryPanel.add(similarityvalue2, c);
		
		c.gridx = 1;
		c.gridy = 2;
		summaryPanel.add(sectionmatch, c);
		
		c.gridx = 1;
		c.gridy = 3;
		summaryPanel.add(compositescore, c);
		//column3
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = 0;
		summaryPanel.add(greenlegend, c);
		
		c.gridx = 2;
		c.gridy = 1;
		summaryPanel.add(yellowlegend, c);
		
		c.gridx = 2;
		c.gridy = 2;
		summaryPanel.add(redlegend, c);
		
		c.gridx = 2;
		c.gridy = 3;
		summaryPanel.add(oldbookname, c);
	}
	
	//class for drawing links between labels
	public class JLayeredPaneCustom extends JLayeredPane{
		protected int[][] new_to_old_indices;//stores pairs of (new, old) indices for title labels
		protected Point[] newlabels_points;//initialize at runtime
		protected Point[] oldlabels_points;//initialize at runtime
		protected ArrayList<Integer> newchapter_indices = new ArrayList<Integer>();
		protected ArrayList<Integer> x_reset_indices = new ArrayList<Integer>();
		
		public JLayeredPaneCustom( ArrayList<JLabel> newbooklabels, ArrayList<JLabel> oldbooklabels, int[][] matchedindices ){
			this.new_to_old_indices = matchedindices;
			//determine height of innerpanel
			int maxlabels;
			if( newbooklabels.size() > oldbooklabels.size() ){
				maxlabels = newbooklabels.size();
			}
			else{
				maxlabels = oldbooklabels.size();
			}
			
			this.setBackground(Color.WHITE);
			this.setLayout(null);
			this.setPreferredSize(new Dimension( 3 * LABELWIDTH, maxlabels * LABELHEIGHT  ));
			
			//initialize points array size
			this.newlabels_points = new Point[ newbooklabels.size() ];
			this.oldlabels_points = new Point[ oldbooklabels.size() ];
			//new labels
			for(int i = 0; i < newbooklabels.size(); i++){//add labels to panel
				newbooklabels.get(i).setOpaque(true);
				newbooklabels.get(i).setBounds(0, i * LABELHEIGHT, LABELWIDTH, LABELHEIGHT);
				this.add( newbooklabels.get(i), new Integer(0));
				this.newlabels_points[i] = getRightMidpointOfLabel( newbooklabels.get(i) );//get midpoint and save
				if( newbooklabels.get(i).getBorder()!=null){//label has border, indicates chapter label
					newchapter_indices.add(i);//add index to chapter index
				}	
				//System.out.println( this.newlabels_points[i] );
			}//end of for loop i
			//System.out.println( newchapter_indices.size() );
			
			//old labels
			for(int i = 0; i < oldbooklabels.size(); i++){//add labels to panel
				oldbooklabels.get(i).setOpaque(true);
				oldbooklabels.get(i).setBounds( 2 * LABELWIDTH, i * LABELHEIGHT, LABELWIDTH, LABELHEIGHT);
				this.add(oldbooklabels.get(i), new Integer(0));
				this.oldlabels_points[i] = getLeftMidpointOfLabel( oldbooklabels.get(i) );//get midpoint and save
				//System.out.println( this.oldlabels_points[i] );
			}//end of for loop i 
			
			//initialize reset points for x spacing in links, check matched indices and keep count of matched indices
			int count = 0;//keep count of links 
			for(int i = 0; i < newchapter_indices.size(); i++){
				if( newchapter_indices.get(i) - count  >= LINKS_MAX){
					x_reset_indices.add( newchapter_indices.get(i - 1) );//save index of previous chapter
					count = newchapter_indices.get(i - 1);
					System.out.print( newchapter_indices.get(i - 1) + " " );
				}
			}
			for( int i = 0; i < x_reset_indices.size(); i++){
				System.out.print( x_reset_indices.get(i) + " " );
			}
			
			System.out.println(" " );
			
		}//end of constructor
		
		public void paintComponent(Graphics g){
			int x_current = LABELWIDTH + X_INDENT;//distance from right edges of newlabels
			//int y_current = 0;//not in use
			int x_reset_count = 0;
			float colorValue = 0;//current spectrum color value
			float colorValueIncrement = (float) 1.0 / ( newchapter_indices.size() - 1) ;//spectrum color increment
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor( Color.getHSBColor(colorValue, 1, 1) );
			g2.setStroke(new BasicStroke(2));
			for(int i = 0; i < this.new_to_old_indices.length; i++){//iterate through all labels 
				//set x_spacing reset
				if( i == x_reset_indices.get(x_reset_count)){
					//System.out.println(i);
					x_current = LABELWIDTH + X_INDENT + (x_reset_count + 1) * X_OFFSET;//reset x to start
					
					if( x_reset_count < x_reset_indices.size() - 1 ){//if not all x reset points have been counted, increment
						x_reset_count++;//increment to next reset point
					}//end of inner if
				}//end of outer it
				
				if( newbooklabels.get(i).getBorder() != null ){//chapter label reached, set new colour
					g2.setColor( Color.getHSBColor(colorValue, 1, 1) );
					if(colorValue < 1){
						colorValue += colorValueIncrement;//increment hue value
					}
				}
				
				int new_index = this.new_to_old_indices[i][0];//get new index
				int old_index = this.new_to_old_indices[i][1];//get old index
				
				if(new_index > -1 && old_index > -1){//check for valid range
					System.out.println( i + " " + this.newlabels_points[new_index] + " " + this.oldlabels_points[old_index]);
					Point new_point = this.newlabels_points[new_index];//temp point ref
					Point old_point = this.oldlabels_points[old_index];//temp point ref
					
					//set line style based on label colour, line for green, dashed for orange
					if( newbooklabels.get(i).getBackground() == Color.YELLOW){
						  float dash[] = { 10.0f };
						g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
						        BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
					}else{
						g2.setStroke(new BasicStroke(2));
					}
					//draw link in 3 segments
					if(new_point.y > old_point.y){//new point position below old point in (+y) downward axis, start drawing from left
						//System.out.println( "new point is lower than old" );
						g2.drawLine(new_point.x, new_point.y, x_current, new_point.y);//segment1, offset x
						g2.drawLine(x_current, new_point.y, x_current, old_point.y);//segment2, offset y
						g2.drawLine(x_current, old_point.y, old_point.x, old_point.y);//segment3, offset x
					}
					else{//new point position above old point in (+y) downward axis, start draw from right 
						//System.out.println( "new point is higher or equal to old" );
						int temp_x_current = old_point.x - ( x_current - LABELWIDTH );//temp x_current in opposite direction
						g2.drawLine(new_point.x, new_point.y, temp_x_current, new_point.y);//segment1, offset x
						g2.drawLine(temp_x_current, new_point.y, temp_x_current, old_point.y);//segment2, offset y
						g2.drawLine(temp_x_current, old_point.y, old_point.x, old_point.y);//segment3, offset x
					}
					
					x_current += X_SPACING ;//increment x coordinate
				}//end of if
			}//end of for loop i
		}
	}
	
	/**
	 * return right midpoint of label
	 * @param thelabel
	 * @return p point
	 */
	public Point getRightMidpointOfLabel(JLabel thelabel){
		Point p = thelabel.getLocation();
		p.translate(LABELWIDTH, LABELHEIGHT / 2);
		return p;
	}
	
	/**
	 * return left midpoint of label
	 * @param thelabel
	 * @return p point
	 */
	public Point getLeftMidpointOfLabel(JLabel thelabel){
		Point p = thelabel.getLocation();
		p.translate(0, LABELHEIGHT / 2);
		return p;
	}
}//end of resultview
