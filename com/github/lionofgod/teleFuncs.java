/*Name: teleFuncs.java
 *Author: LionOfGod
 *Description: This class will handle all the commands that a player executes and will make calls to sqlFuncs in order to carry out sql queries.
 */
package com.github.lionofgod;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class teleFuncs {
	private Player player;
	private String [] args;
	private sqlFuncs sqlDb; // This variable will be used to access sqlFuncs class
	private String playerName;	
	private TeleMeThere plugin;
	
	public teleFuncs(Player player, String[] args, sqlFuncs sqlDb){
		//Constructor will retrieve variables necessary for interaction with player and DB
		this.player = player;// Get player object
		this.args = args; // Get all arguments passed
		this.playerName = player.getDisplayName(); //Players name will be used as table name
		this.sqlDb = sqlDb;// Get db object
	}
	
	public boolean help(){
		//Function responsible for explaining commands, returns true
		player.sendMessage(ChatColor.YELLOW + "Tele Me There! Beta : D");
		player.sendMessage(ChatColor.DARK_GREEN + "Usage: /tele set <location> \"Set a location\" \n/tele <location> \"Teleport to location\" "
				+ "\n/tele list \"List saved locations\" \n/tele rename <oldLocation> <newLocation> \"Rename a location\""
				+ "\n/tele update <location> \"Update a location to new coordinates\"\n/tele del <location> \"Remove a saved location\""
				+ "\n/tele reset \"Remove all saved locations!\"\" ");
		return true;
	}
	
	public boolean reset(){
		//Function responsible for deleteing all saved locations, returns true on success
		boolean result = sqlDb.sqlResetTable(playerName); //Call function to wipe table
		if(result){ //
			player.sendMessage(ChatColor.GREEN + "All locations deleted!");
			return true;
		}else{
			return false;
		}
	}
	
	public boolean list(){
		//Function responsible for listing all of players saved locations, returns true
		ResultSet locations  = sqlDb.sqlList(playerName); // Retrieve resultset containing all saved locations from database
		try {
			locations.next(); // Forward to next location, loop will print first location twice if I don't do this 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {						
			while(!locations.isAfterLast()){ //Make sure cursor is not after last location
				//First string should be at left side, 
				String message = String.format("%-15s", locations.getString("location")); //Retrieve location from column "location"
				locations.next(); // Go to next row in resultset
				if(locations.isAfterLast()){ //If the cursor is now after the last row, send just one location
					player.sendMessage(ChatColor.YELLOW + message);
				}
				else{// Else add next location to message, pad it to the right and send it to player
					message += String.format(" %-15s", locations.getString("location"));
					player.sendMessage(ChatColor.YELLOW + message);
					locations.next();// Move cursor forward one row
				}		
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
				return true;
	}
	
	public boolean tele(){
		player.sendMessage("We are here!");
		//Function responsible for releporting player to saved location, returns true on success
		String location = args[0]; // Retrieve name of location that player would like to set
		String [] badNames = {"list", "set", "rename", "update", "del", "reset"}; // Make sure player is not setting the location to the name of a command
		for(String x : badNames){
			if (location.equalsIgnoreCase(x)){
				player.sendMessage(ChatColor.RED + "Invalid name of location, this is a command!");
				return false; // Return false if player passes command as location name
			}
		}
		boolean exists = sqlDb.sqlCheck(playerName, location); //Check if the location already exists within the database
		if(!exists){ // If it does not exist, exit
			player.sendMessage(ChatColor.RED + "This location has not been set!");
			return false;
		}
		int cords []  = {0, 0 ,0 }; // Create new array to hold coordinates from sqlGetCords function
		cords = sqlDb.sqlGetCords(playerName, location);//pass sqlGetCords location that player wants, playerName = table name
		// tp command is used to teleport player
		// It was not possible to use api command, player.teleport(), as it takes a location object retrieved through player.getLocation()
		// I could not figure out how to store an object within a database and so the tp command is used... it will work even if player is not OP
		player.sendMessage("So far so good!");
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + playerName + " " + cords[0] + " " + cords[1] + " " + cords[2]); // Teleport player
		player.sendMessage(ChatColor.GREEN + "You have been teleported to '" + args[0] + "'.");
		return true;	
	}
	
	public boolean set(){
		//Function responsible for saving a player's location, returns true on success
		boolean exists = sqlDb.sqlCheck(playerName, args[1]); // Check if the player has already saved the location
		if(exists){
			player.sendMessage(ChatColor.RED + "This location already exists, to rename a location use /tele rename <location> \nTo change the cordinates of a location"
					+ " type /tele update <location>");
			return true;
		}
		boolean rowLimit = sqlDb.sqlCheckLimit(playerName); // Check if the player has exceeded the number of locations allowed to be saved
		if(rowLimit){ // If true, exit
			player.sendMessage(ChatColor.RED + "You have reach the maxmimum amount of locations saved. \n Please delete a location");
			return false;
		}				
		Location playerLoc = player.getLocation(); // Get the player's location
		// Use location object to get x cord, y cord and z cord
		int locX = playerLoc.getBlockX();
		int locY = playerLoc.getBlockY();
		int locZ = playerLoc.getBlockZ();
		sqlDb.sqlInsert(playerName, args[1], locX, locY, locZ); // Add the location to the data base
		player.sendMessage(ChatColor.GREEN + "'" + args[1] +"' has been saved as a location."); 
		return true;
	}
	
	public boolean update(){
		//Function responsible for updating players saved location to new coordinates, returns true on success
		String location = args[1];
		boolean exists = sqlDb.sqlCheck(playerName, location); // Make sure location exists
		if(exists){
			Location playerLoc = player.getLocation(); // Get player location
			int locX = playerLoc.getBlockX(); // Use player location to retrieve x,y and z coordinates
			int locY = playerLoc.getBlockY();
			int locZ = playerLoc.getBlockZ();
			sqlDb.sqlChangeCords(playerName, location, locX, locY, locZ ); // Call sqlChangeCords to update the location to new coordinates
			player.sendMessage(ChatColor.GREEN + location +" has been updated to new coordinates");
			return true;
		}else{
			player.sendMessage(ChatColor.RED + "Error: Location specified does not exist!");
			return false;
		}
	}
	
	public boolean rename(){
		//Function responsible for renaming player's saved location, returns true on success
		String oldLocation = args[1];
		String newLocation = args[2];
		boolean exists = sqlDb.sqlCheck(playerName, oldLocation); //Make sure location to be renamed exists
		if(exists){
			sqlDb.sqlChangeName(playerName, oldLocation, newLocation);//call sqlChangeName to rename location
			player.sendMessage(ChatColor.GREEN + oldLocation + " has been renamed to "+ newLocation);
			return true;
		}else{
			player.sendMessage(ChatColor.GREEN + "Error: Location specified does not exist!");
			return false;
		}
	}
	
	public boolean delete(){
		//Function responsible for deleting a player's saved location, returns true on success	
		String location = args[1];
		boolean deleted = sqlDb.sqlDel(playerName, location); //sqlDel checks if location is valid or not, so we don't have to call any checking methods
		if(deleted){
			player.sendMessage(ChatColor.GREEN + location + " has been deleted.");
			return true;
		}else{
			player.sendMessage(ChatColor.RED + "Unsuccesful, location most likely does not exist!");
			return false;
		
		}
	}
}