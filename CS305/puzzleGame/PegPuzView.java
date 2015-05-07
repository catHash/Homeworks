package puzzleGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics; 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;

import javax.swing.JPanel;

public class PegPuzView extends JPanel implements ComponentListener {
 private PegPuzModel model; // game model
 protected int isMobOfMummiesGame;
 protected int selectedPeg; // peg location with green selection box around it
 protected FontMetrics fontMetrics; // allows calc of string width and height
 protected int[][] xyLookUpByID; // int[id][0] = x,int[id][1] = y  lookup table holds x, y pixel locations based upon ID
 protected int gridUnit = 21; // graphics grid based upon this unit
 protected boolean showIds = true;
 protected int[][] boardSolidLines = { // used to draw solid lines between spots by ID
   {1, 3}, {4,6}, {7, 13}, {14, 20}, // horizontal
   {21, 27}, {28, 30}, {31, 33}, 
   {7, 21}, {8, 22}, {1, 31}, {2,32}, // vertical
   {3,33}, {12, 26}, {13, 27} };
 protected static Color mummyColor = Color.ORANGE;
 protected static Color holeColor = Color.BLACK;
 protected static Color holeColorDawgHouse = Color.BLUE;
 protected static Color pegColor = Color.RED;
 protected static Color highlightedCol = Color.GREEN;
 protected static  Color lineColor= Color.BLUE;
 protected static final Color idColor = Color.WHITE;//color of board numbers
 protected static final Color bgColor = Color.WHITE;
 protected static final Color bgBoardColor= Color.GRAY;
 protected static final int NUM_SPOTS_ACROSS = 7; // standard peg puzzle board
 protected static final int NUM_PEG_LOC = 33; // standard peg puzzle board
 protected static final int MAX_UNITS = NUM_SPOTS_ACROSS*4;
 protected static final Dimension DEF_PREFER_SIZE = new Dimension(600,600);// preferred location Dimension(width,height)
 protected static final int NOTHING = 0; // indicates no object at queried location

 public void setModel(PegPuzModel model){
   this.model = model;
 }
 public void setMobOfMummies(boolean isMobOfMummies){
	 this.isMobOfMummiesGame = isMobOfMummies ? 1:0;
	 if (isMobOfMummiesGame == 1){
		 pegColor = new Color(255,69,0);//Orange-Red
		 lineColor= Color.BLACK;
	 }
	 else{
		 pegColor = Color.RED;
		 lineColor= Color.BLUE;
	 }
 }
 public PegPuzView(PegPuzModel theModel) {
  model = theModel; // model to visualize
  xyLookUpByID = new int[NUM_PEG_LOC+1][];
  for (int i=0; i<=NUM_PEG_LOC; i++) {
   xyLookUpByID[i] = new int[2]; // x,y
  }
  xyLookUpByID[0][0] = NOTHING;
  xyLookUpByID[0][1] = NOTHING;
 }
 
 public void setShowIds(boolean b) {
  showIds = b;
 }
 
 /**
  * Redraws the board based upon current model data
  * @param g Graphic to use for drawing
 */
 public void paintComponent(Graphics g){
  super.paintComponent(g);
  Graphics2D g2 = (Graphics2D) g;
  Color savedColor = g2.getColor();
  Stroke savedStroke = g2.getStroke();
  if (isMobOfMummiesGame == 1){
	  drawBoardBackgroundMobOfMummies(g2);
	  drawBoardLines(g2);
	  drawBoardSpots(g2);
	  drawPegsRemaining(g2);
  }else{
	  drawBoardBackground(g2);
	  drawBoardLines(g2);
	  drawBoardSpots(g2);
	  drawPegsRemaining(g2);
  }
  g2.setStroke(savedStroke);
  g2.setColor(savedColor); 
 }
 
  /**
  * Draws the count of pegs remaining
  */
 protected void drawPegsRemaining(Graphics2D g2) {
  int x = gridUnit*2;
  int y = gridUnit*2;
  if (fontMetrics != null) {
   int strWid = fontMetrics.stringWidth(new Integer(model.getPegsRemaining()).toString());
   int strHt = fontMetrics.getHeight();
   g2.setColor(idColor);
   if (gridUnit > 17)
     g2.drawString("Pegs Remaining: " + new Integer(model.getPegsRemaining()).toString(), x,y);
   else if (gridUnit < 9);
   else
     g2.drawString("Pegs: " + new Integer(model.getPegsRemaining()).toString(), x,y);
  }
 }

 /**
  * Draws the background upon which the other board components
  * are placed. Uses precomputed x,y s to define geometry
  * @param g2 Graphics2D to use for drawing
  */
	protected void drawBoardBackground(Graphics2D g2) {
		g2.setColor(bgBoardColor);
		int xMin = xyLookUpByID[7][0] - 2 * gridUnit;
		int yMin = xyLookUpByID[1][1] - 2 * gridUnit;
		g2.fillRoundRect(xMin, yMin, MAX_UNITS * gridUnit,
				MAX_UNITS * gridUnit, gridUnit, gridUnit);
	}

	protected void drawBoardBackgroundMobOfMummies(Graphics2D g2) {
		int xMin,yMin,width,height;
		//xyLookUpByID[SPOT NUMBER][0-X,1-Y] - 2 * gridUnit
		//GRAY SPOT
		g2.setColor(Color.GRAY);
		xMin = xyLookUpByID[7][0] - 2 * gridUnit;
		yMin = xyLookUpByID[7][1] - 2 * gridUnit;
		width = gridUnit * 28;
		height = gridUnit * 20;
		g2.fillRoundRect(xMin, yMin, width, height, gridUnit, gridUnit);
		//MAROON SPOT
		g2.setColor(new Color(128, 0, 0));
		xMin = xyLookUpByID[4][0] - 2 * gridUnit;
		yMin = xyLookUpByID[3][1] - 2 * gridUnit;
		width = gridUnit * 12;
		height = gridUnit * 12;
		g2.fillRoundRect(xMin, yMin, width, height, gridUnit, gridUnit);
		
		//System.out.println(Arrays.deepToString(xyLookUpByID));
		//System.out.println("PegPuzView.drawBoardBackground: gridUnit: "
		//		+ gridUnit);
		//System.out.println("PegPuzView.drawBoardBackground: xMin: " + xMin
		//		+ " yMin: " + yMin + " width: " + width + " height: " + height);
		 
	}

 /**
  * Draws the board lines. Uses precomputed x,y s to define geometry
  * @param g2 Graphics2D to use for drawing
  */
 protected void drawBoardLines(Graphics2D g2) {
  int thickness = gridUnit/8; // scales line thickness
  g2.setColor(lineColor);
  g2.setStroke(new BasicStroke(thickness));
  int loc1, loc2;
  for (int i=0; i<boardSolidLines.length; i++) {
   loc1 = boardSolidLines[i][0];
   loc2 = boardSolidLines[i][1];
   g2.drawLine(xyLookUpByID[loc1][0], xyLookUpByID[loc1][1],
     xyLookUpByID[loc2][0], xyLookUpByID[loc2][1]);
  }
 }
 
 /**
  * Computes the center x,y of all board locations. Should be
  * updated whenever the component's size changes. The locations
  * are used when drawing and also for hit detection.
  * @param w width of component in pixels
  * @param h height of component in pixels
  * @param ppgu pixels per grid unit (units used to size board)
  * @param xyByID lookup table indexed by id, used to store loc centers' x,y
  */
 public void computeSpotLoc(int w, int h, int ppgu) {
  int id =0;
  int boardPixDiam = MAX_UNITS* ppgu;
  int xOffset = (w - boardPixDiam)/2;
  int yOffset = (h - boardPixDiam)/2;
  xOffset = (xOffset<0) ? 0: xOffset;
  yOffset = (yOffset<0) ? 0: yOffset;
  int rowcounter = 0;
  int colcounter = 0;
  for (int row=2; row<MAX_UNITS; row+=4) {
   for (int col=2; col<MAX_UNITS; col+=4){ 
    int r = (row-2)/4;
    int c = (col-2)/4;
    if (PegLocationIndex.mask[r][c] == 0) continue; // skip this location
    id++;
    int x = (col)*ppgu + xOffset;
    int y = (row)*ppgu + yOffset;
    if (id > NUM_PEG_LOC)
      return;
    xyLookUpByID[id][0] = x;
    xyLookUpByID[id][1] = y;
    colcounter++;
   }
   colcounter = 0;
   rowcounter++;;
  }
 }
 
 /**
  * Draws the board spots. Uses precomputed x,y s to define geometry.
  * Hooks to draw individual spots as well as location IDs
  * @param g2 Graphics2D to use for drawing
  */
 protected void drawBoardSpots(Graphics2D g2) {
  if (fontMetrics == null) {
   fontMetrics = g2.getFontMetrics();
  }
  
   if (gridUnit < 8)
     showIds = false;
   if (gridUnit > 8)
     showIds = true;
  for (int id = 1; id <= NUM_PEG_LOC; id++) {
    int[] peg = PegLocationIndex.getPegLocation(id);
   drawSpot(g2, id, model.isPeg(peg),(id == selectedPeg));
   if (id == selectedPeg)
     selectedPeg = -1;

   if (showIds) {
    drawSpotID(g2, id);
   }
  }
 }
 
 /**
  * Draws an individual board spot. Uses precomputed x,y s to define geometry.
  * @param g2 Graphics2D to use for drawing
  * @param id spot location ID to draw
  * @param occupied true if this spot is occupied, else it is empty
  * @param selected true if this spot is selected
  */
 protected void drawSpot(Graphics2D g2, int id, boolean occupied, boolean selected) {
  Color locColor = (occupied) ? pegColor :holeColor;
  g2.setColor(locColor);
  if ((isMobOfMummiesGame == 1) && (occupied == false) && (id < 12) && !(id == 7) && !(id == 8))
	  g2.setColor(holeColorDawgHouse);
  int diam = gridUnit*2;
  int x = xyLookUpByID[id][0];
  int y = xyLookUpByID[id][1];
  g2.fillOval(x-gridUnit, y-gridUnit, diam, diam);
  if (selected) {
   g2.setColor(highlightedCol);
   g2.drawRect(x-gridUnit, y-gridUnit, diam, diam);
  }
 }
 
 /**
  * Draws the ID number for an individual board spot. Uses precomputed
  * x,y s to define geometry.
  * @param g2 Graphics2D to use for drawing
  * @param id spot location ID to draw (this ID will be displayed)
  */
 protected void drawSpotID(Graphics2D g2, int id) {
  int x = xyLookUpByID[id][0];
  int y = xyLookUpByID[id][1];
  
  if (fontMetrics != null) {
   int strWid = fontMetrics.stringWidth(""+id);
   int strHt = fontMetrics.getHeight();
   int strX = x-strWid/2;
   int strY = y-strHt/2+(3*strHt)/4;
   g2.setColor(idColor);
   g2.drawString(""+id, strX, strY);
  }
 }
 

 /**
  * Allows the controller to get a location from a mouse event and then ask
  * the view what was at that location. Returns NOTHING if no object identified.
  * Must be kept in sync with drawing routines for objects.
  * @param x screen position x coordinate
  * @param y screen position y coordinate
  * @param xyById the array with precomputed x,ys by loc Id
  * @return the ID number of the spot clicked on or NOTHING
  */
 public int whichSpot(int x, int y) {
  int id, px=0,py=0;
  int delta = gridUnit;
  for (id = 1; id <= NUM_PEG_LOC; id++) {
  px = xyLookUpByID[id][0];
   py = xyLookUpByID[id][1];
   if ( (Math.abs(px-x)<=delta) && (Math.abs(py-y)<=delta)){  
     return id;
   }
  }
  return -1;
 }
 
 /**
  * Initializes components for GUI display
  */
 public void buildGUI() {
  setOpaque(true); // background has no effect unless opaque
  setBackground(bgColor);
  setPreferredSize(DEF_PREFER_SIZE);
  computeSpotLoc(getWidth(), getHeight(), gridUnit);
  addComponentListener(this);
  updateGUI();
 }
 
 /**
  * Triggers a refresh of the display based upon model data (through paintComponent)
  */
 public void updateGUI() {
  repaint();
 }
 
 /*
  * Responds to resizes - adjusts the basic gridUnit based upon window size.
  * Drawing of board will scale with size - also used to process user mouse
  * clicks (since objects move and change size). Tries to maintain a square
  * area for the board.
  */
 public void componentResized(ComponentEvent arg0) {
  int wid = getWidth();
  int ht = getHeight();
  int min = (wid < ht) ? wid : ht;
  gridUnit = min/MAX_UNITS;
  computeSpotLoc(wid, ht, gridUnit);
  updateGUI();
 }

 // Following methods required to implement component listener
 public void componentHidden(ComponentEvent arg0) {
 }

 public void componentMoved(ComponentEvent arg0) {
 }
 
 public void componentShown(ComponentEvent arg0) {
 }
}