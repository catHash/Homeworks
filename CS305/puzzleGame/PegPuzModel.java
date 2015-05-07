package puzzleGame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class PegPuzModel {
	/*	
	 *  This is the game state
	 */
	private boolean isMobOfMummiesGame;
	private int numberOfMovesPlayed;
	private int hasGameStarted;
	private int[] holeLocation;
	private int[] lastLocation = {-1,-1};
	private int[] location = {-1,-1};
	private int[][] myBoard;
	private int[][][] movesPlayed;

	/*
	 * 	These are constants
	 */
	private static final int MAX_NUMBER_MOVES = 100;
	private static final int BOARD_ROWS = 7;
	private static final int BOARD_COLUMNS = 7;
	private static final int SALUKI = 2;
	private static final int MUMMY = 3;
	private static final int PEG = 1;
	private static final int HOLE = 0;
	private static final int NULL = -1;
	private static final int[] DEFAULT_HOLE_LOCATION = { 3, 3 };
	private static final Charset ENCODING = StandardCharsets.UTF_8;
	private static final int[][] defaultBoard = {
		{ NULL, NULL, PEG, PEG, PEG, NULL, NULL },
		{ NULL, NULL, PEG, PEG, PEG, NULL, NULL },
		{ PEG, PEG, PEG, PEG, PEG, PEG, PEG },
		{ PEG, PEG, PEG, HOLE, PEG, PEG, PEG },
		{ PEG, PEG, PEG, PEG, PEG, PEG, PEG },
		{ NULL, NULL, PEG, PEG, PEG, NULL, NULL },
		{ NULL, NULL, PEG, PEG, PEG, NULL, NULL } };
	private static final int[][] defaultBoardMoM = {
		{ NULL, NULL, HOLE, SALUKI, HOLE, NULL, NULL },
		{ NULL, NULL, HOLE, SALUKI, HOLE,  NULL, NULL },
		{ MUMMY, MUMMY, HOLE, HOLE,  HOLE,  MUMMY, MUMMY },
		{ MUMMY, MUMMY, MUMMY, MUMMY, MUMMY, MUMMY, MUMMY },
		{ MUMMY, MUMMY, MUMMY, MUMMY, MUMMY, MUMMY, MUMMY },
		{ NULL, NULL, MUMMY, MUMMY, MUMMY, NULL, NULL },
		{ NULL, NULL, MUMMY, MUMMY, MUMMY, NULL, NULL } };


	/**
	 * Writes the game state to a file
	 * 
	 * @param String FileName 
	 *           file to write to
	 * @return hopefully a boolean or something
	 * private int numberOfMovesPlayed;
	private boolean hasGameStarted;
	private int[] holeLocation;
	private int[] lastLocation = {-1,-1};
	private int[] location = {-1,-1};
	private int[][] myBoard;
	private int[][][] movesPlayed;
	private String FileName = "$HOME";
	 */
	protected void saveGame(String FileName){
		Path path = Paths.get(FileName);
		try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
			writer.write("" + numberOfMovesPlayed);
			writer.newLine();
			writer.write("" + hasGameStarted);
			writer.newLine();
			for(int i = 0;i < 2;i++){
				writer.write(holeLocation[i] + " ");
			}
			writer.newLine();
			for(int i = 0;i < 2;i++){
				writer.write(lastLocation[i] + " ");
			}
			writer.newLine();
			for(int i = 0;i < 2;i++){
				writer.write(location[i] + " ");
			}
			writer.newLine();
			for (int i = 0; i < defaultBoard.length; i++) {
				for (int j = 0; j < defaultBoard[i].length; j++) {
					if(myBoard[i][j] > -1)
						writer.write(" ");	
					writer.write(myBoard[i][j] + " ");
				}
				writer.write('\n');
			}
			writer.newLine();
			//breakpoint:
			for (int i = 0; i < numberOfMovesPlayed; i++) {
				for (int j = 0; j < 2; j++) {
					for (int k = 0; k < 2; k++) {
						//	if (movesPlayed[i][j][0] == 0 && movesPlayed[i][j][1] == 0)
						//		break breakpoint;
						writer.write(movesPlayed[i][j][k] + " ");
					}
					if(j == 0)
						writer.write("   ");

				}
				writer.write('\n');
			}

			//private static final int PEG = 1;
			//private static final int HOLE = 0;
			//private static final int NULL = -1;
			writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void loadGame(String FileName) {
		try (Scanner scanner = new Scanner(new File(FileName))) {
			numberOfMovesPlayed = scanner.nextInt();
			hasGameStarted = scanner.nextInt();
			holeLocation = new int[2];
			holeLocation[0] = scanner.nextInt();
			holeLocation[1] = scanner.nextInt();
			lastLocation = new int[2];
			lastLocation[0] = scanner.nextInt();
			lastLocation[1] = scanner.nextInt();
			location = new int[2];
			location[0] = scanner.nextInt();
			location[1] = scanner.nextInt();
			myBoard = new int[BOARD_ROWS][BOARD_COLUMNS];
			for (int i = 0; i < defaultBoard.length; i++) {
				for (int j = 0; j < defaultBoard[i].length; j++) {
					myBoard[i][j] = scanner.nextInt();
				}
			}
			movesPlayed = new int[MAX_NUMBER_MOVES][2][2];
			for (int i = 0; i < numberOfMovesPlayed; i++) {
				for (int j = 0; j < 2; j++) {
					for (int k = 0; k < 2; k++) {
						movesPlayed[i][j][k] = scanner.nextInt();
					}
				}
			}

			//			System.out.println("PegPuzModel.loadGame: numberOfMovesPlayed: "
			//					+ numberOfMovesPlayed);
			//			System.out.println("PegPuzModel.loadGame: hasGameStarted: "
			//					+ hasGameStarted);
			//			System.out.println("PegPuzModel.loadGame: holeLocation : "
			//					+ Arrays.toString(holeLocation));
			//			System.out.println("PegPuzModel.loadGame: lastLocation : "
			//					+ Arrays.toString(lastLocation));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * initializes int[][] myBoard to store board state
	 * 		//new int[move number][0, start board location][0, x]
		//new int[move number][0, start board location][1, y]
		//new int[move number][1, end board location][0, x]
		//new int[move number][1, end board location][1, y]
	 */
	public PegPuzModel(boolean isMoM) {
		//new everything
		isMobOfMummiesGame = isMoM ? true : false;//had to put a ternary in there just for fun
		numberOfMovesPlayed = 0;
		hasGameStarted = 0;
		holeLocation = new int[2];
		lastLocation = new int[2];
		location = new int[2];
		myBoard = new int[BOARD_ROWS][BOARD_COLUMNS];
		movesPlayed = new int[MAX_NUMBER_MOVES][2][2];
		//initialize defaults
		holeLocation[0] = DEFAULT_HOLE_LOCATION[0];
		holeLocation[1] = DEFAULT_HOLE_LOCATION[1];
		if (isMoM){
			for (int i = 0; i < defaultBoardMoM.length; i++) {
				for (int j = 0; j < defaultBoardMoM[i].length; j++) {
					myBoard[i][j] = defaultBoardMoM[i][j];
				}
			}
		}else{
			for (int i = 0; i < defaultBoard.length; i++) {
				for (int j = 0; j < defaultBoard[i].length; j++) {
					myBoard[i][j] = defaultBoard[i][j];
				}
			}
		}
	}

	/**
	 * Checks if the current starting hole location is still a hole then swaps
	 * the locations
	 * 
	 * @param location
	 *            {int,int} of desired new hole location
	 * @return whether the new hole location was set
	 */
	protected boolean setHoleLocation(int[] location) {
		if ((location[0] > -1) && (location[1] > -1)) {
			if (myBoard[holeLocation[0]][holeLocation[1]] == HOLE) {
				myBoard[holeLocation[0]][holeLocation[1]] = PEG;
				myBoard[location[0]][location[1]] = HOLE;
				holeLocation[0] = location[0];
				holeLocation[1] = location[1];
				return true;
			}
		}
		return false;
	}
	/**
	 *gets the hole location
	 * 
	 * @return int[] hole location
	 */
	protected void getHoleLocation(int[] location) {
		location[0] = holeLocation[0];
		location[1] = holeLocation[1];
	}
	/**
	 gets if game has started
	 */
	public int getHasGameStarted() {
		return hasGameStarted;
	}
	/**
	 gets if game has started
	 */
	public void setHasGameStarted(int state) {
		hasGameStarted = state;
	}

	/**
	 * Returns the number of pegs remaining on the board
	 * 
	 * @return number of pegs remaining
	 */
	protected int getPegsRemaining() {
		int pegsRemaining = 0;
		if (!isMobOfMummiesGame){
			for (int i = 0; i < myBoard.length; i++) {
				for (int j = 0; j < myBoard.length; j++) {
					if (myBoard[i][j] == PEG)
						pegsRemaining++;
				}
			}
		}else{
			for (int i = 0; i < myBoard.length; i++) {
				for (int j = 0; j < myBoard.length; j++) {
					if (myBoard[i][j] == MUMMY)
						pegsRemaining++;
				}
			}
		}
		return pegsRemaining;
	}
	/**
	 * Returns the number of moves played
	 * 
	 * @return number of moves played
	 */
	protected int getNumMovesPlayed() {
		return numberOfMovesPlayed;
	}
	/**
	 * Returns the moves played array
	 * 
	 * @return moves played 3d array
	 */
	protected int[][][] getMovesPlayed() {
		return movesPlayed;
	}

	/**
	 * Checks a location on the board for pegs
	 * 
	 * @param peg
	 *            {int,int} peg location
	 * @return boolean
	 */
	protected boolean isPeg(int[] peg) {
		if (peg[0] > -1 && peg[1] > -1)
			return (myBoard[peg[0]][peg[1]] == 1);
		else
			return false;
	}
	/**
	 * returns peg type
	 * 
	 * @param peg
	 *            {int,int} peg location
	 * @return int
	 */
	protected int getPeg(int[] peg) {
		return myBoard[peg[0]][peg[1]];
	}


	protected void PlayMummiesMove() {
		int mummyMoves = 0;
		Random generator = new Random(); 



		for (int i = 0; i < defaultBoardMoM.length; i++) {
			for (int j = 0; j < defaultBoardMoM[i].length; j++) {
				i = generator.nextInt(defaultBoardMoM[i].length);
				j = generator.nextInt(defaultBoardMoM[i].length);
				if (myBoard[i][j] == MUMMY){
					if (i > 0){
						if (myBoard[i-1][j] == HOLE){
							myBoard[i][j] = HOLE;
							myBoard[i-1][j] = MUMMY;
							mummyMoves++;
						}
					}
					if (j > 0){
						if (myBoard[i][j-1] == HOLE){
							myBoard[i][j] = HOLE;
							myBoard[i][j-1] = MUMMY;
							mummyMoves++;
						}
					}
					if (j < defaultBoardMoM[i].length-1){
						if (myBoard[i][j+1] == HOLE){
							myBoard[i][j] = HOLE;
							myBoard[i][j+1] = MUMMY;
							mummyMoves++;
						}
					}
					//zombies dont move backwards
					//					if (i < defaultBoardMoM[i].length-1){
					//						if (myBoard[i+1][j] == HOLE){
					//							myBoard[i][j] = HOLE;
					//							myBoard[i+1][j] = MUMMY;
					//							mummyMoves++;
					//						}
					//					}
					if (mummyMoves > 1)
						return;
				}
			}
		}
		return;
	}
	/**
	 * Checks to see if a move is legal and performs the move
	 * 
	 * @param activePeg
	 *            {int,int} peg to move
	 * @param targetPeg
	 *            {int,int} target of move
	 * @return whether a legal move was played
	 */
	protected int PlayMoveMoM(int[] activePeg, int[] targetPeg) {
		System.out.println("test mob of mummies.. beginning a move " +activePeg[0]+ " " + activePeg[1]
				+ " - > " +targetPeg[0]+ " " + targetPeg[1]);
		System.out.println("delta 1 : " + (targetPeg[0] - activePeg[0]) 
				+ " delta 2: " + (targetPeg[1] - activePeg[1]));
		if (myBoard[activePeg[0]][activePeg[1]] == SALUKI && myBoard[targetPeg[0]][targetPeg[1]] == HOLE) {
			System.out.println("test mob of mummies again");
			if (activePeg[0] == targetPeg[0]) {//left and right
				if (targetPeg[1] - activePeg[1] == 2
						&& myBoard[targetPeg[0]][targetPeg[1] - 1] == MUMMY) {// to the right
					System.out.println("test mob of mummies capture right");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[targetPeg[0]][targetPeg[1] - 1] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					//PlayMummiesMove();
					return 0;
				}
				else if(targetPeg[1] - activePeg[1] == 1
						&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE) {// to the right
					System.out.println("test mob of mummies move to the right");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					PlayMummiesMove();
					return 1;
				} else if (activePeg[1] - targetPeg[1] == 2
						&& myBoard[targetPeg[0]][targetPeg[1] + 1] == MUMMY){// to the left
					System.out.println("test mob of mummies capture left");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[targetPeg[0]][targetPeg[1] + 1] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					//PlayMummiesMove();
					return 0;

				}else if(activePeg[1] - targetPeg[1] == 1
						&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE){// to the left
					System.out.println("test mob of mummies move to the left");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					PlayMummiesMove();
					return 1;
				}
			} 
			else if (activePeg[1] == targetPeg[1]) {// up and down
				if (targetPeg[0] - activePeg[0] == 2
						&& myBoard[targetPeg[0] - 1][targetPeg[1]] == MUMMY){// down
					System.out.println("test mob of mummies capture down");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[targetPeg[0] - 1][targetPeg[1]] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					//PlayMummiesMove();
					return 0;
				}
				else if (activePeg[0] - targetPeg[0] == 1
						&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE){// down
					System.out.println("test mob of mummies move to the up");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					PlayMummiesMove();
					return 1;
				} else if (activePeg[0] - targetPeg[0] == 2
						&& myBoard[targetPeg[0] + 1][targetPeg[1]] == MUMMY){// up
					System.out.println("test mob of mummies capture up");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[targetPeg[0] + 1][targetPeg[1]] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					//PlayMummiesMove();
					return 0;
				}
				else if (targetPeg[0] - activePeg[0] == 1
						&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE){// up
					System.out.println("test mob of mummies move to the down");
					myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					PlayMummiesMove();
					return 1;
				}
			}else if ((targetPeg[0] - activePeg[0] == -1) && (targetPeg[1] - activePeg[1] == -1)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE){// diagonal, up+left
				System.out.println("test mob of mummies to the diagonal up+left");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				PlayMummiesMove();
				return 1;
			}	else if ((targetPeg[0] - activePeg[0] == 1) && (targetPeg[1] - activePeg[1] == -1)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE){// diagonal
				System.out.println("test mob of mummies to the diagonal ");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				PlayMummiesMove();
				return 1;
			}	else if ((targetPeg[0] - activePeg[0] == -1) && (targetPeg[1] - activePeg[1] == 1)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE){// diagonal
				System.out.println("test mob of mummies to the diagonal");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				PlayMummiesMove();
				return 1;
			}	else if ((targetPeg[0] - activePeg[0] == 1) && (targetPeg[1] - activePeg[1] == 1)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE){// diagonal
				System.out.println("test mob of mummies to the diagonal");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				PlayMummiesMove();
				return 1;
			}	
			else if ((targetPeg[0] - activePeg[0] == 2) && (targetPeg[1] - activePeg[1] == 2)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE
					&& myBoard[activePeg[0]+1][activePeg[1]+1] == MUMMY){//capture down+right diagonal
				System.out.println("test mob of mummies capture down+right diagonal");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]+1][activePeg[1]+1] = HOLE;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				//PlayMummiesMove();
				return 1;
			}else if ((targetPeg[0] - activePeg[0] == -2) && (targetPeg[1] - activePeg[1] == 2)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE
					&& myBoard[activePeg[0]+1][activePeg[1]-1] == MUMMY){//capture up+right diagonal
				System.out.println("test mob of mummies capture up+right diagonal");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]+1][activePeg[1]-1] = HOLE;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				//PlayMummiesMove();
				return 1;
			}else if ((targetPeg[0] - activePeg[0] == 2) && (targetPeg[1] - activePeg[1] == -2)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE
					&& myBoard[activePeg[0]+1][activePeg[1]-1] == MUMMY){//capture down+left diagona
				System.out.println("test mob of mummies capture down+left diagonal");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]+1][activePeg[1]-1] = HOLE;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				//PlayMummiesMove();
				return 1;
			}else if ((targetPeg[0] - activePeg[0] == -2) && (targetPeg[1] - activePeg[1] == -2)
					&& myBoard[targetPeg[0]][targetPeg[1]] == HOLE
					&& myBoard[activePeg[0]+1][activePeg[1]-1] == MUMMY){//capture up+left diagonal
				System.out.println("test mob of mummies capture up+left diagonal");
				myBoard[targetPeg[0]][targetPeg[1]] = SALUKI;
				myBoard[activePeg[0]+1][activePeg[1]-1] = HOLE;
				myBoard[activePeg[0]][activePeg[1]] = HOLE;
				movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
				movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
				movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
				movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
				numberOfMovesPlayed++;
				//PlayMummiesMove();
				return 1;
			}

		}
		//PlayMummiesMove();
		return 1;
	}
	/**
	 * Checks to see if a move is legal and performs the move
	 * 
	 * @param activePeg
	 *            {int,int} peg to move
	 * @param targetPeg
	 *            {int,int} target of move
	 * @return whether a legal move was played
	 */
	protected int PlayMove(int[] activePeg, int[] targetPeg) {
		if ((myBoard[activePeg[0]][activePeg[1]] == HOLE && myBoard[targetPeg[0]][targetPeg[1]] == HOLE))
			return 4;
		if ((myBoard[activePeg[0]][activePeg[1]] == HOLE && myBoard[targetPeg[0]][targetPeg[1]] == PEG))
			return 3;
		if ((myBoard[activePeg[0]][activePeg[1]] == PEG && myBoard[targetPeg[0]][targetPeg[1]] == PEG))
			return 2;
		if ((myBoard[activePeg[0]][activePeg[1]] == PEG && myBoard[targetPeg[0]][targetPeg[1]] == HOLE)) {
			if (activePeg[0] == targetPeg[0]) {
				if (targetPeg[1] - activePeg[1] == 2
						&& myBoard[targetPeg[0]][targetPeg[1] - 1] == PEG) {// to the right
					myBoard[targetPeg[0]][targetPeg[1]] = PEG;
					myBoard[targetPeg[0]][targetPeg[1] - 1] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					return 0;
				} else if (activePeg[1] - targetPeg[1] == 2
						&& myBoard[targetPeg[0]][targetPeg[1] + 1] == PEG) {// to the left
					myBoard[targetPeg[0]][targetPeg[1]] = PEG;
					myBoard[targetPeg[0]][targetPeg[1] + 1] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					return 0;
				}
			} else if (activePeg[1] == targetPeg[1]) {
				if (targetPeg[0] - activePeg[0] == 2
						&& myBoard[targetPeg[0] - 1][targetPeg[1]] == PEG) {// down
					myBoard[targetPeg[0]][targetPeg[1]] = PEG;
					myBoard[targetPeg[0] - 1][targetPeg[1]] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					return 0;
				} else if (activePeg[0] - targetPeg[0] == 2
						&& myBoard[targetPeg[0] + 1][targetPeg[1]] == PEG) {// up
					myBoard[targetPeg[0]][targetPeg[1]] = PEG;
					myBoard[targetPeg[0] + 1][targetPeg[1]] = HOLE;
					myBoard[activePeg[0]][activePeg[1]] = HOLE;
					movesPlayed[numberOfMovesPlayed][0][0] = activePeg[0];
					movesPlayed[numberOfMovesPlayed][0][1] = activePeg[1];
					movesPlayed[numberOfMovesPlayed][1][0] = targetPeg[0];
					movesPlayed[numberOfMovesPlayed][1][1] = targetPeg[1];
					numberOfMovesPlayed++;
					return 0;
				}
			}
		}
		return 1;
	}

	/**
	 * Checks to see if taking back a move is legal and performs the move
	 * 
	 * @return whether a legal move was taken back
	 */
	protected boolean takeBackMove() {
		int[] firstPeg = new int[2];
		int[] secondPeg = new int[2];

		if (!(numberOfMovesPlayed > 0))
			return false;

		firstPeg[0] = movesPlayed[numberOfMovesPlayed-1][0][0];
		firstPeg[1] = movesPlayed[numberOfMovesPlayed-1][0][1];
		secondPeg[0] = movesPlayed[numberOfMovesPlayed-1][1][0];
		secondPeg[1] = movesPlayed[numberOfMovesPlayed-1][1][1];

		if ((myBoard[firstPeg[0]][firstPeg[1]] == HOLE && myBoard[secondPeg[0]][secondPeg[1]] == PEG)) {
			if (firstPeg[0] == secondPeg[0]) {
				if (secondPeg[1] - firstPeg[1] == 2
						&& myBoard[secondPeg[0]][secondPeg[1] - 1] == HOLE) {// to the right
					myBoard[secondPeg[0]][secondPeg[1]] = HOLE;
					myBoard[secondPeg[0]][secondPeg[1] - 1] = PEG;
					myBoard[firstPeg[0]][firstPeg[1]] = PEG;
					numberOfMovesPlayed--;
					return true;
				} else if (firstPeg[1] - secondPeg[1] == 2
						&& myBoard[secondPeg[0]][secondPeg[1] + 1] == HOLE) {// to the left
					myBoard[secondPeg[0]][secondPeg[1]] = HOLE;
					myBoard[secondPeg[0]][secondPeg[1] + 1] = PEG;
					myBoard[firstPeg[0]][firstPeg[1]] = PEG;
					numberOfMovesPlayed--;
					return true;
				}
			} else if (firstPeg[1] == secondPeg[1]) {
				if (secondPeg[0] - firstPeg[0] == 2
						&& myBoard[secondPeg[0] - 1][secondPeg[1]] == HOLE) {// down
					myBoard[secondPeg[0]][secondPeg[1]] = HOLE;
					myBoard[secondPeg[0] - 1][secondPeg[1]] = PEG;
					myBoard[firstPeg[0]][firstPeg[1]] = PEG;
					numberOfMovesPlayed--;
					return true;
				} else if (firstPeg[0] - secondPeg[0] == 2
						&& myBoard[secondPeg[0] + 1][secondPeg[1]] == HOLE) {// up
					myBoard[secondPeg[0]][secondPeg[1]] = HOLE;
					myBoard[secondPeg[0] + 1][secondPeg[1]] = PEG;
					myBoard[firstPeg[0]][firstPeg[1]] = PEG;
					numberOfMovesPlayed--;
					return true;
				}
			}
		}
		return false;
	}
}

//enum gameBoardState {
//NEW, SETUP, HOLESELECT, MOVESTARTED, ISMOVELEGAL, 
//ILLEGALMOVE, ISGAMEOVER, READYFORMOVE, GAMEOVER
//}
//gameBoardState gameState;

/**
 * Checks if location is a hole
 * 
 * @param location
 *            {int,int} of desired new hole location
 * @return whether the location is the current hole location
 */
//public boolean isHoleLocation(int[] location) {
//	if(location[0] < 8 && location[0] > -1
//		&& location[1] < 8 && location[0] > -1) {
//		if(myBoard[location[0]][location[1]] == HOLE) {
//			return true;
//		}
//		else
//			return false;
//	}
//	return false;
//}

/**
 * Checks if location is a square
 * 
 * @param location
 *            {int,int} of desired square location
 * @return whether the location is a valid square location
 */
//public boolean isSquareLocation(int[] location) {
//	if(location[0] < 8 && location[0] > -1
//		&& location[1] < 8 && location[0] > -1) {
//		if(myBoard[location[0]][location[1]] == HOLE ||
//			myBoard[location[0]][location[1]] == PEG ) {
//			return true;
//		}
//		else
//			return false;
//	}
//	return false;
//}

/**
 * Checks if the current starting hole location is still a hole then swaps
 * the locations
 * 
 * @param location
 *            {int,int} of desired new hole location
 * @return whether the new hole location was set
 */
//protected int[] getHoleLocation() {
//	return holeLocation;
//}

//protected void setGameStarted(boolean started) {
//if(started)
//	hasGameStarted = true;
//else
//	hasGameStarted = false;
//}

//protected boolean getGameStarted() {
//return hasGameStarted;
//}