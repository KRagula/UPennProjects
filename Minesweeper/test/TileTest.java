import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TileTest {
	
	//To String Tests
	@Test
	public void testToStringFlaggedMine() {
		Tile tl = new Tile("M", true, true);
		assertEquals(tl.toString(), "MTT");
	}
	
	@Test
	public void testToStringCoveredMine() {
		Tile tl = new Tile("M", false, true);
		assertEquals(tl.toString(), "MFT");
	}
	
	@Test
	public void testToStringCoveredNumTile() {
		Tile tl = new Tile("2", false, true);
		assertEquals(tl.toString(), "2FT");
	}
	
	@Test
	public void testToStringCoveredEmpty() {
		Tile tl = new Tile("0", false, true);
		assertEquals(tl.toString(), "0FT");
	}
	
	@Test
	public void testToStringUncoveredEmpty() {
		Tile tl = new Tile("0", false, false);
		assertEquals(tl.toString(), "0FF");
	}
	
	
	//Flagged tests
	@Test
	public void isFlaggedTestFalse() {
		Tile tl = new Tile("0", false, true);
		assertFalse(tl.isFlagged());
	}
	
	@Test
	public void isFlaggedTrue() {
		Tile tl = new Tile("0", true, true);
		assertTrue(tl.isFlagged());
	}
	
	@Test
	public void setSetFlaggedTrue() {
		Tile tl = new Tile("0", false, true);
		tl.setFlagged(true);
		assertTrue(tl.isFlagged());
	}
	
	@Test
	public void setSetFlaggedFalse() {
		Tile tl = new Tile("0", true, true);
		tl.setFlagged(false);
		assertFalse(tl.isFlagged());
	}
	
	//Covered Tests
	@Test
	public void isCoveredTestFalse() {
		Tile tl = new Tile("0", false, false);
		assertFalse(tl.isCovered());
	}
	
	@Test
	public void isCoveredTrue() {
		Tile tl = new Tile("0", true, true);
		assertTrue(tl.isCovered());
	}
	
	@Test
	public void setCoveredTrue() {
		Tile tl = new Tile("0", false, false);
		tl.setCovered(true);
		assertTrue(tl.isCovered());
	}
	
	@Test
	public void setCoveredFalse() {
		Tile tl = new Tile("0", false, true);
		tl.setCovered(false);
		assertFalse(tl.isCovered());
	}
	
	//From file tests
	public void testFromFile() {
		try {
			Tile[][] tiles = Tile.tilesFromFile(new File("files/fromFileTest.txt"));
			Tile[][] correct = new Tile[2][3];
			correct[0][0] = new Tile("0", false, false);
			correct[0][1] = new Tile("1", false, false);
			correct[0][2] = new Tile("1", false, false);
			correct[1][0] = new Tile("0", false, false);
			correct[1][1] = new Tile("1", false, false);
			correct[1][2] = new Tile("M", true, true);
			for(int i=0; i<2; i++) {
				for(int j=0; j<3; j++) {
					assertTrue(tiles[i][j].equals(correct[i][j]));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed");
			e.printStackTrace();
		}
	}
}
