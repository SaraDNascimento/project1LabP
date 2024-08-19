package project;

/**
 * Represents a movie in the video club inventory.
 * 
 * @author Sara Nascimento fc61866
 * 
 */
public class Movie {

	private final String title;
	private final int year;
	private int quantity;
	private int[][] rentals;
	private final double price;
	private final double tax;
	private String code;

	/**
	 * Constructs a Movie object with the given attributes.
	 * 
	 * @param title    the title of the movie
	 * @param year     the year of release
	 * @param quantity the quantity of copies available
	 * @param rentals  rentals informations
	 * @param price    the rental price
	 * @param tax      the tax rate
	 * 
	 */
	public Movie(String title, int year, int quantity, int[][] rentals, double price, double tax) {

		this.title = title;
		this.year = year;
		this.quantity = quantity;
		this.rentals = rentals;
		this.price = price;
		this.tax = tax;
		this.code = getCode(title);
	}

	/**
	 * Returns the code of the movie.
	 * 
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Returns the title of the movie.
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Returns the release year of the movie.
	 * 
	 * @return year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Returns the inventory total of the movie.
	 * 
	 * @return the total quantity of copies
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity of copies available in the inventory.
	 * 
	 * @param quantity the quantity of copies to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the rental information of the movie.
	 * 
	 * @return an array representing the rentals, each row contains the client ID
	 *         and the days remaining
	 */
	public int[][] getRentals() {
		return rentals;
	}

	/**
	 * Sets the rental information of the movie.
	 * 
	 * @param rentals an array representing the rentals, each row contains the
	 *                client ID and the days remaining
	 */
	public void setRentals(int[][] rentals) {
		this.rentals = rentals;
	}

	/**
	 * Returns the rental price of the movie.
	 * 
	 * @return price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Returns the tax rate of the movie.
	 * 
	 * @return tax
	 */
	public double getTax() {
		return tax;
	}

	/**
	 * Generates the code of the movie based on its title.
	 * 
	 * @param title the title of the movie
	 * @return the code of the movie
	 */
	private String getCode(String title) {

		title = justLetters(title).toUpperCase();
		StringBuilder code = new StringBuilder();
		char[][] grid = fillMatrix(title);

		// reads the matrix and adds its characters to the code, reading them by row
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < title.length(); j++) {
				if (grid[i][j] != '\0') {
					code.append(grid[i][j]);
				}
			}
		}
		// two substrings, one with the first three characters of the code and
		// another with the last three characters
		return code.substring(0, 3) + code.substring(title.length() - 3, title.length());
	}

	/**
	 * Removes all characters tha aren't letters.
	 * 
	 * @param title the string from which spaces will be removed
	 * @return a new string (without spaces)
	 */
	private String justLetters(String title) {

		// only appends the characters that are letters
		StringBuilder titleStr = new StringBuilder();
		for (int i = 0; i < title.length(); i++) {
			char c = title.charAt(i);
			if (Character.isLetter(c)) {
				titleStr.append(c);
			}
		}
		return titleStr.toString();
	}

	/**
	 * Fills an array with the characters of a given title in a zig-zag pattern.
	 * 
	 * @param title the title whose characters will be placed into the matrix
	 * @return array
	 */
	private static char[][] fillMatrix(String title) {

		char[][] matrix = new char[3][title.length()];
		int line = 0; // functions as a 'line counter'
		boolean down = true; // the direction of the zig-zag pattern

		// fills the matrix with the title in a zig-zag pattern
		for (int i = 0; i < title.length(); i++) {
			matrix[line][i] = title.charAt(i);

			// when reaching the end of the matrix, change the direction
			if (down) {
				line++;
				if (line == matrix.length) {
					line = matrix.length - 2;
					down = false;
				}
			} else {
				line--;
				if (line < 0) {
					line = 1;
					down = true;
				}
			}
		}
		return matrix;
	}

	/**
	 * Checks if there is an available copy for rental.
	 * 
	 * @return true if there is an available copy, false otherwise
	 */
	public boolean hasAvailableCopy() {
		return getQuantity() > 0 && (getRentals() == null || getRentals().length != getQuantity());
	}
}