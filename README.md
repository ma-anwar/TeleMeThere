TeleMeThere V1.2
================

This is a plugin created for Minecraft. This plugin allows for players to save locations and teleport to them easily.
Update: I just added functionality to allow players to teleport with their mounts. This means that players can teleport while riding horses and their horse will come with them! : D

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

1. Head over to my [bukkit-dev](http://dev.bukkit.org/server-mods/tele-me-there/) page and [download](http://dev.bukkit.org/server-mods/tele-me-there/files/) the latest jar file for my plugin.
2. This plugin uses another library to use sqlite databases. This library was created by PatPeter.
You need to download it's Jar file from [here] (http://dev.bukkit.org/server-mods/sqlibrary/files/5-sqlibrary-4-2/).
His Github can be found [here](https://github.com/PatPeter/SQLibrary) and he also has a [Bukkit-Dev page](http://dev.bukkit.org/server-mods/sqlibrary/).
The latest version of the plugin is comaptible with SQLibrary 6.0 and 4.0.
3. Now just take these Jar files and drop them into your plugins directory. A database will automatically be created once the plugin is run.
4. Enjoy and give me feedback!

Other Stuff
------

###Commands and Usage

To use this plugin simply go stand somewhere and save a location. You can now teleport to this location using the name you saved!
All of the commands that can be used are listed here...

* /tele set \<location> "Saves a location, e.g. /tele set mybase"
* /tele \<location> "Teleports player to a saved location e.g. /tele mybase"
* /tele list "Lists all saved locations"
* /tele rename \<oldLocation> \<newLocation> "Renames a saved location e.g. /tele rename mybase skyPalace"
* /tele update \<location> "Updates a location to new coordinates, e.g. /tele update mybase"
* /tele del \<location> "Delete a saved location e.g. /tele del mybase"
* /tele reset "Remove all saved locations"

###Configuration
I made use of the config.yml file for some simple configuration.
Now, when the plugin runs it will generate a default config.yml file.
It's pretty barebones right now, but it's self explanatory.
The onlyOP variable can be set to true if you want only operators to sue the plugin or false if everyone can use the plugin.


###Future Features
In the future I want to implement more features that admins can use, some features that I can think of right now are...

* Wiping the whole database
* Removing player tables
* Configuring how many locations can be saved
* Make sure dependency on sqLibrary is met

###Help

I would really appreciate it if I could get some feedback about my code or stuff I should implement : D

It would be awesome if I could get help implementing new features : )

