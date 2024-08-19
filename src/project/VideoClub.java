package project;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

/**
 * VideoClub class represents a video rental store.
 * 
 * @author Sara Nascimento fc61866
 */
public class VideoClub {

	private Movie[] movies;
	private double totalProfit;
	private double totalRevenue;

	/**
	 * Constructs a VideoClub object by reading data from a given CSV file. If the
	 * specified integer of movies is greater than the number of movies in the file,
	 * it reads all available movies.
	 * 
	 * @requires {@code numberOfMovies>0}
	 * @param fileName       the name of the given CSV file
	 * @param numberOfMovies the integer
	 * @throws FileNotFoundException
	 */
	public VideoClub(String fileName, int numberOfMovies) throws FileNotFoundException {
		this.movies = new Movie[numberOfMovies];
		Scanner reader = new Scanner(new File(fileName));
		reader.nextLine();
		int lines = 0; // counts file's lines
		// title, year, quantity, rentals, price, tax
		while (reader.hasNext() && lines < numberOfMovies) {
			String line = reader.nextLine();
			String[] infos = line.split(",");
			String title = infos[0];
			int year = Integer.parseInt(infos[1]);
			int quantity = infos[2].length() > 0 ? Integer.parseInt(infos[2]) : 0;
			// if infos[3] is empty rentals = null
			int[][] rentals = null;
			if (infos.length > 3 && !infos[3].isEmpty()) {
				rentals = getRentals(infos[3]);
			}
			double price = Double.parseDouble(infos[4]);
			String taxString = infos[5].replace("%", "");
			double tax = Double.parseDouble(taxString);
			this.movies[lines] = new Movie(title, year, quantity, rentals, price, tax);
			lines++;
		}
		reader.close();
		nullRemover();
	}

	/**
	 * Iterates through the array of movies and removes any null elements. Updates
	 * movies.
	 */
	private void nullRemover() {
		int newSize = 0;
		for (int i = 0; i < movies.length; i++) {
			if (movies[i] != null) {
				movies[newSize] = movies[i];
				newSize++;
			}
		}
		movies = Arrays.copyOf(movies, newSize); // array resized to match the new size without null elements
	}

	/**
	 * Parses the rental information string and returns an array representing the
	 * rentals.
	 * 
	 * @param infos string containing rental information
	 * @return array representing the rentals
	 */
	private int[][] getRentals(String infos) {

		infos = infos.replaceAll("[()]", ""); // splits the string by pairs
		String[] pairs = infos.split(" "); // each pair fills a row

		int[][] data = new int[pairs.length][2];
		for (int i = 0; i < pairs.length; i++) {
			String[] pairValues = pairs[i].split(";"); // splits the pair
			data[i][0] = Integer.parseInt(pairValues[0].trim()); // clientID
			data[i][1] = Integer.parseInt(pairValues[1].trim()); // days remaining
		}
		return data;
	}

	/**
	 * Gets the total number of different movies in the video club.
	 * 
	 * @return the total number of movies
	 */
	public int getNumberOfMovies() {
		return movies.length;
	}

	/**
	 * Gets the quantity of different available movies for rental.
	 * 
	 * @return the quantity
	 */
	public int numberAvailableMovies() {
		int available = 0;
		for (Movie movie : movies) {
			if (movie.hasAvailableCopy())
				available++;
		}
		return available;
	}

	/**
	 * Gets the total revenue generated from movie rentals.
	 * 
	 * @return the revenue.
	 */
	public double getTotalRevenue() {
		return totalRevenue;
	}

	/**
	 * Gets the total profit from movie rentals.
	 * 
	 * @return the profit
	 */
	public double getTotalProfit() {
		return totalProfit;
	}

	/**
	 * Filters movies by their year of release.
	 * 
	 * @param year the year of release
	 * @return string containing the filtered movies
	 */
	public String filterByYear(int year) {
		StringBuilder filter = new StringBuilder();
		for (Movie movie : movies) {
			if (movie.getYear() == year)
				filter.append("Title:" + movie.getTitle() + ",Price:" + getCurrencyValue(movie.getPrice())
						+ System.lineSeparator());
		}
		return filter.toString();
	}

	/**
	 * Filters movies by their rental price.
	 * 
	 * @param price the rental price
	 * @return string containing the filtered movies
	 */
	public String filterByPrice(double price) {
		StringBuilder filter = new StringBuilder();
		for (Movie movie : movies) {
			if (movie.getPrice() <= price)
				filter.append("Title:" + movie.getTitle() + ",Price:" + getCurrencyValue(movie.getPrice())
						+ System.lineSeparator());
		}
		return filter.toString();
	}

	/**
	 * Filters available movies.
	 * 
	 * @return string containing the available movies' titles and prices
	 */
	public String filterAvailableMovies() {
		StringBuilder filter = new StringBuilder();
		for (Movie movie : movies) {
			if (movie.hasAvailableCopy())
				filter.append("Title:" + movie.getTitle() + ",Price:" + getCurrencyValue(movie.getPrice())
						+ System.lineSeparator());
		}
		return filter.toString();
	}

	/**
	 * 
	 * Parses the CSV file containing transactions and processes each transaction
	 * based on the given information.
	 * 
	 * @param rentalsFileName the name of the CSV file containing transactions
	 * @return string containing information about the transactions
	 * @throws FileNotFoundException
	 */
	public String activityLog(String rentalsFileName) throws FileNotFoundException {

		StringBuilder infos = new StringBuilder();

		// transaction, clientID, title
		Scanner scanner = new Scanner(new File(rentalsFileName));
		scanner.nextLine(); // skip the header of the file
		scanner.useDelimiter(",");

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] tokens = line.split(",");
			String transaction = tokens[0].trim();
			int clientID = Integer.parseInt(tokens[1].trim());
			String title = tokens[2].trim();

			Movie requestedMovie = null; // if the requested movie isn't in the catalog, it continues to be null
			for (Movie movie : movies) {
				// client may request the movie by its title or code
				if (movie.getTitle().equalsIgnoreCase(title) || movie.getCode().equals(title)) {
					requestedMovie = movie;
				}
			}

			if (requestedMovie == null) {
				infos.append("Movie not found: client " + clientID + " asked for " + title + System.lineSeparator());
			} else {

				if (transaction.equals("rent")) {
					infos.append(rentIfAvailable(requestedMovie, clientID));
				} else if (transaction.equals("return")) {
					infos.append(movieReceiver(requestedMovie, clientID));
				}
			}
		}
		scanner.close();
		return infos.toString();
	}

	/**
	 * Checks the availability of a movie for rental and processes the transaction
	 * accordingly.
	 *
	 * @param movie    the movie being checked for availability
	 * @param clientID the ID of the client attempting to rent the movie
	 * @return a string indicating the outcome of the rental transaction
	 */

	private String rentIfAvailable(Movie movie, int clientID) {

		StringBuilder result = new StringBuilder();

		if (movie.hasAvailableCopy()) {
			result.append("Rental successful: client " + clientID + " rented " + movie.getTitle() + " for "
					+ getCurrencyValue(movie.getPrice()) + System.lineSeparator());
			gotRented(movie, clientID); // movie rental
			double profit = movie.getPrice() - movie.getPrice() * movie.getTax() * 0.01;
			this.totalRevenue += movie.getPrice();
			this.totalProfit += profit;

			result.append("Total: " + getCurrencyValue(movie.getPrice()) + " [" + getCurrencyValue(profit) + "]"
					+ System.lineSeparator());
		} else {
			// all copies are rented
			result.append("Movie currently not available: client " + clientID + " asked for " + movie.getTitle()
					+ System.lineSeparator());
		}

		return result.toString();
	}

	/**
	 * Updates the rental's array when a movie is rented.
	 *
	 * @param movie    the movie being rented
	 * @param clientID the ID of the client renting the movie
	 */
	private void gotRented(Movie movie, int clientID) {

		int[][] updateRentals = null;
		if (movie.getRentals() != null) {
			// increase the matrix by 1 and to add new rental's information
			updateRentals = new int[movie.getRentals().length + 1][2];
			for (int i = 0; i < movie.getRentals().length; i++) {
				updateRentals[i][0] = movie.getRentals()[i][0]; // copies client ID
				updateRentals[i][1] = movie.getRentals()[i][1]; // copies remaining days
			}
			// add the information of the new rental
			updateRentals[movie.getRentals().length][0] = clientID;
			updateRentals[movie.getRentals().length][1] = 7;
		} else { // there wasn't any rentals
			updateRentals = new int[1][2];
			updateRentals[0][0] = clientID;
			updateRentals[0][1] = 7;
		}

		movie.setRentals(updateRentals); // update movie rentals
	}

	/**
	 * Processes the movie's returning
	 * 
	 * @param requestedMovie the movie being returned
	 * @param clientID       the ID of the client returning the movie
	 * @return a string indicating the outcome of the return transaction
	 */
	private String movieReceiver(Movie requestedMovie, int clientID) {

		StringBuilder infos = new StringBuilder();

		// finds the movie´s rental information
		for (int i = 0; i < requestedMovie.getRentals().length; i++) {
			int clientId = requestedMovie.getRentals()[i][0];
			int daysRemaining = requestedMovie.getRentals()[i][1];

			if (clientId == clientID) { // when finding the client, does the returning

				gotReturned(requestedMovie, clientID); // movie returned

				if (daysRemaining < 0) { // overdue
					int days = -1 * daysRemaining;
					double lateFees = days * 2.0;
					double studiosCommision = lateFees * requestedMovie.getTax() * 0.01;

					infos.append("Movie returned with " + days + verifyDays(days) + "of delay: client " + clientID
							+ " returned " + requestedMovie.getTitle() + System.lineSeparator());
					infos.append("Total: " + getCurrencyValue(lateFees) + " ["
							+ getCurrencyValue(lateFees - studiosCommision) + "]" + System.lineSeparator());

					this.totalRevenue += lateFees;
					this.totalProfit += lateFees - studiosCommision;

				} else {
					infos.append("Movie returned: client " + clientID + " returned " + requestedMovie.getTitle()
							+ System.lineSeparator());
					infos.append("Total: $0.00 [$0.00]" + System.lineSeparator());
				}
			}
		}

		return infos.toString();

	}

	/**
	 * Updates the rental information when a movie is returned.
	 *
	 * @param requestedMovieTitle the title of the movie being returned
	 * @param clientID            the ID of the client returning the movie
	 */
	private void gotReturned(Movie movie, int clientID) {

		int[][] updatedRentals = new int[movie.getRentals().length - 1][2]; // decrease length by one to remove
																			// a rented movie
		int index = 0;
		for (int i = 0; i < movie.getRentals().length; i++) {
			// check if the clientID in the current row is equal to the provided client ID
			if (movie.getRentals()[i][0] != clientID) {
				// copies the information unrelated to this client
				updatedRentals[index][0] = movie.getRentals()[i][0]; // client ID
				updatedRentals[index][1] = movie.getRentals()[i][1]; // days remaining
				index++;
			}
		}
		// update movie rentals
		movie.setRentals(updatedRentals);
	}

	/**
	 * Returns a string based on the given number of days
	 * 
	 * @param days the number of days
	 * @return "days" if the number of days is greater than 1, otherwise "day"
	 */
	private String verifyDays(int days) {
		return days > 1 ? " days " : " day ";
	}

	/**
	 * Updates the stock based on the transactions of the day. Writes the stock
	 * state to a CSV file.
	 * 
	 * @param fileName the name of the CSV file to write the updated stock state
	 * @throws IOException
	 */
	public void updateStock(String fileName) {

		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write("Title,Year,Quantity,Rentals,Price,Tax" + System.lineSeparator());

			for (Movie movie : movies) {
				StringBuilder rentalsString = new StringBuilder();
				int[][] rentals = movie.getRentals();
				if (rentals != null) {
					for (int i = 0; i < movie.getRentals().length; i++) {
						int clientID = movie.getRentals()[i][0];
						int daysRemaining = movie.getRentals()[i][1];
						rentalsString.append("(" + clientID + ";" + daysRemaining + ")");
					}
				}

				writer.write(movie.getTitle() + "," + movie.getYear() + "," + movie.getQuantity() + ","
						+ rentalsString.toString() + "," + movie.getPrice() + "," + movie.getTax()
						+ System.lineSeparator());
			}

		} catch (IOException e) {
			System.out.println("ERRO ao escrever ficheiro");
		}
	}

	/**
	 * Formats the given value as a currency string with two decimal places.
	 * 
	 * @param value the value to format
	 * @return the formated value
	 */
	private String getCurrencyValue(double value) {
		return String.format("$%.2f", value).replace(',', '.');
	}

}
