import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Entry point for the Minesweeper Game. Handles creating the UI as well as
 * rendering the controls.
 * 
 * @author Kanishka Ragula
 * @version 1.4.0
 */
public class Game {
	private JFrame game;
	private MineMap mines;
	private JLabel numMinesLabel;
	private JPanel customEntry;

	/**
	 * Constructor which instantiates the different necessary parts of the GUI
	 */
	private Game() {
		game = new JFrame("Minesweeper");
		customEntry = new JPanel();
		numMinesLabel = new JLabel();

		// By default, the game is 16x16 with 40 mines (intermediate from the original
		// Minesweeper)
		mines = new MineMap(16, 16, 40, game, numMinesLabel);

		// Calls the generate method to create a Minesweeper game and set up the rest of
		// the UI
		generate();
	}

	private void generate() {

		// For the Input Field
		JTextField width = new JTextField(2);
		JTextField height = new JTextField(2);
		JTextField numMines = new JTextField(2);
		customEntry.add(new JLabel("Width: "));
		customEntry.add(width);
		customEntry.add(new JLabel("Height: "));
		customEntry.add(height);
		customEntry.add(new JLabel("Number of Mines: "));
		customEntry.add(numMines);

		// For adding the display which tells the user how many mines are left
		JPanel counterPanel = new JPanel();
		numMinesLabel.setText("Number of Mines Remaining: " + String.valueOf(mines.getNumMines()));

		counterPanel.add(numMinesLabel);

		// For the main panel which displays the mines
		JPanel minesPanel = new JPanel();
		minesPanel.add(mines);

		// Add everything so far to game
		game.add(minesPanel, BorderLayout.CENTER);
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.add(counterPanel, BorderLayout.NORTH);

		// Checkbox to control the flagging mode
		JCheckBox flagger = new JCheckBox("Flag");
		flagger.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mines.setFlaggedMode(flagger.isSelected());
			}

		});

		// Reset button to create a new game
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				mines.reset();
				numMinesLabel.setText("Number of Mines Remaining: " + 
						String.valueOf(mines.getNumMines()));
				game.update(game.getGraphics());
				game.setVisible(true);

			}

		});

		// Toolbar to contain all the elements that contrl the game
		JPanel toolBar = new JPanel();
		toolBar.add(flagger);
		toolBar.add(reset);

		// Instruction button
		JButton instructions = new JButton("Instructions");
		instructions.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JOptionPane.showMessageDialog(game, "Welcome to Minesweeper!\n" 
								+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
								+ "Your job is to locate and flag all the mines. \n"
								+ "You will see a board of squares in front of you.  Some contain\n"
								+ "mines under them, others don't.  If you click a square with a \n"
								+ "mine under it, you lose. Your mission, should you choose to\n"
								+ "accept it, is to clear  all squares that do not contain\n"
								+ "mines, flagging (optional) the ones that do.  You do this by\n"
								+ "clicking the squares to see whether or not there is a mine\n"
								+ "under it. If a square doesn't contain a mine under it, it will\n"
								+ "reveal how many mines are in adjacent squares.  Use the\n"
								+ "flag option to keep track of which squares have mines under\n"
								+ "them.  Keep clicking until no mines are left.  \n"
								+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
								+ "To flag a mine, check the flag box.  To return to\n"
								+ "clear mode, uncheck the box.  If you clear all the\n"
								+ "squares without mines, the mines will be flagged\n"
								+ "If you want to pause and come back later, press \n"
								+ "\"Save Game\" and you can close the application.\n"
								+ "To return to the previous saved game, press \"Load Game\"\n"
								+ "If you want to make a Custom Game with a custom number\n"
								+ "of tiles and mines, use the \"Custom Game\" button.  This\n"
								+ "does have some restrictions for size and mines.\n"
								+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
								+ "Good luck and have fun!");

			}

		});

		toolBar.add(instructions);

		// JPanel to house all the IO elements
		JPanel saveBar = new JPanel();

		// Save Game button
		JButton saveGame = new JButton("Save Game");
		saveGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mines.saveGame();
			}

		});

		// Load Game button
		JButton loadGame = new JButton("Load Game");
		loadGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Mines takes care of getting the game, this just has to repaint
				mines.loadGame();
				game.update(game.getGraphics());
				numMinesLabel.setText("Number of Mines Remaining: " + 
						String.valueOf(mines.getNumMines()));
				game.pack();
				game.setVisible(true);
			}

		});

		// Creates a custom game with values from the user
		JButton newGame = new JButton("Custom Game");
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int result = JOptionPane.showConfirmDialog(null, customEntry, 
						"Please Enter Custom Values", JOptionPane.OK_CANCEL_OPTION);
				//If the user entered something, create a new map
				if (result == JOptionPane.OK_OPTION) {
					minesPanel.remove(mines);
					mines = new MineMap(Integer.valueOf(width.getText()), 
							Integer.valueOf(height.getText()), Integer.valueOf(numMines.getText()), 
							game, numMinesLabel);
					
					minesPanel.add(mines, BorderLayout.CENTER);
					numMinesLabel.setText("Number of Mines Remaining: " + 
							String.valueOf(mines.getNumMines()));
					
					//Repaint
					game.pack();
					game.update(game.getGraphics());
					game.setVisible(true);
				}
			}
		});
		
		//Add everything to the JPanel
		saveBar.add(saveGame);
		saveBar.add(loadGame);
		saveBar.add(newGame);
		
		//Create a panel to house the toolBar and controlBar
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		controlPanel.add(toolBar, BorderLayout.NORTH);
		controlPanel.add(saveBar, BorderLayout.SOUTH);

		game.add(controlPanel, BorderLayout.SOUTH);
		game.pack();

		game.setVisible(true);

	}
	
	/**
	 * Entry Point
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Game();
			}
		});
	}
}
