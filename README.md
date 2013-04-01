TeleMeThere
===========

This is a plugin created for Minecraft. This plugin allows for players to save locations and teleport to them easily.

Features
-----------

As of now, this plugin has quite a few features implemented and has more that I want to implement!
Features as of now are....

* Save locations using names, e.g. "base"
* Teleport to saved locations
* Delete saved locations
* Update coordinates and rename saved locations
* Reset all saved locations

This plugin uses a lightweight sqlite database to save and store all player locations. 

Installation
------------

1. Go to the Jars directory and download the jar file from there called TeleMeThere.jar.
2. This plugin uses another library to use sqlite databases. This library was created by PatPeter.
You need to download it's Jar file from [here] (http://dev.bukkit.org/server-mods/sqlibrary/files/5-sqlibrary-4-2/).
His Github can be found [here](https://github.com/PatPeter/SQLibrary) and he also has a [Bukkit-Dev page](http://dev.bukkit.org/server-mods/sqlibrary/).
3. Now just take these Jar files and drop them into your plugins directory. A database will automatically be created once the plugin is run.
4. Enjoy and give me feedback!

Notes
------

###Commands and Usage

To use this plugin simply go stand somewhere and save a location. You can now teleport to this location using the name you saved!
All of the commands that can be used are listed here...

* /tele set <location> "Saves a location, e.g. /tele set mybase"
* /tele <location> "Teleports player to a saved location e.g. /tele mybase"
* /tele list "Lists all saved locations"
* /tele rename <oldLocation> <newLocation> "Renames a saved location e.g. /tele rename mybase skyPalace"
* /tele update <location> "Updates a location to new coordinates, e.g. /tele update mybase"
* /tele del <location> "Delete a saved location e.g. /tele del mybase"
* /tele reset "Remove all saved locations"
