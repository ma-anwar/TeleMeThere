/*Name: TeleMeThere.java
 *Author: LionOfGod
 *Description: A plugin for minecraft that allows players to save locations and teleport to them easily
 */
package com.github.lionofgod;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleMeThere extends JavaPlugin {
	//TODO: Package all the code in onCommand into another class and have onCommand call functions from there
	/*TODO: Stuff to be implemeneted
	 *  - More admin features
	 *  	-Delete a players table
	 *  	-Permissions for which player can use the plugin?
	 *  		-Have a table with list of eligible players to use plugin
	 *  		-Add, delete functionality for table
	 *  	-Change the number of max allowed saved locations
	 *  	-Allow admin functions to be run straight from console
	 *  -Get sql functions to check if locations are valid or exist etc?
	 */
	public final Logger logger = Logger.getLogger("Minecraft"); // Get logger object
	public static TeleMeThere plugin;
	sqlFuncs sqlDb; // This variable will be used to acces sqlFuncs class
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has been enabled!" );
		sqlDb = new sqlFuncs(plugin, this.logger, "TeleMeThere",this.getDataFolder().getAbsolutePath(),"Teleportation", ".sqlite");
	}
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has been enabled!");
		sqlDb.closeConnection();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		//Make sure that the command sender is a player
		if(!(sender instanceof Player)){
			sender.sendMessage("These commands are meant to be run as a player!");
			return false;
		}
		Player player = (Player) sender; // Assume that the sender is player
		String playerName = player.getDisplayName(); //Players name will be used as table name
		boolean table = sqlDb.sqlTableCheck(playerName);// Check if a table for the player already exists, else create one
		if(!table){
			player.sendMessage(ChatColor.RED + "Unidentified error!");
		}
		// All commands begin with tele
		if(commandLabel.equalsIgnoreCase("tele")){
			// "/tele" Print all possible commands and uses
			if(args.length ==  0){
				player.sendMessage(ChatColor.YELLOW + "Tele Me There! Beta : D");
				player.sendMessage(ChatColor.DARK_GREEN + "Usage: /tele set <location> \"Set a location\" \n/tele <location> \"Teleport to location\" "
						+ "\n/tele list \"List saved locations\" \n/tele rename <oldLocation> <newLocation> \"Rename a location\""
						+ "\n/tele update <location> \"Update a location to new coordinates\"\n/tele del <location> \"Remove a saved location\""
						+ "\n/tele reset \"Remove all saved locations!\"\" ");
				return true;
			} 
			// "/tele reset" Reset all of player's saved locations
			else if (args.length == 1 && args[0].equalsIgnoreCase("reset")){
				boolean result = sqlDb.sqlResetTable(playerName); //Call function to wipe table
				if(result){ //
					player.sendMessage(ChatColor.GREEN + "All locations deleted!");
				}
			}
			
			else if (args.length == 1){
				// "/tele list" List all saved locations
				if(args[0].equalsIgnoreCase("list")){
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
				// Teleport player to location
				else{
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
					getServer().dispatchCommand(getServer().getConsoleSender(), "tp " + playerName + " " + cords[0] + " " + cords[1] + " " + cords[2]); // Teleport player
					player.sendMessage(ChatColor.GREEN + "You have been teleported to '" + args[0] + "'.");
					return true;	
				}			
			}
			//Save a new location to database
			else if (args.length == 2 && args[0].equalsIgnoreCase("set")){
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
			//Delete a saved location
			else if (args.length == 2 && args[0].equalsIgnoreCase("del")){
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
			//Change a location to new coordinates
			else if(args.length == 2 && args[0].equalsIgnoreCase("update")){
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
			//Rename a location
			else if(args.length == 3 && args[0].equalsIgnoreCase("rename")){
				String oldLocation = args[1];
				String newLocation = args[2];
				boolean exists = sqlDb.sqlCheck(playerName, oldLocation); //Make sure location to be renamed exists
				if(exists){
					sqlDb.sqlChangeName(playerName, oldLocation, newLocation);//call sqlChangeName to rename location
					player.sendMessage(ChatColor.GREEN + oldLocation + " has been renamed to "+ newLocation);
				}else{
					player.sendMessage(ChatColor.GREEN + "Error: Location specified does not exist!");
				}
				
			}
			
		}
		return false;				
	}
}