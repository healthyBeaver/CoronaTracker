package com.example.testaoo;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * This class is mainly for utilizing the data of countries stored in the
 * database and send the info requested by the program to the main
 *
 * @author user Hyukjoon HJ Yang
 *
 */
public class CountryData extends AsyncTask<String,Void,Void> {

	URL dataURL;
	URLConnection connectURL;
	BufferedReader br;
	SQLConnection connection;

	int infected;
	int tested;
	int recovered;
	int deceased;
	String country;
	String moreData;
	String historyData;
	String sourceURL;
	String updateTime;

	BigDecimal totalInfected;
	BigDecimal totalInfectedCountry;
	//	BigDecimal totalPopulation;
	BigDecimal totalDeceased;
	BigDecimal totalDeceasedCountry;

	boolean loaded;
	boolean connectError;

	/**
	 * Constructor that initilaizes the connection with the online source data
	 */
	public CountryData() {
		loaded = false;
		connectError = false;
//		System.out.println("Connecting to Online Data");
//		try {
//			dataURL = new URL(
//					"https://api.apify.com/v2/key-value-stores/tVaYRsPHLjNdNBu7S/records/LATEST?disableRedirect=true");
//			connectURL = dataURL.openConnection();
//			br = new BufferedReader(new InputStreamReader(connectURL.getInputStream()));
//			System.out.println("Online Data Connected");
//			this.connection = new SQLConnection();
//			this.loadFile();
//		} catch (MalformedURLException e) {
//			System.out.println("Online Data Not Connected");
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("Online Data Not Connected");
//			e.printStackTrace();
//		}
	}


	/**
	 * Override this method to perform a computation on a background thread. The
	 * specified parameters are the parameters passed to {@link #execute}
	 * by the caller of this task.
	 * <p>
	 * This will normally run on a background thread. But to better
	 * support testing frameworks, it is recommended that this also tolerates
	 * direct execution on the foreground thread, as part of the {@link #execute} call.
	 * <p>
	 * This method can call {@link #publishProgress} to publish updates
	 * on the UI thread.
	 *
	 * @param strings The parameters of the task.
	 * @return A result, defined by the subclass of this task.
	 * @see #onPreExecute()
	 * @see #onPostExecute
	 * @see #publishProgress
	 */
	@Override
	protected Void doInBackground(String... strings) {
		System.out.println("Connecting to Online Data");
		try {
			System.out.println("Test");
			dataURL = new URL(
					"https://api.apify.com/v2/key-value-stores/tVaYRsPHLjNdNBu7S/records/LATEST?disableRedirect=true");
			connectURL = dataURL.openConnection();
			br = new BufferedReader(new InputStreamReader(connectURL.getInputStream()));
			System.out.println("Online Data Connected");
			this.connection = new SQLConnection();
			this.loadFile();
		} catch (MalformedURLException e) {
			System.out.println("Online Data Not Connected");
			connectError = true;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Online Data Not Connected");
			connectError = true;
			e.printStackTrace();
		} catch (Exception e){
			System.out.println("Online Data Not Connected");
			connectError = true;
			e.printStackTrace();
		}
		loaded = true;
		return null;
	}

	/**
	 * Loads the data from the online source and apply the updates of the data in
	 * the database
	 */
	public void loadFile() {
		String line = null;
		boolean start = false;
		String[] splitLine = null;
		System.out.println("Updating Database");
		try {

			// Read each line in the data from the online source
			while ((line = br.readLine()) != null) {
				line = line.trim();

				// Start getting info about a country from the online source
				if (line.charAt(0) == '{') {
					start = true;
					continue;
				}

				// Insert/Update new info about a country
				if (line.charAt(0) == '}') {
					start = false;
					// Insert the data into the database
					if (infected != -1) {
						connection.loadData(country, infected, recovered, deceased, tested, updateTime, moreData,
								historyData, sourceURL);
					}
					country = updateTime = moreData = historyData = sourceURL = null;
					infected = recovered = deceased = tested = -1;
					continue;
				}

				// Disregard unimportant lines from the online source
				if (start == false) {
					continue;
				}

				// Retrieve info about a country from the online source
				if (start) {
					splitLine = line.split("\":");

					// Remove comma at the end of the line if exists
					if (splitLine[1].charAt(splitLine[1].length() - 1) == ',') {
						splitLine[1] = splitLine[1].substring(0, splitLine[1].length() - 1);
					}

					// Remove double quotes
					splitLine[0] = splitLine[0].replaceAll("\"", "");
					splitLine[1] = splitLine[1].replaceAll("\"", "");

					splitLine[0] = splitLine[0].toLowerCase();

					// Remove white spaces at the beginning and at the end
					splitLine[0] = splitLine[0].trim();
					splitLine[1] = splitLine[1].trim();
					if (splitLine[0].compareTo("infected") == 0) {
						try {
							infected = Integer.parseInt(splitLine[1]);
						} catch (NumberFormatException e) {
							infected = -1;
						}
					} else if (splitLine[0].compareTo("tested") == 0) {
						try {
							tested = Integer.parseInt(splitLine[1]);
						} catch (NumberFormatException e) {
							tested = -1;
						}
					} else if (splitLine[0].compareTo("recovered") == 0) {
						try {
							recovered = Integer.parseInt(splitLine[1]);
						} catch (NumberFormatException e) {
							recovered = -1;
						}
					} else if (splitLine[0].compareTo("deceased") == 0) {
						try {
							deceased = Integer.parseInt(splitLine[1]);
						} catch (NumberFormatException e) {
							deceased = -1;
						}
					} else if (splitLine[0].compareTo("country") == 0) {
						country = splitLine[1].toUpperCase();
					} else if (splitLine[0].compareTo("moredata") == 0) {
						moreData = splitLine[1];
					} else if (splitLine[0].compareTo("historydata") == 0) {
						historyData = splitLine[1];
					} else if (splitLine[0].compareTo("sourceurl") == 0) {
						sourceURL = splitLine[1];
					} else if (splitLine[0].compareTo("lastupdatedapify") == 0) {
						updateTime = splitLine[1];
					}
				}
			}

			// Calculates the sum of the infected and the deceased
			BigDecimal[] resultInfected = connection.sumInfected();
			BigDecimal[] resultDeceased = connection.sumDeceased();
			if (resultInfected == null | resultDeceased == null) {
				System.out.println("Database Not Updated");
				return;
			}
			totalInfected = resultInfected[0];
			totalInfectedCountry = resultInfected[1];
			totalDeceased = resultDeceased[0];
			totalDeceasedCountry = resultDeceased[1];
			System.out.println("Database Updated");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Database Not Updated");
		}
	}

	/**
	 * Is called on the main page of the app program to display the statistic of the
	 * infected and the deceased
	 */
	public String[] worldData() {
		String[] result = new String[4];
		result[0] = totalInfected + "";
		result[1] = totalInfectedCountry + "";
		result[2] = totalDeceased + "";
		result[3] = totalDeceasedCountry + "";
		return result;
	}

	/**
	 * Prints the number of the infected and the deceased respectively of all
	 * countries stored in the database
	 */
	public ArrayList<String[]> briefInfoCountries() {
		return connection.retrieveBriefData();
//		ArrayList<String[]> result = connection.retrieveBriefData();
//		for (int i = 0; i < result.size(); i++) {
//			System.out.println(result.get(i)[0]);
//			System.out.println("\tInfected: \t" + result.get(i)[1]);
//			System.out.println("\tDeceased: \t" + result.get(i)[2]);
//		}
	}

	/**
	 * Returns the statistics of the COVID status of a country
	 *
	 * @param country    is the name of a country to be searched for COVID info
	 * @return Array of String storing the requested info
	 */
	public ArrayList<String[]> infoCountries(String country) {
		ArrayList<String[]> result = new ArrayList<>();

		// Data of a country
		String[] data = connection.retrieveDetailData(country);
		if (data == null) {
			System.out.println("Data of " + country + " does not exist");
			return null;
		}

		result.add(data);

		// Get the data of the latest update before current data of the country
		ArrayList<String[]> updates = connection.retrieveUpdate(country);

		// Variables to store the difference between the current data and the latest
		// update before the current
		int diffInfected = Integer.MIN_VALUE;
		int diffRecovered = Integer.MIN_VALUE;
		int diffDeceased = Integer.MIN_VALUE;
		int diffTested = Integer.MIN_VALUE;

		// Calculate the differences
		if (updates != null) {
			if (data[1].compareTo("N/A") != 0) {
				try {
					diffInfected = Integer.parseInt(data[1]) - Integer.parseInt(updates.get(0)[2]);
				} catch (NumberFormatException e) {
				}
			}
			if (data[2].compareTo("N/A") != 0) {
				try {
					diffRecovered = Integer.parseInt(data[2]) - Integer.parseInt(updates.get(0)[3]);
				} catch (NumberFormatException e) {
				}
			}
			if (data[3].compareTo("N/A") != 0) {
				try {
					diffDeceased = Integer.parseInt(data[3]) - Integer.parseInt(updates.get(0)[4]);
				} catch (NumberFormatException e) {
				}
			}
			if (data[4].compareTo("N/A") != 0) {
				try {
					diffTested = Integer.parseInt(data[4]) - Integer.parseInt(updates.get(0)[5]);
				} catch (NumberFormatException e) {
				}
			}
		}

		System.out.println(data[0] + " Statistics");
		String[] changes = new String[9];

		// Prints the data of the country
		if (diffInfected == Integer.MIN_VALUE) {
			changes[1] = "N/A";
//			System.out.println("\tInifected: \t" + data[1] + "\t N/A");
//		} else if (diffInfected > 0) {
//			System.out.println("\tInifected: \t" + data[1] + "\t +" + diffInfected);
		} else {
			changes[1] = diffInfected + "";
//			System.out.println("\tInifected: \t" + data[1] + "\t" + diffInfected);
		}

		if (diffRecovered == Integer.MIN_VALUE) {
			changes[2] = "N/A";
//			System.out.println("\tRecovered: \t" + data[2] + "\t N/A");
//		} else if (diffRecovered > 0) {
//			System.out.println("\tRecovered: \t" + data[2] + "\t +" + diffRecovered);
		} else {
			changes[2] = diffRecovered + "";
//			System.out.println("\tRecovered: \t" + data[2] + "\t" + diffRecovered);
		}

		if (diffDeceased == Integer.MIN_VALUE) {
			changes[3] = "N.A";
//			System.out.println("\tDeceased: \t" + data[3] + "\t N/A");
//		} else if (diffRecovered > 0) {
//			System.out.println("\tDeceased: \t" + data[3] + "\t +" + diffDeceased);
		} else {
			changes[3] = diffDeceased + "";
//			System.out.println("\tDeceased: \t" + data[3] + "\t" + diffDeceased);
		}

		if (diffTested == Integer.MIN_VALUE) {
			changes[4] = "N/A";
//			System.out.println("\tTested: \t" + data[4] + "\t N/A");
//		} else if (diffTested > 0) {
//			System.out.println("\tTested: \t" + data[4] + "\t +" + diffTested);
		} else {
			changes[4] = diffTested + "";
//			System.out.println("\tTested: \t" + data[4] + "\t" + diffTested);
		}

		if (data[5] == null | data[5].compareTo("null") == 0) {
			data[5] = "DNE";
		}
		if (data[6] == null | data[6].compareTo("null") == 0) {
			data[6] = "DNE";
		}
		if (data[7] == null | data[7].compareTo("null") == 0) {
			data[7] = "DNE";
		}
		if (data[8] == null | data[8].compareTo("null") == 0) {
			data[8] = "DNE";
		}
		System.out.println("\tUpdate: \t" + data[5]);
		System.out.println("\tMore Data: \t" + data[6]);
		System.out.println("\tHistory: \t" + data[7]);
		System.out.println("\tSource: \t" + data[8]);

		return result;
	}

	/**
	 * Prints the log of the updates done for the country
	 *
	 *
	 * @param country    is the country which updates have been made for
	 */
	public ArrayList<String[]> updateLogCountry(String country) {
		ArrayList<String[]> result = connection.retrieveUpdate(country);
		if (result == null) {
			System.out.println("Such country updates does not exist");
			return null;
		}
		return result;
//		System.out.println(result.get(0)[0] + " Update History");
//		for (int i = 0; i < result.size(); i++) {
//			System.out.println("\"Update \t" + result.get(i)[1]);
//			System.out.println("\tInifected: \t" + result.get(i)[2]);
//			System.out.println("\tRecovered: \t" + result.get(i)[3]);
//			System.out.println("\tDeceased: \t" + result.get(i)[4]);
//			System.out.println("\tTested: \t" + result.get(i)[5]);
//		}
	}

	/**
	 * This method is called at the end of the app program to terminate all
	 * connections
	 */
	public void endConnection() {
		try {
			br.close();
			connection.endConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
