import static org.junit.Assert.*;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MineMapTest {
	JFrame basePanel;
	JLabel textPanel;
	@Before
	public void setUp() throws Exception {
		//Create a new JFrame and Label to use every time
		basePanel = new JFrame();
		textPanel = new JLabel();
	}

	@After
	public void tearDown() throws Exception {
		//Get rid of the old one
		basePanel.dispose();
		
	}

	
	//Test sizes tests, will make the map the base size of 8x8 mines or 128x128 pixels
	@Test
	public void testSmallerThanNeededSize() {
		MineMap testMap = new MineMap(2 ,2, 2, null, null);
		assertEquals(testMap.getPreferredSize(), new Dimension(128,128));
	}
	
	//Normal use test
	@Test
	public void testNormalSize() {
		MineMap testMap = new MineMap(10, 10, 2, null, null);
		assertEquals(testMap.getPreferredSize(), new Dimension(160,160));
	}
	
	//Tests for get mines
	
	@Test
	public void testGetNumMines() {
		MineMap testMap = new MineMap(10, 10, 12, null, null);
		assertEquals(testMap.getNumMines(), 12);
	}
	
	//Test for minimum number of mines being 10
	@Test
	public void testGetNumMinesMin() {
		MineMap testMap = new MineMap(10, 10, 2, null, null);
		assertEquals(testMap.getNumMines(), 10);
	}
	
	//Test of max number of mines = w*h-10
	@Test
	public void testGetNumMinesMax() {
		MineMap testMap = new MineMap(10, 10, 100, null, null);
		assertEquals(testMap.getNumMines(), 90);
	}

	//Test for putting it on the JFrame.  1/10000 chance of failing
	@Test
	public void testReset() {
		MineMap mines = new MineMap(10, 10, 10, basePanel, textPanel);
		mines.saveGame(new File("files/writeTest1"));
		mines.reset();
		mines.saveGame(new File("files/writeTest2"));
		try {
			Tile[][] save1 = Tile.tilesFromFile(new File("files/writeTest1"));
			Tile[][] save2 = Tile.tilesFromFile(new File("files/writeTest2"));
			boolean same = true;
			for(int i=0; i<save1.length; i++) {
				for(int j=0; j<save1[0].length; j++) {
					if(!save1[i][j].equals(save2[i][j])) {
						same = false;
					}
				}
			}
			assertFalse(same);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException");
		}

	}
	
	//Test to see clicking a flag does nothing
	@Test
	public void testFlaggedClick() {
		try {
			Tile[][] save1 = Tile.tilesFromFile(new File("files/fromFileTest.txt"));
			MineMap mines = new MineMap(10,10,10, basePanel, textPanel);
			mines.loadGame(new File("files/fromFileTest.txt"));
			mines.click(1, 2); //Clicks on the flagged square
			mines.saveGame(new File("files/writeTest1.txt"));
			Tile[][] save2 = Tile.tilesFromFile(new File("files/writeTest1.txt"));
			boolean same = true;
			for(int i=0; i<save1.length; i++) {
				for(int j=0; j<save1[0].length; j++) {
					if(!save1[i][j].equals(save2[i][j])) {
						same = false;
					}
				}
			}
			assertTrue(same);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException");
		}

	}
	
	//Test to see if the recursive statement works when clearing squares with no neighboring mines
	@Test
	public void testEmptySquareClick() {
		try {
			Tile[][] expected = Tile.tilesFromFile(new File("files/fromFileTest.txt"));
			MineMap mines = new MineMap(10,10,10, basePanel, textPanel);
			mines.loadGame(new File("files/emptyClickTest.txt"));
			mines.click(0, 0); //Clicks on the flagged square
			mines.saveGame(new File("files/writeTest1.txt"));
			Tile[][] save2 = Tile.tilesFromFile(new File("files/writeTest1.txt"));
			
			boolean same = true;
			for(int i=0; i<expected.length; i++) {
				for(int j=0; j<expected[0].length; j++) {
					if(!expected[i][j].equals(save2[i][j])) {
						same = false;
					}
				}
			}
			assertTrue(same);
			assertTrue(mines.victory()); //Should Prompt Victory
			assertTrue(save2[1][2].isFlagged()); //Should flag the last mine automatically
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException");
		}
	}
	
	//Tests to see if the last mine is automatically flagged so that the user can't click on it
	@Test
	public void testAutoFlagLast() {
		try {
			
			MineMap mines = new MineMap(10,10,10, basePanel, textPanel);
			mines.loadGame(new File("files/emptyClickTest.txt"));
			mines.click(0, 0); //Clicks on the flagged square
			mines.saveGame(new File("files/writeTest1.txt"));
			Tile[][] save2 = Tile.tilesFromFile(new File("files/writeTest1.txt"));
			
			assertTrue(mines.victory()); //Should Prompt Victory
			assertTrue(save2[1][2].isFlagged()); //Should flag the last mine automatically
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException");
		}
	}
	
	//Test to see that clicking on a mine as the first move relocates the mine
	@Test
	public void testFirstClickOnMineDoesntFail() {
		try {
			
			MineMap mines = new MineMap(10,10,10, basePanel, textPanel);
			mines.loadGame(new File("files/emptyClickTest.txt"));
			mines.click(1, 2); //Clicks on the flagged square
			mines.saveGame(new File("files/writeTest1.txt"));
			Tile[][] save2 = Tile.tilesFromFile(new File("files/writeTest1.txt"));
			
			assertFalse(mines.failed()); //Shouldn't fail
			assertFalse(save2[1][2].getTileVal().equals("M")); //Mine should be moved
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException");
		}
	}
	
}
