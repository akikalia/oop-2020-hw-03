import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}

	private List<Spot> spots;
	private int solutions, numSpots;
	private long elapsed;
	private Grid grid;
	private String solution;
	private int [][]ints;

	private class Grid{
		private List<Set<Integer>> horizontal;
		private List<Set<Integer>> vertical;
		private List<List<Set<Integer>>> parts;
		private int [][] arrGrid;
		public Grid(int [][]ints) {
			arrGrid = new int[SIZE][SIZE];
			horizontal = new ArrayList<>(SIZE);
			vertical = new ArrayList<>(SIZE);
			parts = new ArrayList<>(PART);
			for (int i = 0; i < SIZE; i++) {
				vertical.add(new HashSet<Integer>());
				horizontal.add(new HashSet<Integer>());
			}
			List<Set<Integer>> temp;
			for (int x = 0; x < PART; x++){
				temp = new ArrayList<>(PART);
				for (int y = 0; y < PART; y++){
					temp.add(y, new HashSet<Integer>());
				}
				parts.add(x, temp);
			}
			for (int x = 0; x < SIZE; x++){
				for (int y = 0; y < SIZE; y++) {
						this.populate(x, y, ints[x][y]);
				}
			}
		}

		public void populate(int x, int y, int val){
			vertical.get(y).add(val);
			horizontal.get(x).add(val);
			parts.get(x/PART).get(y/PART).add(val);
			arrGrid[x][y] = val;
		}
		public void remove(int x, int y){
			int val = arrGrid[x][y];
			vertical.get(y).remove(val);
			horizontal.get(x).remove(val);
			parts.get(x/PART).get(y/PART).remove(val);
			arrGrid[x][y] = 0;
		}
		public boolean contains(int x, int y,int val){
			boolean res = false;
			res = res || vertical.get(y).contains(val);
			res = res || horizontal.get(x).contains(val);
			res = res || parts.get(x/PART).get(y/PART).contains(val);
			return res;
		}
		public String getSolution(){

			return stringify(arrGrid);
		}
	}

	private class Spot implements Comparable<Spot>{
		int possibleNums;
		int x, y, currVal;

		public Spot(int x, int y) {
			possibleNums = 0;
			this.x=x;
			this.y=y;
			for (int i = 1; i < 10; i++){
				if (!grid.contains(x, y, i)){
					possibleNums++;
				}
			}
			currVal = 0;
		}

		//trys to set spot value, returns false if failed, true if success
		public boolean setVal(int val){
			if (grid.contains(x, y, val))
				return false;
			grid.populate(x, y, val);
			currVal = val;
			return true;
		}
		public void unsetVal(){
			grid.remove(x, y);
		}

		public int getPossibleNums(){
			return possibleNums;
		}

		@Override
		public int compareTo(Spot otherSpot) {
			return  possibleNums - otherSpot.getPossibleNums();
		}
	}
	public Sudoku(String text){
		this(textToGrid(text));
	}
	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		solutions = 0;
		elapsed = 0;
		solution = "";
		this.ints = ints;
		grid = new Grid(ints);
		spots = new ArrayList<>();
		for (int x = 0; x < SIZE; x++){
			for (int y = 0; y < SIZE; y++) {
				if (ints[x][y] == 0)
					spots.add(new Spot(x, y));
			}
		}
		numSpots = spots.size();
		Collections.sort(spots);
	}


	static private int count = 0;
	private void solveWr(int i) {
		Spot spot;
		if (i == numSpots){
			solutions++;
			if (solutions  == 1)
				solution = grid.getSolution();
			return;
		}
		spot = spots.get(i);
		for (int m = 1; m < 10; m++){
			if (spot.setVal(m)) {
				solveWr(i+1);
				spot.unsetVal();
			}
		}
	}
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		long startTime = System.currentTimeMillis();
		solveWr(0);
		long endTime = System.currentTimeMillis();
		elapsed = (endTime - startTime);
		return solutions;
	}
	
	public String getSolutionText() {
		return solution;
	}
	
	public long getElapsed() {
		return elapsed;
	}

	private String stringify(int [][]arr){
		StringBuilder res = new StringBuilder();
		for(int x = 0;x < SIZE; x++){
			for(int y = 0;y < SIZE; y++){
				res.append(arr[x][y]);
				if (y+1 != SIZE)
					res.append(" ");
			}
			if (x+1 != SIZE)
				res.append("\n");
		}
		return res.toString();
	}

	public String toString(){

		return stringify(ints);
	}
}
