package puzzleGame;


import org.junit.Before;
import org.junit.Test;

public class PegPuzzleTest {
  private PegPuzzle puzzle;
  private PegPuzModel model;
  
  int[][] moveSequence1  = { {15,17}, {28,16}, {25,23}, {16,28}, {31,23},
    {22,24}, {4,16}, {7,9}, {21,7}, {10,8},
    {7,9}, {16,4}, {1,9}, {27,25}, {13,27},
    {12,26}, {24,10}, {26,24}, {11,25}, {24,26},
    {32,24}, {9,11}, {2,10}, {27,25}, {30,18},
    {11,25}, {3,11}, {24,26}, {10,12} };
  int[][] moveSequence2 = { {29,17}, {26,24}, {33,25}, {31,33}, {18,30}, 
    {33,25}, {6,18}, {13,11}, {27,13}, {10,12}, {13,11}, {8,10}, 
    {1,9}, {16,4}, {3,1}, {1,9},{28,16}, {21,23}, {7,21}, {24,22}, 
    {21,23}, {10,12}, {12,26}, {26,24}, {24,22}, {22,8}, {8,10}, 
    {17,19}, {5,17}, {16,18}, {19,17}};
  
  @Before
  public void setUp() {
    puzzle = new PegPuzzle();
  }
  
  
   // Represent Board and Moves Graphically ID:010
  @Test
 public void testGraphicalUserInterface() {
   //puzzle.mouseClicked(new MouseEvent(MOUSE_CLICKED,puzzle.view.xyLookUpByID[15][0],puzzle.view.xyLookUpByID[15][1]
 }
   
   //Only Allow user to pick hole location before game starts ID:035
// @Test
//public void testPickHoleLocation() {
//   
//}
 
 // Game over message ID:030
 // TODO: feedback on number of pegs left
 public void testGameOverMessage() {

 }
 

 
 // Show Credits ID:075
 // TODO:
 public void testCredits() {

 }


  
 
 
  
  
  
}