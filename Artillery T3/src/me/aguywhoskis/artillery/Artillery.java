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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;


public class Artillery extends JavaPlugin {
	
    /** * Your plugin's command framework object. */
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
        
		/**
		 * 0: Exp
		 * 1: Level
		 * 2: Kills
		 * 3: Assists
		 * 4: Deaths
		 * 5: Games
		 * 6: Coins
		 **/
        
        Util.initIs();
        ShopHandle.initIs();
        Game.winnerIsLocked = false;
        //ScoreBoard.updateAll();
        
        for (World w:Bukkit.getWorlds()) {
        	w.setAutoSave(false);
        }
        
        Game.changeMode();
        Timer.update();
        
	}
	
	
	public void onDisable() {
		
		for (Player p:Bukkit.getServer().getOnlinePlayers()) {
			p.setDisplayName(p.getName());
			p.teleport(WORLD.mainSpawn);
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

	//World
	@me.aguywhoskis.artillery.framework.Command(name = "world", aliases = {"w"}, description = "Base command for map management", usage = "/world <setspawn:setcore:delcore> <red:blue>", permission = "artillery.admin.world")
    public void world(CommandArgs args) {
		CommandSender sender = args.getSender();
		
        sender.sendMessage(ChatColor.GOLD+"-----"+ChatColor.DARK_AQUA+" All "+ChatColor.GOLD+"/world"+ChatColor.DARK_AQUA+" subcommands "+ChatColor.GOLD+"-----");
        sender.sendMessage(ChatColor.GOLD+"/world setcore <red:blue>"+ChatColor.DARK_AQUA+": Saves the location below you as a core location");
        sender.sendMessage(ChatColor.GOLD+"/world delcore <red:blue> <core number>"+ChatColor.DARK_AQUA+": Deletes the specified core block");
        sender.sendMessage(ChatColor.GOLD+"/world setspawn <red:blue>"+ChatColor.DARK_AQUA+": Saves your current location as the spawn location for the specified team.");
        sender.sendMessage(ChatColor.GOLD+"/world delspawn <red:blue>"+ChatColor.DARK_AQUA+": Deletes the spawn location of the specified team.");
        sender.sendMessage(ChatColor.GOLD+"/world border"+ChatColor.DARK_AQUA+": Toggles border construction mode, placing sponges at your x and z coordinates, but at y=0");
        sender.sendMessage(ChatColor.GOLD+"------------------------------");
        
	}
	
	@me.aguywhoskis.artillery.framework.Command(name = "world.setcore", description = "Adds a core location to the specified team.", usage = "/world setcore <red:blue>", permission = "artillery.admin.world.setcore")
	public void worldSetcore(CommandArgs args) {
		CommandSender sender = args.getSender();
		if (!(sender instanceof Player)) {
			wrongSender(sender);
			return;
		}
		if (args.getArgs().length != 1) {
			wrongArgNum(sender, 2);
			return;
		}
		if (args.getArgs()[0].equalsIgnoreCase("red")|| args.getArgs()[0].equalsIgnoreCase("blue")) {
			
		}
	}
	
	
	
	
	//Game
	@me.aguywhoskis.artillery.framework.Command(name = "game", aliases = {"g"}, description = "Base command for managing games", usage = "/game <start:stop:pause:resume>", permission = "artillery.admin.game")
    public void game(CommandArgs args) {
		CommandSender sender = args.getSender();
		
		sender.sendMessage(ChatColor.GOLD+"-----"+ChatColor.DARK_AQUA+" All "+ChatColor.GOLD+"/game"+ChatColor.DARK_AQUA+" subcommands "+ChatColor.GOLD+"-----");
		sender.sendMessage(ChatColor.GOLD+"/game start"+ChatColor.DARK_AQUA+": Starts the game");
		sender.sendMessage(ChatColor.GOLD+"/game stop"+ChatColor.DARK_AQUA+": Stops the game");
		sender.sendMessage(ChatColor.GOLD+"/game pause"+ChatColor.DARK_AQUA+": Pauses the timer at the current setting");
		sender.sendMessage(ChatColor.GOLD+"/game resume"+ChatColor.DARK_AQUA+": Resumes a paused game");
		sender.sendMessage(ChatColor.GOLD+"------------------------------");
		
	}
	
	
	//Game start
	@me.aguywhoskis.artillery.framework.Command(name = "game.start", aliases = {"g.start"}, description = "Starts a game.", usage = "/game start", permission = "artillery.admin.game.start")
	public void gameStart(CommandArgs args) {
		CommandSender sender = args.getSender();
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
	
	//Game stop
	@me.aguywhoskis.artillery.framework.Command(name = "game.stop", aliases = {"g.stop"}, description = "Stops a game.", usage = "/game stop", permission = "artillery.admin.game.stop")
	public void gameStop(CommandArgs args) {
		CommandSender sender = args.getSender();
		if (PLUGIN.gameMode == 0) {
			sender.sendMessage("The game is not running. Use /game pause to stop just the timer.");
			return;
		}
		CleanUp.runNoWinner();
		Bukkit.getScheduler().cancelTasks(plugin);
		Util.messageServer(PLUGIN.prefix+"&cThe game has been stopped by "+ChatColor.GOLD+sender.getName()+"&c!");
	}
	
	
	//Game pause
	@me.aguywhoskis.artillery.framework.Command(name = "game.pause", aliases = {"g.pause","game.p","g.p"}, description = "Pauses the timer", usage = "/game pause", permission = "artillery.admin.game.pause")
	public void gamePause(CommandArgs args)  {
		CommandSender sender = args.getSender();
		if (PLUGIN.isPaused) {
			sender.sendMessage("The game is already paused!");
			return;
		}
		Timer.pause();
		for (BukkitTask t:Bukkit.getScheduler().getPendingTasks()) {
			Bukkit.broadcastMessage(""+t);
			Class c = t.getClass();
			
		}
	}
	
	//Game fakeupdate
	@me.aguywhoskis.artillery.framework.Command(name = "game.fakeupdate",aliases = {"g.fu","game.fu","g.fakeupdate","game.timer"}, description = "Updates the pre-game timer with a fake number of players.",permission = "artillery.admin.game.fakeupdate")
	public void gameFakeupdate(CommandArgs args) {
		CommandSender sender = args.getSender();
		Bukkit.broadcastMessage(args.getArgs().length+"");
		if (!(args.getArgs().length == 1)) {
			sender.sendMessage(ChatColor.RED+"Invalid arguments!");
			game(null);
			return;
		}
		int n = 0;
		try {
			n = Integer.parseInt(args.getArgs()[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED+"Invalid arguments! Use a whole number for argument 2.");
			return;
		}
		Timer.update(n);
	}
	
	//Game resume
	@me.aguywhoskis.artillery.framework.Command(name = "game.resume", aliases = {"game.unpause","game.r","g.r","g.u"}, description = "Pauses the timer", usage = "/game pause", permission = "artillery.admin.game.pause")
	public void gameResume(CommandArgs args)  {
		CommandSender sender = args.getSender();
		PLUGIN.pendingTasks = Bukkit.getScheduler().getPendingTasks();
		BukkitTask task = PLUGIN.pendingTasks.get(0);
		for (BukkitTask t:PLUGIN.pendingTasks) {

		}
	}
	
	
	
	
	public static void wrongSender(CommandSender sender) {
		sender.sendMessage("You must be a player to do that.");
	}
	
	public static void wrongArgType(CommandSender sender, String wrongArg, String expectedArg) {
		sender.sendMessage(ChatColor.RED+"Invalid argument, \""+wrongArg+"\", "+expectedArg+" expected.");
	}
	
	public static void wrongArgNum(CommandSender sender, int n) {
		sender.sendMessage("Invalid arguments, "+n+" args expected.");
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
