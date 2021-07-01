package com.example.testaoo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class is mainly responsible for connecting program with mySQL database
 * server and for executing SQL queries to insert, update and retrieving data
 * from the database
 * 
 * @author Hyukjoon Yang
 *
 */
public class SQLConnection{
	static final String ID = "administrator";
	static final String password = "Home0501!";

	private Connection connection;
	private Statement stmt;
	private ResultSet rs;
	private String query;

	/**
	 * The constructor method that initializes the connection to the database
	 */
	public SQLConnection() {
		System.out.println("Testing SQLCOnnection");
		try {
			System.out.println("Connecting to Database");
			Class.forName("com.mysql.jdbc.Driver");
			String endpoint = "jdbc:mysql://project1-instance-1.cmpkjdwxqnze.us-east-2.rds.amazonaws.com";
			connection = DriverManager.getConnection(endpoint, ID, password);
			stmt = connection.createStatement();
			rs = null;
			query = "use covidProject;";
			stmt.execute(query);
			System.out.println("Database Connected");
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("Database Not Connected");
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
			System.out.println("Database Not Connected");
		}
	}


	/**
	 * This method is called to retrieve data of the given country from the database
	 * 
	 * @param country     is the country to be searched
	 * @param infected    is the number of the infected
	 * @param recovered   is the number of the recovered
	 * @param deceased    is the number of the deceased
	 * @param tested      is the number of the tested
	 * @param updateTime  is String representing the updated time
	 * @param moreData    is String for the address where further data is stored
	 * @param historyData is String for the address where historical data is stored
	 * @param sourceURL   is String where the info is based on
	 */
	public void loadData(String country, int infected, int recovered, int deceased, int tested, String updateTime,
			String moreData, String historyData, String sourceURL) {
		try {
			query = "SELECT * FROM countryData WHERE STRCMP(country," + "\"" + country + "\") = 0;";
			rs = stmt.executeQuery(query);

			if (rs.first() == false) {
				insertData(country, infected, recovered, deceased, tested, updateTime, moreData, historyData,
						sourceURL);
			} else {
				// Update
				updateData(country, infected, recovered, deceased, tested, updateTime, moreData, historyData,
						sourceURL);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This inserts new data of a country when the country does not exist in the
	 * database
	 * 
	 * @param country     is the name of the country to be inserted
	 * @param infected    is the number of the infected
	 * @param recovered   is the number of the recovered
	 * @param deceased    is the number of the deceased
	 * @param tested      is the number of the tested
	 * @param updateTime  is String representing the upated time
	 * @param moreData    is String representing the address for further info
	 * @param historyData is String representing the address for historical data
	 * @param sourceURL   is String representing the address where the info is based
	 *                    on
	 */
	public void insertData(String country, int infected, int recovered, int deceased, int tested, String updateTime,
			String moreData, String historyData, String sourceURL) {
		try {
			// Convert the String for updateTime into correct format
			String convertTime = convertDate(updateTime);
			query = "INSERT INTO countryData VALUES(";
			query = query.concat("\"" + country + "\", ");
			query = query.concat(infected + ",");
			query = query.concat(recovered + ",");
			query = query.concat(deceased + ",");
			query = query.concat(tested + ",");
			query = query.concat("\"" + convertTime + "\",");
			query = query.concat("\"" + moreData + "\", ");
			query = query.concat("\"" + historyData + "\", ");
			query = query.concat("\"" + sourceURL + "\"); ");
//			System.out.println(query);
			if (stmt.executeUpdate(query) != 1) {
				throw new SQLException(country + ": Insertion Failed countryData");
			}
			System.out.println(country + ": Insertion Success countryData");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Records the old updates of data for each country
	 * 
	 * @param country    is the name of the country
	 * @param infected   is the number of the infected
	 * @param recovered  is the number of the recovered
	 * @param deceased   is the number of the deceased
	 * @param tested     is the number of the tested
	 * @param updateTime is the String representing the updated time
	 */
	public void insertUpdate(String country, int infected, int recovered, int deceased, int tested, String updateTime) {
		try {
			query = "INSERT INTO countryUpdate VALUES(";
			query = query.concat("\"" + country + "\", ");
			query = query.concat("\"" + updateTime + "\",");
			query = query.concat(infected + ",");
			query = query.concat(recovered + ",");
			query = query.concat(deceased + ",");
			query = query.concat(tested + ");");
//			System.out.println(query);
			stmt.executeUpdate(query);
//			System.out.println(country + ": Insertion Success countryUpdate");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the data of a country and calls insertUpdate to records
	 * the previus data before update
	 * 
	 * @param country    is the name of the country
	 * @param infected   is the number of the infected
	 * @param recovered  is the number of the recovered
	 * @param deceased   is the number of the deceased
	 * @param tested     is the number of the tested
	 * @param updateTime is String representing the updated time
	 */
	public void updateData(String country, int infected, int recovered, int deceased, int tested, String updateTime,
			String moreData, String historyData, String sourceURL) {
		try {
			query = "SELECT * FROM countryData WHERE STRCMP(country," + "\"" + country + "\") = 0;";
			rs = stmt.executeQuery(query);

			// Retrieve the data before update
			if (rs.next()) {
				String latestUpdate = rs.getString("updateTime");
				String convertTime = convertDate(updateTime);

				// Update if the data from the parameters are newer than the existing data
				if (convertTime.compareTo(latestUpdate) > 0) {

					// Record the existing data into the log
					insertUpdate(country, rs.getInt("infected"), rs.getInt("recovered"), rs.getInt("deceased"),
							rs.getInt("tested"), latestUpdate);

					// Update
					query = "UPDATE countryData SET ";
					query = query.concat("infected = " + infected + ", ");
					query = query.concat("recovered = " + recovered + ", ");
					query = query.concat("deceased = " + deceased + ", ");
					query = query.concat("tested = " + tested + ", ");
					query = query.concat("updateTime = \"" + convertTime + "\", ");
					query = query.concat("moreData = \"" + moreData + "\", ");
					query = query.concat("historyData = \"" + historyData + "\", ");
					query = query.concat("sourceURL = \"" + sourceURL + "\" ");
					query = query.concat("WHERE country = \"" + country + "\";");
//					System.out.println(query);
					if (stmt.executeUpdate(query) == 1) {
						System.out.println("Successfully Updated " + country);
					} else {

						// Delete the log if the update is failed
						query = "DELETE FROM countryUpdate WHERE country = \"" + country + "\" and updateTime = \""
								+ latestUpdate + "\";";
						System.out.println(query);
						if (stmt.executeUpdate(query) != 1) {
							throw new SQLException("Failed to update data and delete update" + country);
						} else {
							throw new SQLException("Failed to update data " + country);
						}
					}
				}
			} else {
				throw new SQLException(country + ": Update failed by rs.next()");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the format of time into correct format in String
	 * 
	 * @param time is String representing the time
	 * @return String for the time in correct format
	 */
	private String convertDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(time);
			String formattedTime = output.format(d);
//			System.out.println(formattedTime);
			return formattedTime;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Returns the number of the infected and the deceased for all countries stored
	 * in the database
	 * 
	 * @return ArrayList of String array storing the data of all countries.
	 *         Otherwise, null
	 */
	public ArrayList<String[]> retrieveBriefData() {
		try {
			query = "SELECT country, infected, deceased FROM countryData";
			rs = stmt.executeQuery(query);

			// Retrieves the data of all countries from the database
			ArrayList<String[]> result = new ArrayList<String[]>();

			// Stores the results into the ArrayList
			while (rs.next()) {
				String[] resultCountry = new String[3];
				resultCountry[0] = rs.getString("country");
				int infected = rs.getInt("infected");
				if (infected == -1) {
					resultCountry[1] = "N/A";
				} else {
					resultCountry[1] = infected + "";
				}
				int deceased = rs.getInt("deceased");
				if (deceased == -1) {
					resultCountry[2] = "N/A";
				} else {
					resultCountry[2] = deceased + "";
				}
				result.add(resultCountry);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the all data of a country from the database
	 * 
	 * @param country is the name of the country
	 * @return String[] storing all data of the country. Otherwise, null
	 */
	public String[] retrieveDetailData(String country) {
		query = "SELECT * FROM countryData WHERE country = \"" + country + "\";";
		String[] result = null;
		try {
			rs = stmt.executeQuery(query);

			// Retrieves the data ofo the country from the database
			while (rs.next()) {
				result = new String[9];
				result[0] = rs.getString("country");
				int infected = rs.getInt("infected");
				if (infected == -1) {
					result[1] = "N/A";
				} else {
					result[1] = infected + "";
				}
				int recovered = rs.getInt("recovered");
				if (recovered == -1) {
					result[2] = "N/A";
				} else {
					result[2] = recovered + "";
				}
				int deceased = rs.getInt("deceased");
				if (deceased == -1) {
					result[3] = "N/A";
				} else {
					result[3] = deceased + "";
				}
				int tested = rs.getInt("tested");
				if (tested == -1) {
					result[4] = "N/A";
				} else {
					result[4] = tested + "";
				}
				result[5] = rs.getString("updateTime");
				result[6] = rs.getString("moreData");
				result[7] = rs.getString("historyData");
				result[8] = rs.getString("sourceURL");
			}
			return result;
		} catch (SQLException e) {
		}
		return result;
	}

	/**
	 * Returns the log of updates of a country form the database
	 * 
	 * @param country is the name of the country
	 * @return ArrayList of String array storing the log of updates made for the
	 *         country. Otherwise, null
	 */
	public ArrayList<String[]> retrieveUpdate(String country) {
		query = "SELECT * FROM countryUpdate WHERE country = \"" + country + "\" ORDER BY updateTime DESC;";
		try {
			rs = stmt.executeQuery(query);
			ArrayList<String[]> result = new ArrayList<String[]>();

			// Retrieves the log of updates from the database
			while (rs.next()) {
				String[] update = new String[6];
				update[0] = rs.getString("country");
				update[1] = rs.getString("updateTime");
				int infected = rs.getInt("infected");
				if (infected == -1) {
					update[2] = "N/A";
				} else {
					update[2] = infected + "";
				}
				int recovered = rs.getInt("recovered");
				if (recovered == -1) {
					update[3] = "N/A";
				} else {
					update[3] = recovered + "";
				}
				int deceased = rs.getInt("deceased");
				if (deceased == -1) {
					update[4] = "N/A";
				} else {
					update[4] = deceased + "";
				}
				int tested = rs.getInt("tested");
				if (tested == -1) {
					update[5] = "N/A";
				} else {
					update[5] = tested + "";
				}
				result.add(update);
			}
			if (result.size() == 0) {
				return null;
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public BigDecimal[] sumPopulation() {
//		try {
//			query = "SELECT SUM(c1.population) as populations FROM countryPopulation c1, countryData c2 WHERE c1.country = c2.country;";
//			System.out.println(query);
//			rs = stmt.executeQuery(query);
//			System.out.println("Testing1");
//			BigDecimal[] result = new BigDecimal[2];
//
//			if (rs.next()) {
//				System.out.println("Testing2");
//				result[0] = rs.getBigDecimal(1); // Total populations
//				result[1] = rs.getBigDecimal(2); // Total num. countries
//			}
//			return result;
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.out.println("Testing3");
//			e.printStackTrace();
//			return null;
//		}
//	}

	/**
	 * Returns the total number of the infected of the countries stored in the
	 * database
	 * 
	 * @return Array of BigDecimal where index 0 stores the total number of the
	 *         infected and index 1 stores the number of countries considered in the
	 *         calculation. Otherwise, null
	 */
	public BigDecimal[] sumInfected() {
		try {
			BigDecimal[] result = new BigDecimal[2];
			query = "SELECT SUM(infected) as infected, COUNT(infected) as countries FROM countryData;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				result[0] = rs.getBigDecimal(1); // Total Infected
				result[1] = rs.getBigDecimal(2); // Total Countries
			}
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the total number of the deceased of all countries stored in the
	 * database
	 * 
	 * @return Array of BigDecimal where index 0 stores the total number of the
	 *         deceased and index 1 stores the number of countries considered in the
	 *         calculation. Otherwise, null
	 */
	public BigDecimal[] sumDeceased() {
		try {
			rs = stmt.executeQuery(query);
			BigDecimal[] result = new BigDecimal[2];

			query = "SELECT SUM(deceased) as deceased, COUNT(deceased) as countries FROM countryData WHERE deceased != -1;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				result[0] = rs.getBigDecimal(1); // Total Deceased
				result[1] = rs.getBigDecimal(2); // Total Countries
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is called at the end of the program to end the SQL Connection to
	 * the database
	 */
	public void endConnection() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

			connection.close();

			System.out.println("Connection terminated");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
