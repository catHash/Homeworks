package puzzleGame;
public class PegLocationIndex {
   private static final int[] badLocation = {-1,-1};
   private static final int[][] pegIndex = {     
                 {0, 2}, {0, 3}, {0, 4},
                 {1, 2}, {1, 3}, {1, 4},
  {2,0}, {2, 1}, {2, 2}, {2, 3}, {2, 4}, {2, 5}, {2, 6},
  {3,0}, {3, 1}, {3, 2}, {3, 3}, {3, 4}, {3, 5}, {3, 6},
  {4,0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {4, 5}, {4, 6},
                 {5, 2}, {5, 3}, {5, 4},
                 {6, 2}, {6, 3}, {6, 4}};
   public static final int[][] mask = {  
      {0,0,1,1,1,0,0},
      {0,0,1,1,1,0,0},
      {1,1,1,1,1,1,1},
      {1,1,1,1,1,1,1},
      {1,1,1,1,1,1,1},
      {0,0,1,1,1,0,0},
      {0,0,1,1,1,0,0} };
  public static int[] getPegLocation(int peg){
    if (peg > -1)
      return pegIndex[peg-1];
    return badLocation;
  }
}