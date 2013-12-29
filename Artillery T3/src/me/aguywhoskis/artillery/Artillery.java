package me.aguywhoskis.artillery;

import me.aguywhoskis.artillery.event.BlockHandle;
import me.aguywhoskis.artillery.event.PlayerHandle;
import me.aguywhoskis.artillery.event.ShopHandle;
import me.aguywhoskis.artillery.framework.CommandArgs;
import me.aguywhoskis.artillery.framework.CommandFramework;
import me.aguywhoskis.artillery.thread.CleanUp;
import me.aguywhoskis.artillery.thread.tick.Timer;
import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.PLUGIN;
import me.aguywhoskis.artillery.util.ScoreBoard;
import me.aguywhoskis.artillery.util.TNTManager;
import me.aguywhoskis.artillery.util.Util;
import me.aguywhoskis.artillery.util.WORLD;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Artillery extends JavaPlugin {
	
    CommandFramework framework;
    
    public static Plugin plugin;

	
	public void onEnable() {
		Artillery.plugin = this;
		File schemDir = new File(getDataFolder(), "cannons");
		File playerDir = new File (getDataFolder(), "players");
		schemDir.mkdirs();
		playerDir.mkdirs();
		

		Bukkit.getPluginManager().registerEvents(new TNTManager(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerHandle(), this);
        Bukkit.getPluginManager().registerEvents(new BlockHandle(), this);
        Bukkit.getPluginManager().registerEvents(new ShopHandle(), this);

        WORLD.mainSpawn = WORLD.loadMainSpawn();
        
        framework = new CommandFramework(plugin);
        
        /** This will register all commands inside of this class. It works much the same as the registerEvents() method. Note: Commands do not need to be registered in plugin.yml! */
        framework.registerCommands(this);
        framework.registerHelp();
        
        for (Player p: Bukkit.getOnlinePlayers()) {
        	Game.exp.put(p.getName(), Game.getStatFromFile(p.getName(), 0));
        	Game.kills.put(p.getName(), 0);
        	Game.assists.put(p.getName(), 0);
        	Game.deaths.put(p.getName(), 0);
        	Game.kills.put(p.getName(), 0);
        	Game.coins.put(p.getName(), Game.getStatFromFile(p.getName(), 6));
        	ScoreBoard.create(p);
		}
                
        Util.initIs();
        ShopHandle.initIs();
        
        for (World w:Bukkit.getWorlds()) {
        	w.setAutoSave(false);
        }
        
        Game.changeMode();
        Timer.update();
        
	}
	
	
	public void onDisable() {
		
		for (Player p:Bukkit.getServer().getOnlinePlayers()) {
			p.setDisplayName(p.getName());
			if (!p.getWorld().equals(WORLD.main)) {
				p.teleport(WORLD.mainSpawn);
			}
			p.getInventory().clear();
		}
		
		WORLD.unload(WORLD.map.getName());
		
		Game.teamBlue.clear();
		Game.teamRed.clear();
		Game.exp.clear();
		Game.kills.clear();
		Game.assists.clear();
		Game.deaths.clear();
		Game.winner = null;

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "The game has been stopped.");
		Bukkit.broadcastMessage("");
	}

	//Cmd_World*
	@me.aguywhoskis.artillery.framework.Command(name = "world", description = "Base command for map management", usage = "/world <setspawn:setcore:delcore> <red:blue>", permission = "artillery.admin.world.world")
    public void world(CommandArgs args) {
		CommandSender sender = args.getSender();
        sender.sendMessage(ChatColor.GOLD+"-----"+ChatColor.DARK_AQUA+" All "+ChatColor.GOLD+"/world"+ChatColor.DARK_AQUA+" subcommands "+ChatColor.GOLD+"-----");
        sender.sendMessage(ChatColor.GOLD+"/world setcore <red:blue>"+ChatColor.DARK_AQUA+": Saves the location below you as a core location");
        sender.sendMessage(ChatColor.GOLD+"/world delcore <red:blue> <core number>"+ChatColor.DARK_AQUA+": Deletes the specified core block");
        sender.sendMessage(ChatColor.GOLD+"/world setspawn <red:blue>"+ChatColor.DARK_AQUA+": Saves your current location as the spawn for the specified team.");
        sender.sendMessage(ChatColor.GOLD+"/world border"+ChatColor.DARK_AQUA+": Toggles border construction mode, placing sponges at y = 0 underneath you.");
        sender.sendMessage(ChatColor.GOLD+"/world <world>"+ChatColor.DARK_AQUA+": Teleports you to the specified world.");
        sender.sendMessage(ChatColor.GOLD+"/worlds"+ChatColor.DARK_AQUA+": Lists all valid worlds");
        sender.sendMessage(ChatColor.GOLD+"------------------------------");
	}
	
	//Cmd_Worlds
	@me.aguywhoskis.artillery.framework.Command(name = "worlds", description = "Lists all valid worlds.", usage = "/worlds", permission = "artillery.admin.world.worlds")
	public void worlds(CommandArgs args) {
		CommandSender sender = args.getSender();
		ArrayList<String> list = WORLD.getAllWorlds();
		sender.sendMessage(ChatColor.GREEN+"All valid worlds:");
		for (String s:list) {
			sender.sendMessage(ChatColor.RED + s);
		}
	}
	
	//Cmd_WorldBorder
	@me.aguywhoskis.artillery.framework.Command(name = "world.border", description = "Toggles border construction mode", usage = "/world border", permission = "artillery.admin.world.border")
	public void worldBorder(CommandArgs args) {
		CommandSender sender = args.getSender();
		if (checkArgs(sender, args.getArgs(), null, true) == false) {
			return;
		}
		String p = sender.getName();
		if (PlayerHandle.borderPlayerList.contains(p)) {
			PlayerHandle.borderPlayerList.remove(p);
			sender.sendMessage(ChatColor.GREEN + "Border mode disabled.");
		} else {
			PlayerHandle.borderPlayerList.add(p);
			sender.sendMessage(ChatColor.GREEN + "Border mode enabled.");
		}
	}
	
	//Cmd_WorldTeleport
	@me.aguywhoskis.artillery.framework.Command(name = "world.teleport", aliases = {"world.tp","w.tp","w.teleport"}, description = "Teleports you to the specified world", usage = "/world teleport <world>", permission = "artillery.admin.world.teleport")
	public void worldTeleport(CommandArgs args) {
		CommandSender sender = args.getSender();
		if (checkArgs(sender, args.getArgs(), null, true) == false) {
			return;
		}
		
		String name = "";
		for (int i = 0; i < args.getArgs().length; i++) {
			name = name + args.getArgs()[i] + " ";
		}
		name = name.trim();
		Player p = (Player) sender;
		if (WORLD.isValid(name)) {
			WORLD.load(name);
			p.sendMessage(ChatColor.GREEN + "Teleporting to map \""+name+"\"...");
			p.teleport(Bukkit.getWorld(name).getSpawnLocation());							
		} else {
			p.sendMessage(ChatColor.RED+"The world \""+name+"\" does not exist.");
		}
	}
	
	//Cmd_WorldSetcore
	@me.aguywhoskis.artillery.framework.Command(name = "world.setcore", description = "Adds a core location to the specified team.", usage = "/world setcore <red:blue>", permission = "artillery.admin.world.setcore")
	public void worldSetcore(CommandArgs args) {
		
		CommandSender sender = args.getSender();
		if (checkArgs(sender, args.getArgs(), new Class[] {String.class}, true) == false) {
			return;
		}
		
		Player p = (Player) sender;
		String team = args.getArgs()[0];
		
		if (!(team.equalsIgnoreCase("blue")) && !(team.equalsIgnoreCase("red"))) {
			p.sendMessage(ChatColor.RED+"Please specify which team; red or blue.");
			return;
		}	
		
		
		if (p.getWorld() == WORLD.main) {
			p.sendMessage(ChatColor.RED+"You can't do that in the main world.");
			return;
		}
		
		try {
			Location loc = Util.roundLoc(p.getLocation());
			loc.setY(loc.getY()-1);
			if (loc.getBlock().getType().equals(Material.BEACON)) {
				int i = WORLD.getAvailBeaconFileName(loc.getWorld(), team);
				WORLD.saveLocToFile(loc, team+"core"+i+".loc");
				p.sendMessage(ChatColor.GREEN+"Saved location as: "+team+"core"+i+".loc");
			} else {
				p.sendMessage(ChatColor.RED+"You must be standing on a beacon to do that.");
			}
		} catch (IOException e) {
			p.sendMessage(ChatColor.RED + "An error occured when saving the "+team+" core. Please try again.");
			e.printStackTrace();
		}
	}
	
	//Cmd_WorldSetspawn
	@me.aguywhoskis.artillery.framework.Command(name = "world.setspawn", description = "Sets the spawn of the specified team.", usage = "/world setspawn <red:blue>", permission = "artillery.admin.world.setspawn")
	public void worldSetspawn(CommandArgs args) {
		CommandSender sender = args.getSender();
		if (checkArgs(sender, args.getArgs(), new Class[] {String.class}, true) == false) {
			return;
		}
		Player p = (Player) sender;
		String team = args.getArgs()[0];
		if (!(team.equalsIgnoreCase("blue")) && !(team.equalsIgnoreCase("red"))) {
			p.sendMessage(ChatColor.RED+"Please specify which team; red or blue.");
			return;
		}
		if (!p.getWorld().equals(WORLD.main)) {
			p.sendMessage(ChatColor.RED+"You can't do that in the main world.");
		}

		try {
			team = team.toLowerCase();
			WORLD.saveLocToFile(p.getLocation(), team+"spawn.loc");
			team = WordUtils.capitalize(team);
			p.sendMessage(ChatColor.GREEN+team+" spawn saved succesfully.");
		} catch (IOException e) {
			p.sendMessage(ChatColor.RED + "An error occured when setting the "+team.toLowerCase()+" spawn.");
		}
	}
	
	//Cmd_WorldSave
	@me.aguywhoskis.artillery.framework.Command(name = "world.save", aliases = {"world.s"},description = "Saves the world you are currently in.", usage = "/world save", permission = "artillery.admin.world.save")
	public void worldSave(CommandArgs args) {
		CommandSender sender = args.getSender();
		
		if (checkArgs(sender, args.getArgs(), null, false) == false) {
			return;
		}
		World w = null;
		if (sender instanceof Player) {
			w = ((Player) sender).getWorld();
		} else {
			String name = "";
			for (int i = 0; i < args.getArgs().length; i++) {
				name = name + args.getArgs()[i] + " ";
			}
			name = name.trim();
			if (WORLD.isValid(name)) {
				w = Bukkit.getWorld(name);
			} else {
				sender.sendMessage(ChatColor.RED+"The world \""+name+"\" does not exist.");
				return;
			}
		}
		if (WORLD.map.equals(w)) {
			sender.sendMessage(ChatColor.RED+"You can't do that if the game is running in world \""+w.getName()+"\".");
			return;
		}
		w.save();
		for (Player p:Bukkit.getOnlinePlayers()) {
			if (p.isOp()) {
				p.sendMessage(ChatColor.GRAY+sender.getName()+" saved world \""+w.getName()+"\".");
			}
		}
		sender.sendMessage(ChatColor.GREEN+"Saved world \""+w.getName()+"\" succesfully.");
	}
	
	//Cmd_WorldDelcore
	@me.aguywhoskis.artillery.framework.Command(name = "world.delcore", description = "Deletes a core location of the specified team.", usage = "/world delcore <red:blue> <core number>", permission = "artillery.admin.world.delcore")
	public void worldDelcore(CommandArgs args) {
		CommandSender sender = args.getSender();
		World w = null;
		int num;
		String team;
		if (sender instanceof Player) {
			if (checkArgs(sender, args.getArgs(), new Class[] {String.class, Integer.class}, true) == false) {
				return;
			}
			
			Player p = (Player) sender;
			w = p.getWorld();
			num = Integer.parseInt(args.getArgs()[1]);
			team = args.getArgs()[0];
			
		} else {
			if (checkArgs(sender, args.getArgs(), new Class[] {String.class, Integer.class, String.class}, false) == false) {
				return;
			}

			String name = "";
			for (int i = 2; i < args.getArgs().length; i++) {
				name = name + args.getArgs()[i] + " ";
			}
			name = name.trim();
			w = Bukkit.getWorld(name);
			team = args.getArgs()[0];
			num = Integer.parseInt(args.getArgs()[1]);
				
		}
		if (!(team.equalsIgnoreCase("blue")) && !(team.equalsIgnoreCase("red"))) {
			sender.sendMessage(ChatColor.RED+"Please specify which team; red or blue.");
			return;
		}
		if (!WORLD.isValid(w.getName())) {
			sender.sendMessage(ChatColor.RED+"The world \""+w.getName()+"\" does not exist.");
			return;
		}
			
		if (!WORLD.coreExists(w, team, num)) {
			sender.sendMessage(ChatColor.RED+"Core #"+num+" does not exist in world \""+w.getName()+"\".");
			return;
		}
		WORLD.delCore(w, team, num);
		sender.sendMessage(ChatColor.GREEN+"Deleted "+args.getArgs()[0]+" core #"+num+".");
	}
	
	//Cmd_Game*
	@me.aguywhoskis.artillery.framework.Command(name = "game", aliases = {"g"}, description = "Base command for managing games", usage = "/game <start:stop:pause:resume>", permission = "artillery.admin.game.game")
    public void game(CommandArgs args) {
		CommandSender sender = args.getSender();
		
		sender.sendMessage(ChatColor.GOLD+"-----"+ChatColor.DARK_AQUA+" All "+ChatColor.GOLD+"/game"+ChatColor.DARK_AQUA+" subcommands "+ChatColor.GOLD+"-----");
		sender.sendMessage(ChatColor.GOLD+"/game start"+ChatColor.DARK_AQUA+": Starts the game");
		sender.sendMessage(ChatColor.GOLD+"/game stop"+ChatColor.DARK_AQUA+": Stops the game");
		sender.sendMessage(ChatColor.GOLD+"/game pause"+ChatColor.DARK_AQUA+": Pauses the timer at the current setting");
		sender.sendMessage(ChatColor.GOLD+"/game resume"+ChatColor.DARK_AQUA+": Resumes a paused game");
		sender.sendMessage(ChatColor.GOLD+"------------------------------");
		
	}
	
	//Cmd_GameSkip
	@me.aguywhoskis.artillery.framework.Command(name = "game.skip", aliases = "game.s", description = "Skips the current timer.", usage = "/game skip", permission = "artillery.admin.game.skip")
	public void gameSkip(CommandArgs args) {
		CommandSender sender = args.getSender();
		Bukkit.getScheduler().cancelTasks(plugin);
		Game.changeMode();
		Bukkit.broadcastMessage(ChatColor.GOLD+sender.getName() +ChatColor.GREEN+" has skipped the timer.");
	}
	
	//Cmd_GameStart
	@me.aguywhoskis.artillery.framework.Command(name = "game.start", aliases = {"g.start"}, description = "Starts a game.", usage = "/game start", permission = "artillery.admin.game.start")
	public void gameStart(CommandArgs args) {
		CommandSender sender = args.getSender();
		if (checkArgs(sender, args.getArgs(), null, false) == false) {
			return;
		}
		
		if (PLUGIN.started) {
			sender.sendMessage(ChatColor.RED+"The game is already started!");
			return;
		}
		if (PLUGIN.isPaused == true) {
			sender.sendMessage(ChatColor.RED+"The game is paused. Please resume the game before starting.");
			return;
		}
		PLUGIN.gameMode = 1;
		Game.changeModeSim1();
		Util.messageServer(PLUGIN.prefix+"&cThe game has been started by "+ChatColor.GOLD+sender.getName()+"&c!");
	}
	
	//Cmd_GameStop
	@me.aguywhoskis.artillery.framework.Command(name = "game.stop", aliases = {"g.stop"}, description = "Stops a game.", usage = "/game stop", permission = "artillery.admin.game.stop")
	public void gameStop(CommandArgs args) {
		CommandSender sender = args.getSender();
		if (checkArgs(sender, args.getArgs(), null, false) == false) {
			return;
		}
		if (PLUGIN.gameMode == 0) {
			sender.sendMessage("The game is not running. Use /game pause to stop just the timer.");
			return;
		}
		CleanUp.runNoWinner();
		Bukkit.getScheduler().cancelTasks(plugin);
		Util.messageServer(PLUGIN.prefix+"&cThe game has been stopped by "+ChatColor.GOLD+sender.getName()+"&c!");
	}
	
	//Cmd_GamePause
	@me.aguywhoskis.artillery.framework.Command(name = "game.pause", aliases = {"g.pause","game.p","g.p"}, description = "Pauses the timer", usage = "/game pause", permission = "artillery.admin.game.pause")
	public void gamePause(CommandArgs args)  {
		CommandSender sender = args.getSender();
		if (checkArgs(sender, args.getArgs(), null, false) == false) {
			return;
		}
		if (PLUGIN.isPaused) {
			sender.sendMessage("The game is already paused!");
			return;
		}
		if (PLUGIN.gameMode == 0) {
			Timer.pause();
		} else {
			Bukkit.getScheduler().cancelTasks(plugin);
			Util.messageServer(PLUGIN.prefix+"The timer has been paused by "+sender.getName()+"!");
		}
		
	}

	//Cmd_GameResume
	@me.aguywhoskis.artillery.framework.Command(name = "game.resume", aliases = {"game.unpause","game.r","g.r","g.u"}, description = "Pauses the timer", usage = "/game pause", permission = "artillery.admin.game.pause")
	public void gameResume(CommandArgs args)  {
		PLUGIN.gameMode -= 1;
		Game.changeMode();
	}
	
	
	//Cmd_GameFakeupdate
	@me.aguywhoskis.artillery.framework.Command(name = "game.fakeupdate",aliases = {"g.fu","game.fu","g.fakeupdate","game.timer"}, description = "Updates the pre-game timer with a fake number of players.",permission = "artillery.admin.game.fakeupdate")
	public void gameFakeupdate(CommandArgs args) {
		CommandSender sender = args.getSender();
		Bukkit.broadcastMessage(args.getArgs().length+"");
		if (checkArgs(sender, args.getArgs(), new Class[] {Integer.class}, false) == false) {
			return;
		}
		int n = Integer.parseInt(args.getArgs()[0]);
		Timer.update(n);
		sender.sendMessage(ChatColor.GREEN+"Updated.");
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean checkArgs(CommandSender sender, String[] args, Class[] argTypes, boolean senderIsPlayer) {
		if (senderIsPlayer && !(sender instanceof Player)) {
			sender.sendMessage("You must be a player to do that.");
			return false;
		}
		if (argTypes != null) {
			
			if (args.length < argTypes.length) {
				sender.sendMessage(ChatColor.RED+"Invalid arguments, "+(argTypes.length-args.length)+" more argument(s) expected.");
				return false;
			}	
			
			ArrayList<Class> baseTypes = new ArrayList<Class>(Arrays.asList(argTypes));
			ArrayList<Class> inTypes = new ArrayList<Class>();
			
			//Add all argument "types" (String, double, integer) to an arraylist:
			for (String s: args) {
				Class type = String.class;
				try {
					Integer.parseInt(s);
					type = Integer.class;
				} catch (NumberFormatException e) {
					try {
						Double.parseDouble(s);
						type = Double.class;
					} catch (NumberFormatException nfe) {
	
					}
				}
				inTypes.add(type);
			}
			
			//check 
			for (int i=0; i<baseTypes.size(); i++) {
				if (inTypes.size() >= baseTypes.size()) {
					if (baseTypes.get(i) != inTypes.get(i)) {
						sender.sendMessage(ChatColor.RED+"Invalid argument, \""+args[i]+"\", "+baseTypes.get(i).getSimpleName()+" expected.");
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return framework.handleCommand(sender, label, cmd, args);
		/**if (sender.isOp()) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must be a player to do that.");
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("a")) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("setspawn")) {
						
						Player p = (Player) sender;
						if (p.getLocation().getWorld() != WORLD.main) {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("blue")) {
									try {
										WORLD.saveLocToFile(p.getLocation(), "bluespawn.loc");
										p.sendMessage("Set.");
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when setting the blue spawn.");
										e.printStackTrace();
									}
								} else if (args[1].equalsIgnoreCase("red")) {
									try {
										WORLD.saveLocToFile(p.getLocation(), "redspawn.loc");
										p.sendMessage("Set.");
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when setting the red spawn.");
										e.printStackTrace();
									}
								}	
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid arguments. "+ChatColor.GREEN + "Use /cc setspawn <red/blue>");	
							}
						}
						
					} else if (args[0].equalsIgnoreCase("world")) {
						String name = "";
						for (int i = 1; i < args.length; i++) {
							name = name + args[i] + " ";
						}
						name = name.trim();
						Player p = (Player) sender;
						if (WORLD.isValid(name)) {
							WORLD.load(name);
							World currentWorld = p.getLocation().getWorld();
							p.sendMessage(ChatColor.GREEN + "Teleporting to map \""+name+"\"...");
							if (name == WORLD.main.getName()) {
								p.teleport(WORLD.mainSpawn);
							} else {
								p.teleport(Bukkit.getWorld(name).getSpawnLocation());
								if (currentWorld != Bukkit.getWorlds().get(0) && (currentWorld.getPlayers().size() == 0)) {
									WORLD.unload(currentWorld.getName());
								}	
							}							
						} else if (name.startsWith("--create")){
							name = name.replaceFirst("--create", "");
							name = name.trim();
							if (!WORLD.isValid(name)) {
								p.sendMessage(ChatColor.GREEN+"Creating world \""+name+"\"...");
								WORLD.load(name);
								World w = Bukkit.getWorld(name);
								w.setAutoSave(false);
								w.setMonsterSpawnLimit(0);
							} else {
								p.sendMessage(ChatColor.RED+"That world already exists.");
							}
						} else {
							p.sendMessage("Invalid command");
						}

						
					} else if (args[0].equalsIgnoreCase("worlds")) {
						ArrayList<String> list = WORLD.getAllWorlds();
						sender.sendMessage(ChatColor.GREEN+"All valid worlds:");
						for (String s:list) {
							sender.sendMessage(ChatColor.RED + s);
						}
					} else if (args[0].equalsIgnoreCase("start")) {
						if (PLUGIN.gameMode == 0) {
							Game.changeMode();
						}
					} else if (args[0].equalsIgnoreCase("test")) {
						Player p = (Player) sender;
						p.getInventory().clear();
						Util.giveInventory(p);
						Game.coins.put(p.getName(), Game.coins.get(p.getName()) +100);
						ScoreBoard.update(p);
						
					} else if (args[0].equalsIgnoreCase("setcore")) {
						
						Player p = (Player) sender;
						if (p.getLocation().getWorld() != WORLD.main) {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("blue")) {
									try {
										Location loc = Util.roundLoc(p.getLocation());
										loc.setY(loc.getY()-1);
										if (loc.getBlock().getType().equals(Material.BEACON)) {
											int i = WORLD.getAvailBeaconFileName(loc.getWorld(), "blue");
											WORLD.saveLocToFile(loc, "bluecore"+i+".loc");
											p.sendMessage("Saved location as: bluecore"+i+".loc");
										}
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when saving the blue core.");
										e.printStackTrace();
									}
								} else if (args[1].equalsIgnoreCase("red")) {
									try {
										Location loc = Util.roundLoc(p.getLocation());
										loc.setY(loc.getY()-1);
										if (loc.getBlock().getType().equals(Material.BEACON)) {
											int i = WORLD.getAvailBeaconFileName(loc.getWorld(), "red");
											WORLD.saveLocToFile(loc, "redcore"+i+".loc");
											p.sendMessage("Saved location as: redcore"+i+".loc");
										}
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when saving the blue core.");
										e.printStackTrace();
									}
								}	
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid arguments. "+ChatColor.GREEN + "Use /a setcore <red/blue>");	
							}
						}
						
						
					
					} else if (args[0].equalsIgnoreCase("delcore")) {
						Player p = (Player) sender;
						if (args.length == 3) {
							try {
								int num = Integer.parseInt(args[2]);
								if (args[1].equalsIgnoreCase("red")) {
									WORLD.delCore(p.getWorld(), "red", num);
								} else if (args[1].equalsIgnoreCase("blue")) {
									WORLD.delCore(p.getWorld(), "blue", num);
								}
							} catch (NumberFormatException e) {
								p.sendMessage("Proper usage: /cc delcore <red:blue> <number>");
							}
						}
					} else if (args[0].equalsIgnoreCase("skip")) {
					
					
						Bukkit.getScheduler().cancelTasks(plugin);
						Game.changeMode();
						Bukkit.broadcastMessage(sender.getName() +" skipped the timer.");
					} else if (args[0].equalsIgnoreCase("save")) {
						Player p = (Player) sender;
						World w = p.getWorld();
						w.save();
						p.sendMessage("Saved world \""+w.getName()+"\".");
					} else if (args[0].equalsIgnoreCase("stop")) {
						sender.sendMessage("Stopping...");
						Bukkit.getScheduler().cancelTasks(plugin);
						sender.sendMessage("Stopped.");
						PLUGIN.gameMode = 0;
					} else if (args[0].equalsIgnoreCase("start")) {
						sender.sendMessage("Starting...");
						Game.changeMode();
					} else if (args[0].equalsIgnoreCase("fakeupdate")) {
						if (args.length > 1) {
							Timer.update(Integer.parseInt(args[1]));
						}
					} else if (args[0].equalsIgnoreCase("border")) {
						String p = sender.getName();
						if (PlayerHandle.borderPlayerList.contains(p)) {
							PlayerHandle.borderPlayerList.remove(p);
							sender.sendMessage(ChatColor.GREEN + "Border mode disabled.");
						} else {
							PlayerHandle.borderPlayerList.add(p);
							sender.sendMessage(ChatColor.GREEN + "Border mode enabled.");
						}
					}
				}
			}
		}
		return true;
		**/
	}
}
