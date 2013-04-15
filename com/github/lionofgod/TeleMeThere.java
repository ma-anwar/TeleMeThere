/*Name: TeleMeThere.java
 *Author: LionOfGod
 *Description: A plugin for minecraft that allows players to save locations and teleport to them easily
 */
package com.github.lionofgod;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleMeThere extends JavaPlugin {
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
	sqlFuncs sqlDb; // This variable will be used to access sqlFuncs class
	public static boolean onlyOP = true; 

	
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
	
	public void setOnlyOP(boolean onlyOP){
		if(TeleMeThere.onlyOP != onlyOP){
			TeleMeThere.onlyOP = onlyOP;
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		if(commandLabel.equalsIgnoreCase("teleadmin")){
			if (sender.isOp() || sender instanceof ConsoleCommandSender){
				if(args.length == 0){
					sender.sendMessage(ChatColor.GREEN + "\nTele Me There!\nAdmin Commands\nUsage: /teleadmin setusage op \"Set plugin to allow only operator use\"" +
							"\n/teleadmin setusage everyone \"Set plugin to allow everyone to use it\"");
					return true;
				}
				if(args.length == 2 && args[0].equalsIgnoreCase("setusage")){
					if(args[1].equalsIgnoreCase("op")){
						setOnlyOP(true);
						sender.sendMessage(ChatColor.GREEN +"Plugin has been set to OP use only!");
						return true;
					}
					else if(args[1].equalsIgnoreCase("everyone")){
						setOnlyOP(false);
						sender.sendMessage(ChatColor.GREEN + "Everyone can use the plugin now!");
						return true;
					}
					else{
						sender.sendMessage(ChatColor.RED + "Invalid use of command");
						return true;
					}
				}
				
			}else{
				sender.sendMessage(ChatColor.RED + "This command is not available to you!");
			}
		}
		//Make sure that the command sender is a player
		if(onlyOP && !(sender.isOp())){
			sender.sendMessage(ChatColor.RED + "This command is not available to you!");
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage("These commands are meant to be run as a player!");
			return true;
		}
		Player player = (Player) sender; // Cast sender to type Player
		
		boolean table = sqlDb.sqlTableCheck(player.getDisplayName());// Check if a table for the player already exists, else create one
		if(!table){
			player.sendMessage(ChatColor.RED + "Unidentified error!");
		}
		
		teleFuncs tele = new teleFuncs(player, args, sqlDb);// Create a new teleFuncs object
		// All commands begin with tele, e.g. "/tele <command> <args>"
		if(commandLabel.equalsIgnoreCase("tele")){
			// "/tele" Print all possible commands and uses
			if(args.length ==  0){
				tele.help();
				return true;
			} 
			// "/tele reset" Reset all of player's saved locations
			else if (args.length == 1 && args[0].equalsIgnoreCase("reset")){
				if(tele.reset())
					return true;
				else
					return false;
			}
			
			else if (args.length == 1){
				// "/tele list" List all saved locations
				if(args[0].equalsIgnoreCase("list")){
					tele.list();
					return true;
				}
				//Teleport player to location
				else{
					player.sendMessage("Hey");
					if(tele.tele())
						return true;
					else
						return false;
				}			
			}
			//Save a new location to database
			else if (args.length == 2 && args[0].equalsIgnoreCase("set")){
				if(tele.set())
					return true;
				else
					return false;
			}
			//Delete a saved location
			else if (args.length == 2 && args[0].equalsIgnoreCase("del")){
				if(tele.delete())
					return true;
				else
					return false;
				}
			}
			//Change a location to new coordinates
			else if(args.length == 2 && args[0].equalsIgnoreCase("update")){
				if(tele.update())
					return true;
				else
					return false;
			}
			//Rename a location
			else if(args.length == 3 && args[0].equalsIgnoreCase("rename")){
				if(tele.rename())
					return true;
				else
					return false;
			}		
		return false;				
	}
}