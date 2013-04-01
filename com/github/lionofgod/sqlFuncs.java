/*Name: sqlFuncs.java
 *Author: LionOfGod
 *Description: A class that serves as a back end to TeleMeThere.java.
 *This class handles all of the background work to do with databases.
 */
package com.github.lionofgod;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.SQLite; //Library created by PatPeter to use databases

public class sqlFuncs {
	public SQLite sqlite; // Variable to be used to access database
	
	public sqlFuncs(TeleMeThere plugin, Logger logger, String pluginName,String path, String dbName, String extension){
		//Function responsible for initializing a connection to database/creating a database
		sqlite = new SQLite(logger, pluginName, path, dbName, extension);
		try{
			sqlite.open();
		}catch(Exception e){
			plugin.getLogger().info(e.getMessage());
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}
	public boolean sqlCheckLimit(String tableName){
		//Function responsible for checking if the player has exceeded the amount of saved locations allowed
		//TODO: Allow admin to configure saved locations allowed
		int rowsAllowed = 10;
		try {
			
			ResultSet rs = sqlite.query("SELECT COUNT(*) AS number FROM " + tableName + ";"); // Retrieve number of rows in table and assign it to (afaik) a "row" called number, add row to result set
			if(rs.next()){
				int rows = rs.getInt("number"); // Number row holds an integer representing number of rows
				if(rows == rowsAllowed){ // If player has met maximum number of rows, return true
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public ResultSet sqlList(String tableName){
		//Function responsible for querying database to retrieve player's saved locations
		//Returns ResultSet containing all locations
		try {
			ResultSet rs = sqlite.query("SELECT * FROM " + tableName + ";"); //Select everything from the database
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean sqlTableCheck(String name){
		//Function responsible for checking if a table for a player exists
		//If a table does not exist, the function will create one for the player
		//The player's name is used as the table's name; this function returns true
		if(sqlite.isTable(name)) {
			return true;
		} else{
			try{
			sqlite.query("CREATE TABLE " + name + "(id INT , location VARCHAR(20), cordx INT, cordy INT, cordz INT);");
			} catch (Exception e){
				
			}
			return true;
		}
	}
	
	public boolean sqlCheck(String tableName, String location){
		//Function responsible for checking if a location already exists
		//Will return true if location exists
		//TODO: Result Sets always start before row one, so rs.next() will always be true
		//TODO: Find out what the query returns if the location is not there
		try {
			//Query db for location
			ResultSet rs = sqlite.query("SELECT location FROM " + tableName + " WHERE location = '" +location + "';" );
			if(rs.next()){
				try{
					String retrieved = rs.getString("location");
					if (retrieved.equalsIgnoreCase(location)){
						return true;
					}
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	public boolean sqlInsert(String tableName, String location, int cordX, int cordY, int cordZ){
		//Function responsible for inserting a new location with coordinates into the database, returns true
		try {	
			//Insert the location and coordinates
			sqlite.query("INSERT INTO " + tableName + "(location, cordx, cordy, cordz) VALUES('" + location +"', " + cordX + ", " + cordY + ", " + cordZ + " );" );
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int [] sqlGetCords(String tableName, String location){
		//Function responsible for retrieving a player's saved coordinates from the database
		//Returns an integer array containing the coordinates
		//TODO: Query database for all coords in one query
		int cords [] = {0 , 0 ,0}; // Initialize array to hold coordinates retrieved from table
		
		try {
			ResultSet rsx = sqlite.query("SELECT cordx FROM " + tableName + " where location = '" + location +"';"); // Retrieve x coordinate and put it in result set
			if (rsx.next()){
				try { cords[0] = rsx.getInt("cordx"); // Get coordinate from result set add to array
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			ResultSet rsy = sqlite.query("SELECT cordy FROM " + tableName + " where location = '" + location +"';" ); // Retrieve y coordinate and put it in result set
			if (rsy.next()){
				try { cords[1] = rsy.getInt("cordy"); // Get coordinate from result set add to array
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			ResultSet rsz = sqlite.query("SELECT cordz FROM " + tableName + " where location= '" + location +"';"); // Retrieve z coordinate and put it in result set
			if (rsz.next()){
				try { cords[2] = rsz.getInt("cordz"); // Get coordinate from result set add to array
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return cords; // Return array holding coordinates of location that player requested
		
	}
	
	public boolean sqlChangeCords(String tableName, String location, int cordX, int cordY, int cordZ){
		//Function responsible for changin a player's saved location to new coordinates
		//Returns true on success
		try {
			//Change saved coordinates in table
			sqlite.query("UPDATE " + tableName + " SET cordX ="+ cordX + ", cordY =" + cordY + ", cordZ =" + cordZ +" WHERE location ='"+ location + "';" );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean sqlChangeName(String tableName, String oldLocation, String newLocation){
		//Function responsible for renaming a saved location in the database, returns true
		try {
			//Change location name
			sqlite.query("UPDATE " + tableName + " SET location='" + newLocation + "' WHERE location='" + oldLocation + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean sqlDel(String tableName, String location){
		// Function responsible for deleting a player's saved location
		// Returns true on success
		boolean exists = sqlCheck(tableName, location);
		if(exists){
			try {
				sqlite.query("DELETE FROM "+ tableName + " WHERE location = '" + location +"';" ); // Delete row from table
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
			
		}else{
			return false;
		}
		
	}

	public boolean sqlResetTable(String tableName){
		//Function responsible for deleting all of a players locations
		//Wipes table and returns true
		try {
			sqlite.query("DELETE FROM " + tableName + ";"); //Query database to delete all entries
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void closeConnection(){
		//Function responsible for closing the connection to the sqlite database
		sqlite.close();
	}	
}
	