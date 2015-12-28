package oop.asg04;

import java.util.Arrays;

// Board.java

/**
 * CS108 Tetris Board. Represents a Tetris board -- essentially a 2-d grid of
 * booleans. Supports tetris pieces and row clearing. Has an "undo" feature that
 * allows clients to add and remove pieces efficiently. Does not do any drawing
 * or have any idea of pixels. Instead, just represents the abstract 2-d board.
 */
public class Board {
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = false;
	private boolean committed;
	private int maxHeight;
	private int saveWidth[]; // secondary arrays that store saveWidth and
								// saveHeight
	private int saveHeight[]; // of filled blocks

	// backup ivars for undo
	private boolean[][] gridBackup;
	private int saveWidthBackup[];
	private int saveHeightBackup[];

	// Here a few trivial methods are provided:

	/**
	 * Creates an empty board of the given width and height measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;

		saveWidth = new int[height];
		saveHeight = new int[width];

		// initialize backup arrays
		gridBackup = new boolean[width][height];
		saveWidthBackup = new int[height];
		saveHeightBackup = new int[width];

	}

	/**
	 * Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the max column height present in the board. For an empty board
	 * this is 0.
	 */
	public int getMaxHeight() {

		return maxHeight;
	}

	/**
	 * private helper method that recomputes the maxHeight field
	 * */
	private void calculateMaxHeight() {
		maxHeight = 0;
		for (int i = 0; i < saveHeight.length; i++) {
			if (maxHeight < saveHeight[i])
				maxHeight = saveHeight[i];
		}
	}

	/**
	 * Checks the board for internal consistency -- used for debugging.
	 */
	public void sanityCheck() {
		if (DEBUG) {
			if (!checkWidths()) {
				throw new RuntimeException("Widths check failed");
			}
			if (!checkHeights()) {
				throw new RuntimeException("Heights check failed");
			}
			if (!checkMaxHeight()) {
				throw new RuntimeException("MaxHeights check failed");
			}

		}
	}

	private boolean checkWidths() {
		int i, j;
		for (i = 0; i < height; i++) {
			int rowFilled = 0;
			for (j = 0; j < width; j++) {
				if (grid[i][j])
					rowFilled++;
			}
			if (saveWidth[j] != rowFilled)
				return false;
		}
		return true;
	}

	private boolean checkHeights() {
		int i, j;

		for (i = 0; i < width; i++) {
			int columnFilled = 0;
			for (j = 0; j < height; j++) {
				if (grid[i][j]) {
					columnFilled++;
				}

			}
			if (saveHeight[j] != columnFilled)
				return false;
		}
		return true;
	}

	private boolean checkMaxHeight() {
		int i, j;
		int checkMaxHeight = 0;
		for (i = 0; i < width; i++) {
			int columnFilled = 0;
			for (j = 0; j < height; j++) {
				if (grid[i][j]) {
					columnFilled++;
				}
				if (checkMaxHeight < columnFilled)
					checkMaxHeight = columnFilled;
			}
			if (checkMaxHeight != maxHeight) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Given a piece and an x, returns the y value where the piece would come to
	 * rest if it were dropped straight down at that x.
	 * <p>
	 * Implementation: use the skirt and the col saveHeight to compute this fast
	 * -- O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		//start at the column height for the given x.
		//increment the piece y until it doesn't overlap with 
		int[] skirt = piece.getSkirt();
		int min_possibible_y = 0;
		for (int i=0; i < skirt.length; i++)
		{
			int Y =	Math.max(0, this.saveHeight[i+x] - skirt[i]);	// Y =y_where_ith_skirt_block_hits_cols
			if (Y > min_possibible_y)
				min_possibible_y = Y;
		}
		return min_possibible_y;
	}

	/**
	 * Returns the height of the given column -- i.e. the y value of the highest
	 * block + 1. The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
		return saveHeight[x];
	}

	/**
	 * Returns the number of filled blocks in the given row.
	 */
	public int getRowWidth(int y) {
		return saveWidth[y];
	}

	/**
	 * Returns true if the given block is filled in the board. Blocks outside of
	 * the valid width/height area always return true.
	 */
	public boolean getGrid(int x, int y) {
		boolean result = false;
		try {
			result = grid[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			result = true;
		}
		return result; // YOUR CODE HERE
	}

	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

	/**
	 * Attempts to add the body of a piece to the board. Copies the piece blocks
	 * into the board grid. Returns PLACE_OK for a regular placement, or
	 * PLACE_ROW_FILLED for a regular placement that causes at least one row to
	 * be filled.
	 * <p>
	 * Error cases: A placement may fail in two ways. First, if part of the
	 * piece may falls out of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 * Or the placement may collide with existing blocks in the grid in which
	 * case PLACE_BAD is returned. In both error cases, the board may be left in
	 * an invalid state. The client can use undo(), to recover the valid,
	 * pre-place state.
	 */
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed)
			throw new RuntimeException("place commit problem");
		committed = false;
		System.arraycopy(saveWidth, 0, saveWidthBackup, 0, saveWidth.length);
		System.arraycopy(saveHeight, 0, saveHeightBackup, 0, saveHeight.length);
		for (int i = 0; i < grid.length; i++)
			System.arraycopy(grid[i], 0, gridBackup[i], 0, grid[i].length);

		int result = PLACE_OK;
		int pieceX, pieceY;

		for (TPoint point : piece.getBody()) {
			int X = point.x + x;
			int Y = point.y + y;
			if (x < 0 || y < 0 || X > width - 1 || Y > height - 1) {
				result = PLACE_OUT_BOUNDS;
				// undo();
				break;
			} else if (grid[X][Y]) {
				result = PLACE_BAD;
				// undo();
				break;
			} else {
				grid[X][Y] = true;
				if (saveHeight[X] < Y + 1)
					saveHeight[X] = Y + 1;

				saveWidth[Y]++;
				if (width == saveWidth[Y])
					result = PLACE_ROW_FILLED;
			}
		}
		calculateMaxHeight();
		sanityCheck();
		return result;
	}

	/**
	 * Deletes rows that are filled all the way across, moving things above
	 * down. Returns the number of rows cleared.
	 */
	public int clearRows() {
		/**
		 * This is coded in a very procedural way.  It is somewhat ugly, but mostly straightforward.
		 * 
		 */
		if (committed) {
			committed = false;
			backup();
		}
		
		for (int col=0; col < width; col++)
			saveHeight[col] = 0; //reset the heights
		
		int modify_row = 0, //row we are modifying
		    translate_row = 0, //row we are translating downward
		    newMaxHeight = 0, // new maximum height value
		    rowsCleared = 0; //number of rows cleared
		
		boolean use_empty_row = false;
		while (modify_row < getMaxHeight() || modify_row < height)
		{
			//1.  Increment the row we are effectively moving down (translate row) over filled rows
			if (translate_row == getHeight())
				use_empty_row = true;
			while (!use_empty_row && this.saveWidth[translate_row] == getWidth())
			{	
				++rowsCleared;
				++translate_row;
				
				if (translate_row == getHeight())
					use_empty_row = true;
			}
			
			//2. fill the row and adjust the height
			int fill_width = 0;
			for (int col=0; col < width; col++)
			{
				//fill the row maxHeight
				boolean fill_value = use_empty_row ? false :  grid[col][translate_row];
				grid[col][modify_row] = fill_value;
				if (fill_value)
				{
					fill_width++;
					saveHeight[col] = modify_row + 1;
				}
			}
			
			saveWidth[modify_row] = fill_width;
			if (fill_width != 0)
				newMaxHeight = modify_row + 1;
			//3. increment the rows and loop			
			++translate_row;
			++modify_row;
		}
		
		maxHeight = newMaxHeight;
		sanityCheck();
		return rowsCleared;
	}
	

	

	/**
	 * Reverts the board to its state before up to one place and one
	 * clearRows(); If the conditions for undo() are not met, such as calling
	 * undo() twice in a row, then the second undo() does nothing. See the
	 * overview docs.
	 */
	public void undo() {
		if (!committed){
			int[] temp = saveWidthBackup;
			saveWidthBackup = saveWidth;
			saveWidth = temp;

			temp = saveHeightBackup;
			saveHeightBackup = saveHeight;
			saveHeight = temp;

			boolean[][] gridtemp = gridBackup;
			gridBackup = grid;
			grid = gridtemp;

			calculateMaxHeight();
			commit();
		}
		sanityCheck();
	}

	/**
	 * Puts the board in the committed state.
	 */
	public void commit() {
		committed = true;
	}

	/*
	 * Renders the board state as a big String, suitable for printing. This is
	 * the sort of print-obj-state utility that can help see complex state
	 * change over time. (provided debugging utility)
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height - 1; y >= 0; y--) {
			buff.append('|');
			for (int x = 0; x < width; x++) {
				if (getGrid(x, y))
					buff.append('+');
				else
					buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x = 0; x < width + 2; x++)
			buff.append('-');
		return (buff.toString());
	}

	/**
	 * private helper method to make a backup of board state before it is
	 * modified
	 * */
	private void backup() {

		System.arraycopy(saveWidth, 0, saveWidthBackup, 0, saveWidth.length);
		System.arraycopy(saveHeight, 0, saveHeightBackup, 0, saveHeight.length);
		for (int i = 0; i < grid.length; i++)
			System.arraycopy(grid[i], 0, gridBackup[i], 0, grid[i].length);
	}

	
}