import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class Tile {
	private String tileVal;
	private boolean flagged;
	private boolean covered;
	
	/**
	 * Generates a default unflagged covered tile with a given tile value
	 * @param tileVal Tile's value
	 */
	public Tile(String tileVal) {
		this.setTileVal(tileVal);
		setFlagged(false);
		setCovered(true);
	}
	
	/**
	 * Creates a custom tile with a given tile value, flagged and covered status
	 * @param tileVal Tile's value
	 * @param flagged Whether or not the tile is flagged
	 * @param covered Whether or not the tile is covered
	 */
	public Tile(String tileVal, boolean flagged, boolean covered) {
		this.tileVal = tileVal;
		this.flagged = flagged;
		this.covered = covered;
	}
	
	/**
	 * Generates a 2d array of tiles from a given save file
	 * @param entry File to read from 
	 * @return 2d array of tiles read from file
	 * @throws IOException An exception, either from bad format or the file is not found
	 */
	public static Tile[][] tilesFromFile(File entry) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(entry));

		ArrayList<String> tilesStrings = new ArrayList<String>();
		String currLine = br.readLine();
		while (currLine != null) {
			tilesStrings.add(currLine);
			currLine = br.readLine();
		}
		br.close();

		if (tilesStrings.isEmpty())
			throw new IOException("Input File Empty");

		int length1 = tilesStrings.get(0).length();

		for (String s : tilesStrings) {
			if (s.length() % 3 != 0 || s.length() != length1 || length1 == 0)
				throw new IOException("Input File Formatted Poorly, length");
		}

		Tile[][] returnVal = new Tile[tilesStrings.size()][length1 / 3];
		Tile tempTile;
		for (int i = 0; i < tilesStrings.size(); i++) {
			StringReader sr = new StringReader(tilesStrings.get(i));
			int index = 0;
			do {
				char tileVal = (char) sr.read();
				char flaggedChar = (char) sr.read();
				char coveredChar = (char) sr.read();
				if (!Tile.isValidTile(tileVal) || !Tile.isBoolChar(flaggedChar) || 
						!Tile.isBoolChar(coveredChar)) {
					throw new IOException("Input File Formatted Poorly, char vals");
				}
				boolean isFlagged = true;
				boolean isCovered = true;
				if (flaggedChar == 'F') {
					isFlagged = false;
				}
				if (coveredChar == 'F') {
					isCovered = false;
				}
				tempTile = new Tile(String.valueOf(tileVal), isFlagged, isCovered);
				returnVal[i][index] = tempTile;
				index++;
			} while (index < length1 / 3);

		}
		
		return returnVal;
	}

	/**
	 * @return the flagged
	 */
	public boolean isFlagged() {
		return flagged;
	}

	/**
	 * @param flagged the flagged to set
	 */
	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	/**
	 * @return the tileVal
	 */
	public String getTileVal() {
		return tileVal;
	}

	/**
	 * @param tileVal the tileVal to set
	 */
	public void setTileVal(String tileVal) {
		this.tileVal = tileVal;
	}

	/**
	 * @return the covered
	 */
	public boolean isCovered() {
		return covered;
	}

	/**
	 * @param covered the covered to set
	 */
	public void setCovered(boolean covered) {
		this.covered = covered;
	}
	
	/**
	 * Returns whether or not the tile is valid (M, F, or any number from 0-8)
	 * @param tileVal char to check if it is a tile value
	 * @return Whether or not it is a valid tile value
	 */
	public static boolean isValidTile(char tileVal) {
		return (tileVal == 'F' || tileVal == 'M' || tileVal == '0' || tileVal == '1' || 
				tileVal == '2' || tileVal == '3' || tileVal == '4' || tileVal == '5' || 
				tileVal == '6' || tileVal == '7' || tileVal == '8');
	}
	
	/**
	 * Returns wheter or not a given char is a valid boolean value for the encoding (T or F)
	 * @param boolVal Char value to check
	 * @return Whether or not is is a valid boolean char value
	 */
	public static boolean isBoolChar(char boolVal) {
		return (boolVal == 'F' || boolVal == 'T');
	}
	
	/**
	 * Used for encoding
	 */
	@Override
	public String toString() {
		String returnVal = tileVal;
		if (isFlagged()) {
			returnVal += "T";
		} else {
			returnVal += "F";
		}

		if (isCovered()) {
			returnVal += "T";
		} else {
			returnVal += "F";
		}
		return returnVal;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (covered ? 1231 : 1237);
		result = prime * result + (flagged ? 1231 : 1237);
		result = prime * result + ((tileVal == null) ? 0 : tileVal.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (covered != other.covered)
			return false;
		if (flagged != other.flagged)
			return false;
		if (tileVal == null) {
			if (other.tileVal != null)
				return false;
		} else if (!tileVal.equals(other.tileVal))
			return false;
		return true;
	}
	
}
