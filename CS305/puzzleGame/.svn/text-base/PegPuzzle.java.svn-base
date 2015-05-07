package puzzleGame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class PegPuzzle extends MouseAdapter {
	protected int[] currentLocation = { -1, -1 };
	protected int[] lastLocation = {-1,-1};
	protected int[] clickLocation = {-1,-1};
	private boolean isMobOfMummiesGame;
	protected PegPuzModel model;
	protected PegPuzModel modelMoM;
	private JFrame frame;
	private PegPuzView view;
	protected JPanel cardPane;
	protected CardLayout cardLayout;
	protected GridBagLayout bagLayout;
	JLabel consoleLabel;
	JLabel pegsRemainingLabel;
	JTextArea movesLabel;
	JFileChooser fileChooser;
	protected final static String MOVES_CARD = "MOVES";
	protected final static String MOM_INSTRUCTIONS_CARD = "MOM_INSTRUCTIONS";
	protected final static String ABOUT_CARD = "ABOUT";
	protected final static String ABOUT_MOM_CARD = "ABOUTMOM";
	protected final static String BOARD_CARD = "BOARD";
	protected final static String START_MENU_CARD = "START";
	protected final static String MOM_CARD = "MUMMIES";
	protected final static String INSTRUCTIONS_CARD = "INSTRUCTIONS";

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PegPuzzle window = new PegPuzzle();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public PegPuzzle() {
		model = new PegPuzModel(false);
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buildGUI();
		frame.pack();
		frame.setVisible(true);
	}
	/**
	 * receives input from controller
	 * 
	 * @param location - click location
	 * 
	 * 
	 */ 
	protected int indicateLocation(int[] location) {
		lastLocation[0] = currentLocation[0];
		lastLocation[1] = currentLocation[1];
		currentLocation[0] = location[0];
		currentLocation[1] = location[1];
		int pegReturn = 1;
		int[] holeLocation = {-1,-1};
		model.getHoleLocation(holeLocation);
		if (isMobOfMummiesGame == false) {
			/*
			 * Regular mode rules for clicking/playing moves if you dont like it,
			 * see Brian Gunn
			 */
			if (location[0] == -1 || lastLocation[0] == -1)
				return 1;
			else if (lastLocation[0] == currentLocation[0]
					&& lastLocation[1] == currentLocation[1])
				return 1;
			else {
				if (model.getHasGameStarted() == 0) {
					if ((lastLocation[0] == holeLocation[0])
							&& (lastLocation[1] == holeLocation[1])) {
						if (model.setHoleLocation(currentLocation)) {
							currentLocation[0] = -1;
							currentLocation[0] = -1;
							return 1;
						}
					} else if ((pegReturn = model.PlayMove(lastLocation, currentLocation)) == 0) {
						model.setHasGameStarted(1);
						return pegReturn;
					}

				} else
					return model.PlayMove(lastLocation, currentLocation);
			}
			return pegReturn;
		} else {
			/*
			 * mob of mummies rules for clicking/playing moves
			 * if you dont like it, see Brian Gunn
			 */
			if (location[0] == -1 || lastLocation[0] == -1)
				return 1;
			else if (lastLocation[0] == currentLocation[0]
					&& lastLocation[1] == currentLocation[1])
				return 1;
			else 
				return modelMoM.PlayMoveMoM(lastLocation, currentLocation);
		}
		//return 1;
	}

	/**
	 * Basic control code to support player interacting with PegPuzView
	 * 
	 * @param e
	 *            event generated from clicking on PegPuzView
	 */
	public void mouseClicked(MouseEvent e) {
		//System.out.println(e);
		int id = view.whichSpot(e.getX(), e.getY());
		clickLocation[0] = PegLocationIndex.getPegLocation(id)[0];
		clickLocation[1] = PegLocationIndex.getPegLocation(id)[1];
		view.selectedPeg = id;
		int pegReturn  = indicateLocation(clickLocation);
		if (!isMobOfMummiesGame){
			if (pegReturn == 0){
				consoleLabel.setText("You played a valid move!!! That means one less peg!");
			}else  if (pegReturn == 5){
				consoleLabel.setText("Invalid move");
				view.selectedPeg = -1;
			}else  if (pegReturn == 4){
				consoleLabel.setText("Invalid move: Hole -> Hole");
				view.selectedPeg = -1;
			}else  if (pegReturn == 3){
				consoleLabel.setText("Invalid move: Hole -> Peg");
				view.selectedPeg = -1;
			}else  if (pegReturn == 2){
				consoleLabel.setText(" ");
			}else  if (pegReturn == 1){
				consoleLabel.setText(" ");
			}
			if (model.getPegsRemaining() == 6)
				consoleLabel.setText("6 Pegs Left: Are You Even Trying?");
			else if (model.getPegsRemaining() == 5)
				consoleLabel.setText("5 Pegs Left: You Need More Practice!");
			else if (model.getPegsRemaining() == 4)
				consoleLabel.setText("4 Pegs Left: You Can Do Better!");
			else if (model.getPegsRemaining() == 3)
				consoleLabel.setText("3 Pegs Left: Good Job!");
			else if (model.getPegsRemaining() == 2)
				consoleLabel.setText("2 Pegs Left: Excellent!");
			else if (model.getPegsRemaining() == 1)
				consoleLabel.setText("1 Peg Left: Genius!");
			pegsRemainingLabel.setText("Pegs Remaining: " + model.getPegsRemaining());
		}else{
			if (pegReturn == 0){
				consoleLabel.setText("You killed a mummy!");
			}else if (pegReturn == 1){
				consoleLabel.setText("You moved a space");
			}
			pegsRemainingLabel.setText("Mummies Remaining: " + modelMoM.getPegsRemaining());
		}
		view.updateGUI();

	}
	/**
	 * This is the structure of the GUI:
	 * 
	 * 	cont
	 * 		|cardPane
	 * 			|boardPane
	 * 			|	|buttonPane
	 * 			|		|newGameButton
	 * 			|		|creditsButton
	 * 			|		|takeBackMoveButton
	 * 			|		|saveGameButton
	 * 			|		|loadGameButton
	 * 			|		|viewMoveHistoryButton
	 * 			|	|view				
	 * 			|	|consolePane
	 * 			|		|consolelabel
	 * 			|aboutPane
	 * 			|	|aboutContent
	 * 			|	|backtogameBut
	 *				|startPane
	 *				|	|pugPuzButton
	 *				|	|MoMButton
	 *				|movesPane
	 *					|movesLabel
	 */
	public void buildGUI() {


		JPanel buttonPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		// Button to start a new game
		JButton switchGameButton = new JButton("Switch Game");
		switchGameButton.addActionListener(new ActionListener() {
			// clicking button will create new game
			public void actionPerformed(ActionEvent ae) {
				cardLayout.show(cardPane, START_MENU_CARD);
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		buttonPane.add(switchGameButton, c);
		// Button to go to credits screen
		JButton creditsButton = new JButton("Credits");
		creditsButton.addActionListener(new ActionListener() {
			// clicking button will switch to credits
			public void actionPerformed(ActionEvent ae) {
				if (isMobOfMummiesGame){
					cardLayout.show(cardPane, ABOUT_MOM_CARD);
				}else{
					cardLayout.show(cardPane, ABOUT_CARD);
				}

			}
		});
		c.gridx = 0;
		c.gridy = 1;
		buttonPane.add(creditsButton, c);
		// Button to start a new game
		JButton newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new ActionListener() {
			// clicking button will create new game
			public void actionPerformed(ActionEvent ae) {
				if (isMobOfMummiesGame){
					modelMoM = new PegPuzModel(true);
					view.setModel(model,modelMoM);
				}else{
					model = new PegPuzModel(false);
					view.setModel(model,modelMoM);
				}
				view.updateGUI();
				currentLocation[0] = -1;
				currentLocation[1] = -1;
			}
		});
		c.gridx = 1;
		c.gridy = 0;
		buttonPane.add(newGameButton, c);
		// Button to take a move back
		JButton takeBackMoveButton = new JButton("Take Back Move");
		takeBackMoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				model.takeBackMove();
				view.updateGUI();
			}
		});
		c.gridx = 1;
		c.gridy = 1;
		buttonPane.add(takeBackMoveButton, c);

		// Button to save game
		JButton saveGameButton = new JButton("Save Game");
		saveGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileChooser = new JFileChooser();

				int returnVal = fileChooser.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					model.saveGame(file.toString());
				}
			}
		});
		c.gridx = 2;
		c.gridy = 0;
		buttonPane.add(saveGameButton, c);

		// Button to load game
		JButton loadGameButton = new JButton("Load Game");
		loadGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileChooser = new JFileChooser();

				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					model.loadGame(file.toString());
					view.updateGUI();
					currentLocation[0] = -1;
					currentLocation[1] = -1;
				}
			}
		});
		c.gridx = 3;
		c.gridy = 0;
		buttonPane.add(loadGameButton, c);



		// Button to go to move history
		JButton moveHistoryButton = new JButton("Move History");
		moveHistoryButton.addActionListener(new ActionListener() {
			// clicking button will switch to credits
			public void actionPerformed(ActionEvent ae) {
				cardLayout.show(cardPane, MOVES_CARD);
				movesLabel.setText("This is the history of your moves played:\n" + view.getMovesHistory());
			}
		});
		c.gridx = 2;
		c.gridy = 1;
		buttonPane.add(moveHistoryButton, c);
		// Button to view instructions pane
		JButton instructionsButton = new JButton("Instructions");
		instructionsButton.addActionListener(new ActionListener() {
			// clicking button will switch to credits
			public void actionPerformed(ActionEvent ae) {
				if (!isMobOfMummiesGame){
					cardLayout.show(cardPane, INSTRUCTIONS_CARD);
				}else{
					cardLayout.show(cardPane, MOM_INSTRUCTIONS_CARD);
				}
			}
		});
		c.gridx = 3;
		c.gridy = 1;
		buttonPane.add(instructionsButton, c);

		// Button to return to boardPane
		JButton backToGameBut = new JButton("Back");
		backToGameBut.addActionListener(new ActionListener() {
			// clicking button will switch back to the game
			public void actionPerformed(ActionEvent ae) {
				cardLayout.show(cardPane, BOARD_CARD);
			}
		});
		// Button to return to boardPane
		JButton backToGameBut2 = new JButton("Back");
		backToGameBut2.addActionListener(new ActionListener() {
			// clicking button will switch back to the game
			public void actionPerformed(ActionEvent ae) {
				cardLayout.show(cardPane, BOARD_CARD);
			}
		});
		// Button to return to boardPane
		JButton backToGameBut3 = new JButton("Back");
		backToGameBut3.addActionListener(new ActionListener() {
			// clicking button will switch back to the game
			public void actionPerformed(ActionEvent ae) {
				cardLayout.show(cardPane, BOARD_CARD);
			}
		});
		// Button to return to boardPane
		JButton backToGameBut4 = new JButton("Back");
		backToGameBut4.addActionListener(new ActionListener() {
			// clicking button will switch back to the game
			public void actionPerformed(ActionEvent ae) {
				cardLayout.show(cardPane, BOARD_CARD);
			}
		});
		// Button to return to boardPane
		JButton backToGameBut5 = new JButton("Back");
		backToGameBut5.addActionListener(new ActionListener() {
			// clicking button will switch back to the game
			public void actionPerformed(ActionEvent ae) {
				cardLayout.show(cardPane, BOARD_CARD);
			}
		});



		JPanel consolePane = new JPanel(new BorderLayout());
		consoleLabel = new JLabel();
		consolePane.add(consoleLabel,BorderLayout.SOUTH);


		pegsRemainingLabel = new JLabel();
		consolePane.add(pegsRemainingLabel,BorderLayout.NORTH);


		JPanel movesPane = new JPanel(new BorderLayout());
		movesLabel = new JTextArea();
		movesPane.add(movesLabel,BorderLayout.CENTER);
		movesLabel.setText("You have not played any moves yet.");
		movesPane.add(backToGameBut2, BorderLayout.NORTH);


		model = new PegPuzModel(false);
		modelMoM = new PegPuzModel(true);
		// Button for PegPuzzle
		JButton pegPuzButton = new JButton("PegPuzzle Game");
		pegPuzButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (model.getNumMovesPlayed() == 0){
					consoleLabel.setText("Welcome to PegPuzzle");
					pegsRemainingLabel.setText("Pegs Remaining: " + model.getPegsRemaining());
				}else{
					consoleLabel.setText("Welcome back to PegPuzzle");
					pegsRemainingLabel.setText("Pegs Remaining: " + model.getPegsRemaining());
				}
				cardLayout.show(cardPane, BOARD_CARD);
				view.setMobOfMummies(isMobOfMummiesGame = false);
				view.updateGUI();
				currentLocation[0] = -1;
				currentLocation[1] = -1;
			}
		});

		// Button for Mob of Mummies!
		JButton MoMButton = new JButton("Mob of Mummies! Game");
		MoMButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (modelMoM.getNumMovesPlayed() == 0){
					consoleLabel.setText("Welcome to PegPuzzle: Mob of Mummies!");
					pegsRemainingLabel.setText("Mummies Remaining: " + modelMoM.getPegsRemaining());
				}else{
					consoleLabel.setText("Welcome back to PegPuzzle: Mob of Mummies!");
					pegsRemainingLabel.setText("Mummies Remaining: " + modelMoM.getPegsRemaining());

				}

				cardLayout.show(cardPane, BOARD_CARD);
				view.setMobOfMummies(isMobOfMummiesGame = true);
				view.updateGUI();
				currentLocation[0] = -1;
				currentLocation[1] = -1;
			}
		});
		JPanel startPane = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		startPane.add(pegPuzButton, c);
		c.gridy = 1;
		startPane.add(MoMButton, c);

		// Use a CardLayout to manage different types of content in the same
		// place
		cardLayout = new CardLayout();
		cardPane = new JPanel(cardLayout);
		// Board Content
		JPanel boardPane = new JPanel(new BorderLayout());

		// About Info
		JPanel AboutPane = new JPanel(new BorderLayout());


		URL aboutInDetailsURL;
		URL instructionsURL;
		URL MoMinstructionsURL;
		JEditorPane aboutContent;
		JEditorPane aboutMoMContent;
		JEditorPane instructionsContent;
		try {

			/*
			 * About Pane
			 */
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			aboutInDetailsURL = classLoader.getResource("resources/Credits.html");
			if(aboutInDetailsURL == null) {
				aboutInDetailsURL = getClass().getResource("resources/Credits.html");
			}
			aboutContent = new JEditorPane(aboutInDetailsURL);

			//begin mom about
			JPanel aboutMoMPane = new JPanel(new BorderLayout());
			instructionsURL = classLoader.getResource("resources/momCredits.html");
			if(instructionsURL == null) {
				instructionsURL = getClass().getResource("resources/momCredits.html");
			}
			aboutMoMContent = new JEditorPane(instructionsURL);
			aboutMoMPane.add(aboutMoMContent, BorderLayout.CENTER);
			aboutMoMPane.add(backToGameBut5, BorderLayout.NORTH);
			aboutMoMContent.setEditable(false);
			cardPane.add(aboutMoMPane, ABOUT_MOM_CARD);
			//end mom about

			JPanel InstructionsPane = new JPanel(new BorderLayout());
			instructionsURL = classLoader.getResource("resources/instructions.html");
			if(instructionsURL == null) {
				instructionsURL = getClass().getResource("resources/instructions.html");
			}
			instructionsContent = new JEditorPane(instructionsURL);
			InstructionsPane.add(instructionsContent, BorderLayout.CENTER);
			InstructionsPane.add(backToGameBut3, BorderLayout.NORTH);
			instructionsContent.setEditable(false);
			cardPane.add(InstructionsPane, INSTRUCTIONS_CARD);

			JPanel MoMInstructionsPane = new JPanel(new BorderLayout());
			MoMinstructionsURL = classLoader.getResource("resources/mominstructions.html");
			if(MoMinstructionsURL == null) {
				MoMinstructionsURL = getClass().getResource("resources/mominstructions.html");
			}
			JEditorPane MoMinstructionsContent = new JEditorPane(MoMinstructionsURL);
			MoMInstructionsPane.add(MoMinstructionsContent, BorderLayout.CENTER);
			MoMInstructionsPane.add(backToGameBut4, BorderLayout.NORTH);
			MoMinstructionsContent.setEditable(false);
			cardPane.add(MoMInstructionsPane, MOM_INSTRUCTIONS_CARD);

			Container cont = frame.getContentPane();
			cont.setLayout(new BorderLayout());
			aboutContent.setEditable(false);

			PegPuzzle myPuzzle = this;
			model = new PegPuzModel(false);
			modelMoM = new PegPuzModel(true);
			view = new PegPuzView(model,modelMoM);
			view.buildGUI();
			view.addMouseListener(myPuzzle);
			view.setModel(model, modelMoM);

			boardPane.add(view, BorderLayout.CENTER);
			boardPane.add(buttonPane, BorderLayout.NORTH);
			boardPane.add(consolePane, BorderLayout.SOUTH);
			AboutPane.add(aboutContent, BorderLayout.CENTER);
			AboutPane.add(backToGameBut, BorderLayout.NORTH);

			cardPane.add(boardPane, BOARD_CARD);
			cardPane.add(AboutPane, ABOUT_CARD);
			cardPane.add(startPane, START_MENU_CARD);
			cardPane.add(movesPane, MOVES_CARD);

			cont.add(cardPane, BorderLayout.CENTER);
			cardLayout.show(cardPane, START_MENU_CARD);

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			aboutContent = new JEditorPane();
			aboutContent.setText("About Content did not properly load");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}


		// About Info


	} 
}

