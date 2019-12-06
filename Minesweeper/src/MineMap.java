import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Random;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * MineMap is the JPanel that has MineSweeper on it. This can be used anywhere
 * as a Minesweeper widget. Follows the standard rules of Minesweeper.
 * 
 * @author Kanishka Ragula
 *
 */
public class MineMap extends JPanel {

	// Eclipse wanted this. Don't know what it is
	private static final long serialVersionUID = -6231029321013837603L;
	private Random randomInts;
	private int mines;
	private HashSet<Point> mineLocations;
	private JButton[][] displayVals;
	private boolean notRunning, victoryCalled;
	private boolean flagMode;
	private JFrame parent;
	private boolean firstClick;
	private Tile[][] board;
	private int numFlags;
	private JLabel minesLabel;

	/**
	 * Generates a new mine map of the given width, height, mines, and takes in the
	 * parent and label for updating whenever something happens where the GUI needs
	 * to be updated.
	 * 
	 * @param width      Width of the desired mine map (cannot be below 8).
	 * @param height     Height of the desired mine map (cannot be below 8).
	 * @param numMines   Number of mines in game (cannot be less than 10 or greater
	 *                   than w*h-10
	 * @param parent     JFrame this will be put on so that update can be called
	 *                   when an event happens
	 * @param minesLabel Label to be updated whenever a mine is flagged
	 */
	public MineMap(int width, int height, int numMines, JFrame parent, JLabel minesLabel) {
		super();
		int h;
		int w;

		// Restrictions on the height, width, and number of mines of the game
		if (height < 8) {
			h = 8;
		} else {
			h = height;
		}
		if (width < 8) {
			w = 8;
		} else {
			w = width;
		}
		if (numMines >= w * h) {
			mines = w * h - 10;
		} else if (numMines < 10) {
			mines = 10;
		} else {
			mines = numMines;
		}

		// Create a new board of the mines
		board = new Tile[h][w];

		// Instantiate stuff
		flagMode = false;
		notRunning = false;
		victoryCalled = false;
		firstClick = true;
		numFlags = 0;
		randomInts = new Random();
		this.parent = parent;
		this.minesLabel = minesLabel;

		reset();
	}

	/**
	 * Creates a new Mine Map with random mines and adds the individual number
	 * values for each tile
	 */
	private void setupMap() {

		// Create 10 unique Mine Locations
		while (mineLocations.size() < mines) {
			mineLocations.add(getRandPoint());
		}

		// Add each of those mines to the board
		for (Point p : mineLocations) {
			board[(int) p.getX()][(int) p.getY()] = new Tile("M");
		}

		// Create the rest of the mine board
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (getMineVal(i, j).equals("Error")) {
					board[i][j] = new Tile(String.valueOf(numNeighboringMines(i, j)));
				}
			}
		}
	}

	/**
	 * Setup Method with some methods excluded for a pre-made mine map (used when
	 * loading a save)
	 */
	private void setupMapPreMade() {
		mines = 0;
		numFlags = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (getMineVal(i, j).equals("Error")) {
					board[i][j] = new Tile(String.valueOf(numNeighboringMines(i, j)));
				}
				if (getMineVal(i, j).equals("M")) {
					mineLocations.add(new Point(i, j));
					mines += 1;
				}
				if (board[i][j].isFlagged()) {
					numFlags += 1;
				}
			}
		}

	}

	/**
	 * Generates a random point (for use in making mine maps)
	 * 
	 * @return Random Point
	 */
	private Point getRandPoint() {
		return new Point(randomInts.nextInt(board.length), randomInts.nextInt(board[0].length));
	}

	/**
	 * Get String value associated with a Tile coordinate
	 * 
	 * @param r Row of the tile
	 * @param c Column of the tile
	 * @return String corresponding to value of the tile
	 */
	private String getMineVal(int r, int c) {
		if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] == null) {
			return "Error";
		} else {
			return board[r][c].getTileVal();
		}
	}

	/**
	 * Prints out the number of neighboring mines around a given tile
	 * 
	 * @param x x coordinate of the 2d Array
	 * @param y y coordinate of the 2d array
	 * @return Number of neighboring mines
	 */
	private int numNeighboringMines(int x, int y) {
		int returnVal = 0;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (!(i == 0 && j == 0)) {
					if (getMineVal(x + i, y + j).equals("M"))
						returnVal += 1;
				}
			}
		}
		return returnVal;
	}

	/**
	 * Prints the generated map to the console. For debugging purposes
	 */
	@SuppressWarnings("unused")
	private void printMap() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				System.out.print(board[i][j].getTileVal());
			}
			System.out.println();
		}
	}

	/**
	 * Creates new buttons with the corresponding tile values under each button and
	 * adds the logic that button has associated with it to the actionPerformed
	 */
	private void initializeButtons() {
		ImageIcon buttonRender;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				final int x = i;
				final int y = j;

				// Determine what image will be rendered onto the button
				if (board[i][j].isFlagged()) {
					buttonRender = new ImageIcon("files/Flagged.png");
				} else if (board[i][j].isCovered()) {
					buttonRender = new ImageIcon("files/Covered.png");
				} else {
					buttonRender = new ImageIcon("files/" + board[i][j].getTileVal() + ".png");
				}

				// Creates the button that will be rendered
				JButton valToAdd = new JButton(buttonRender);
				valToAdd.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// repaint the parent
						parent.repaint();

						// When a non-flagged covered button is clicked
						if (board[x][y].isCovered() && !board[x][y].isFlagged() && !flagMode) {
							board[x][y].setCovered(false);

							// Uncover it if the game is running
							if (!notRunning)
								uncoverButtons(x, y);

							// Change the value of the button's image
							valToAdd.setIcon(new ImageIcon("files/" + board[x][y].getTileVal() + 
									".png"));

							// If the user is victorious call the dialog only once
							if (victory() && !victoryCalled) {
								victoryCalled = true;
								JOptionPane.showMessageDialog(parent, "Congrats!");
							}

							repaint();

							// Used for preventing a mine from being selected in the first click
							firstClick = false;

						} else if (board[x][y].isCovered() && flagMode && !notRunning) {
							// For if the user is in a flag mode
							if (board[x][y].isFlagged()) {
								valToAdd.setIcon(new ImageIcon("files/Covered.png"));
								numFlags--;
								board[x][y].setFlagged(false);
							} else {
								valToAdd.setIcon(new ImageIcon("files/Flagged.png"));
								numFlags++;
								board[x][y].setFlagged(true);
								parent.repaint();
							}
							// Update the label
							minesLabel.setText("Number of Mines Remaining: " + 
									String.valueOf(getNumMines()));
							minesLabel.repaint();
						}
					}
				});
				displayVals[i][j] = valToAdd;
			}
		}
	}

	/**
	 * Handles the logic associated with clicking either a mine or an empty square
	 * 
	 * @param x x coordinate of mine to handle in 2d array
	 * @param y y coordinate of mine to handle in 2d array
	 */
	private void uncoverButtons(int x, int y) {
		//Recursive method when an empty square is clicked
		if (board[x][y].getTileVal().equals("0") &&  !board[x][y].isFlagged()) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (x + i >= 0 && x + i < board.length && y+j >= 0 && y + j < board[0].length) {
						if (board[x + i][y + j].isCovered()) {
							displayVals[x + i][y + j].doClick(0);
							uncoverButtons(x + i, y + j);

						}
					}
				}
			}
		} else if (board[x][y].getTileVal().equals("M")) {
			//If it is the user's first click, it cannot be a mine clicked
			if (firstClick) {
				while (mineLocations.size() < mines + 1) {
					mineLocations.add(getRandPoint());
				}
				super.removeAll();
				mineLocations.remove(new Point(x, y));
				board = new Tile[board.length][board[0].length];
				displayVals = new JButton[board.length][board[0].length];
				firstClick = false;
				numFlags = 0;

				setupMap();
				initializeButtons();
				parent.update(parent.getGraphics());
				parent.setVisible(true);
				repaint();
				displayVals[x][y].doClick();
			} else {
				//Fail the user if they click a mine
				board[x][y].setTileVal("F");
				displayVals[x][y].setIcon(new ImageIcon("files/F.png"));
				notRunning = true;
				uncoverAll();
				JOptionPane.showMessageDialog(parent, "You tripped a mine, good luck next time!");
			}
		}
		repaint();
	}

	/**
	 * Uncovers all buttons when a player loses
	 */
	private void uncoverAll() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j].isCovered())
					displayVals[i][j].doClick(0);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		super.setLayout(new GridLayout(board.length, board[0].length, 0, 0));
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {

				super.add(displayVals[i][j]);
			}
		}
	}

	/**
	 * Resets the board to a new randomized board of the same dimensions.
	 */
	public void reset() {
		super.removeAll();
		board = new Tile[board.length][board[0].length];
		
		mineLocations = new HashSet<Point>();
		displayVals = new JButton[board.length][board[0].length];
		firstClick = true;
		notRunning = false;
		victoryCalled = false;
		numFlags = 0;

		setupMap();
		initializeButtons();
		repaint();

	}

	/**
	 * Overrides the method to set it to 16 times the width and 16 times the height
	 * of the number of the 2d Array of buttons (to match the sizes of the images.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16 * board[0].length, 16 * board.length);
	}

	public boolean failed() {
		return notRunning;
	}

	/**
	 * Sets the flagMode (mode that flags all tiles covered when clicked) to true or
	 * false
	 * 
	 * @param flagMode Desired flagmode to be in
	 */
	public void setFlaggedMode(boolean flagMode) {
		this.flagMode = flagMode;
	}

	/**
	 * Determines whether or not a victory condition has been met (all tiles cleared
	 * except for those containing mines under them
	 * 
	 * @return Whether or not the player has won the game.
	 */
	public boolean victory() {
		int numCovered = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j].isCovered())
					numCovered++;
			}
		}
		// System.out.println("Num Covered " + numCovered);

		if (numCovered != mines)
			return false;

		for (Point m : mineLocations) {
			if (!board[(int) m.getX()][(int) m.getY()].isCovered()) {
				// System.out.println("Mine at " + m.getX() + "," + m.getY());
				return false;

			}
		}

		for (Point m : mineLocations) {
			displayVals[(int) m.getX()][(int) m.getY()].setIcon(new ImageIcon("files/Flagged.png"));
			board[(int) m.getX()][(int) m.getY()].setFlagged(true);

		}
		numFlags = mines;
		minesLabel.repaint();
		notRunning = true;
		return true;
	}

	/**
	 * Saves the current game to a
	 */
	public void saveGame() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("files/save.txt")));
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[0].length; j++) {
					bw.write(board[i][j].toString());
				}
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent, "Error Saving, sorry!");
		}
	}

	/**
	 * Debugging purposes
	 * 
	 * @param f File to write to
	 */
	protected void saveGame(File f) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[0].length; j++) {
					bw.write(board[i][j].toString());
				}
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent, "Error Saving, sorry!");
		}
	}

	/**
	 * Loads the game from the save file that has already been created. If no file
	 * has been created, does nothing to the existing board.
	 */
	public void loadGame() {
		try {
			board = Tile.tilesFromFile(new File("files/save.txt"));
			mineLocations = new HashSet<Point>();
			displayVals = new JButton[board.length][board[0].length];
			notRunning = false;
			firstClick = false;
			super.removeAll();
			setupMapPreMade();
			initializeButtons();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent, "Error loading, sorry!");
		}
	}

	/**
	 * Debugging version of load game
	 * 
	 * @param fromFile File to read from
	 */
	protected void loadGame(File fromFile) {
		try {
			board = Tile.tilesFromFile(fromFile);
			mineLocations = new HashSet<Point>();
			displayVals = new JButton[board.length][board[0].length];
			notRunning = false;
			super.removeAll();
			setupMapPreMade();
			initializeButtons();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent, "Error loading, sorry!");
		}
	}

	/**
	 * Returns the number of mines that haven't been flagged. If a user has
	 * erroneously put a flag on a tile not a mine, that counts as a mine since the
	 * user believes it is one.
	 * 
	 * @return Number of unflagged mines.
	 */
	public int getNumMines() {
		return Math.max(0, mines - numFlags);
	}

	/**
	 * Debugging purposes
	 * 
	 * @param x x value
	 * @param y y value
	 */
	protected void click(int x, int y) {
		displayVals[x][y].doClick(0);
	}

}
