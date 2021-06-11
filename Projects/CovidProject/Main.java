import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * This class is the main class executing the program.
 * 
 * @author Hyukjoon Yang
 *
 */
public class Main {

	static CountryData countryData;
	static SQLConnection connection;
	static Scanner scnr;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		scnr = new Scanner(System.in);

		// Initialize the app program
		countryData = new CountryData();
		connection = new SQLConnection();
		countryData.loadFile(connection);

		while (true) {

			/*** Main page of the program ***/
			countryData.worldData(); // Display the total number of the infected and the deceased
			countryData.briefInfoCountries(connection); // Display the list of countries with the number of the infected
														// and the deceased
			System.out.println("Type \"Update\", \"(Country Name)\", or \"Exit\""); // Options available in the main
																					// page
			String input = scnr.nextLine();
			input = input.toLowerCase();

			if (input.compareTo("update") == 0) { // Update the data and remains on the main page
				countryData.loadFile(connection);
			} else if (input.compareTo("exit") == 0) { // Ends the program
				break;
			}

			/*** Page for displaying the all data of a country ***/
			else {

				String[] result = countryData.infoCountries(connection, input.toUpperCase()); // Display the data of the
																								// country
				String country = input.toUpperCase();
				if (result == null) {
					continue;
				}

				System.out.println("Type \"More\", \"History\", \"Source\", \"Updates\" or \"Back\""); // Options
																										// available in
																										// the page
				input = scnr.nextLine();

				// Once user selects back, Move to the main page
				while (input.toLowerCase().compareTo("back") != 0) {

					// Once selected, open the address in the web browser and remains on the same
					// page
					if (input.compareTo("more") == 0) {
						try {
							URI uri = new URI(result[6]);
							java.awt.Desktop.getDesktop().browse(uri);
						} catch (URISyntaxException | IOException e) {
							System.out.println("Cannot get access to the page");
						}
					} 
					
					// Once selected, open the address in the web browser and remains on the same page
					else if (input.compareTo("history") == 0) { 
						try {
							URI uri = new URI(result[7]);
							java.awt.Desktop.getDesktop().browse(uri);
						} catch (URISyntaxException | IOException e) {
							System.out.println("Cannot get access to the page");
						}
					} 
					
					// Once selected, open the address in the web browser and remains on the same page
					else if (input.compareTo("source") == 0) {
						try {
							URI uri = new URI(result[8]);
							java.awt.Desktop.getDesktop().browse(uri);
						} catch (URISyntaxException | IOException e) {
							System.out.println("Cannot get access to the page");
						}

					} 
					// Once selected, Move to a different page displaying the log of updates of the country
					else if (input.compareTo("updates") == 0) {
						
						/*** Page for displaying the log of updates made for the country ***/
						countryData.updateLogCountry(connection, country);
						// Option available for the page
						// Back: Back to the previous page
						// Home: Back to the main page
						
					} else {
						System.out.println("Invalid option selected");
					}
					System.out.println("Type \"More\", \"History\", \"Source\", \"Updates\" or \"Back\"");
					input = scnr.nextLine().toLowerCase();
				}
			}
		}
		
		
		// Once the app is closed
		countryData.endConnection();
		connection.endConnection();
	}

}
