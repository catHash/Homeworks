package puzzleGame;

import junit.framework.TestCase;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PegPuzModelTest  extends TestCase {
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
    model = new PegPuzModel(false);
 }
 
  @Test
   // Only Allow Legal Moves ID:015
  public void testLegalMoveSequences(){
    model = new PegPuzModel(false);
    testLegalMoveSequence(moveSequence1);
    model = new PegPuzModel(false);
    testLegalMoveSequence(moveSequence2);
  }
 
 //Only Allow user to pick hole location before game starts ID:035
 @Test
public void testPickHoleLocation() {
 model = new PegPuzModel(false);
 int[] TEST_HOLE_LOC = {1,1};
 int[] center_location = {3,3};
 assertTrue("testPickHoleLocation: setting hole location",model.setHoleLocation(TEST_HOLE_LOC));
 assertTrue("default hole location is peg",model.isPeg(center_location));
 assertTrue("new hole location is hole",(!model.isPeg(TEST_HOLE_LOC)));
 assertTrue("testPickHoleLocation: setting hole location", model.setHoleLocation(center_location));
 assertFalse("default hole location is peg",model.isPeg(center_location));
 assertFalse("new hole location is hole",(!model.isPeg(TEST_HOLE_LOC)));
}
 
 
  //Keep Track of Pegs Remaining ID:025
 public void testInitialPegsRemaining() {
  assertEquals("Number of pegs is correct at game start",
    model.getPegsRemaining(), 32);
 }



 public void testLegalMoveSequence(int[][] moveSeq) {
  int startSquare, endSquare;
  for (int i = 0; i < moveSeq.length; i++) {
   startSquare = moveSeq[i][0];
   endSquare = moveSeq[i][1];
   int[] activePeg = PegLocationIndex.getPegLocation(startSquare);
   int[] targetPeg = PegLocationIndex.getPegLocation(endSquare);
   System.out.println("testLegalMoveSequence: " + startSquare + " -> " + endSquare + "   " + activePeg[0] + "," + activePeg[1] +
       " -> " + targetPeg[0] + " " + targetPeg[1]);
 //  assertTrue( "testLegalMoveSequence " + startSquare + " -> " + endSquare, 
 //     model.PlayMove(activePeg,targetPeg) );
  }
 }

}
 

//
//@Test
//// Test if pegs are at startup locations around default hole
//public void testPegLocations() {
// assertTrue("Model has peg at row 2 column 2",
//    model.isPegAtLoc(2, 2));
// assertTrue("Model has peg at row 2 column 3",
//   model.isPegAtLoc(2, 3));
// assertTrue("Model has peg at row 2 column 4",
//   model.isPegAtLoc(2, 4));
// assertTrue("Model has peg at row 3 column 2",
//   model.isPegAtLoc(3, 2));
// assertFalse("Model has peg at row 3 column 3",
//   model.isPegAtLoc(3, 3));
// assertTrue("Model has pe