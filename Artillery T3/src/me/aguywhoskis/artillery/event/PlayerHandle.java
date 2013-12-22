package me.aguywhoskis.artillery.event;

import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import me.aguywhoskis.artillery.Artillery;
import me.aguywhoskis.artillery.thread.DelayDamageEvent;
import me.aguywhoskis.artillery.thread.DelayDeathEvent;
import me.aguywhoskis.artillery.thread.RemoveFired;
import me.aguywhoskis.artillery.thread.tick.Forfeit;
import me.aguywhoskis.artillery.thread.tick.Timer;
import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.PLUGIN;
import me.aguywhoskis.artillery.util.ScoreBoard;
import me.aguywhoskis.artillery.util.TNTManager;
import me.aguywhoskis.artillery.util.Util;
import me.aguywhoskis.artillery.util.WORLD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;


public class PlayerHandle implements Listener {

	static String prefix = "&0[&2!&0]&r ";
	public static ArrayList<String> borderPlayerList = new ArrayList<String>();
	
	


	Material[] id = { Material.COAL_BLOCK, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK };
	Plugin myplugin = Artillery.plugin;

	@EventHandler(priority = EventPriority.NORMAL)
	public void OnTurretFire(PlayerToggleSneakEvent e) {
		
		if (PLUGIN.canShoot) {
		if (e.getPlayer().isSneaking()) {
			Player p = e.getPlayer();
			
			//Location of player:
			Location main = new Location(Bukkit.getWorld(p.getLocation().getWorld().getName()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p
					.getLocation().getYaw(), p.getLocation().getPitch());
			main.setY(main.getY()-1);
			
			//Location of alleged middle of turret:
			Location idenLoc = Util.roundLoc(p.getLocation());
			idenLoc.setY(idenLoc.getY()-1);
			
			
			//Checking if there is a cannon under the player
					if (TNTManager.registeredLaunchers.containsKey(idenLoc)) {
						if (!PLUGIN.turretDelay.contains(idenLoc)) {

							Material type = idenLoc.getBlock().getType();
							BukkitScheduler s = Bukkit.getScheduler();

							main.setY(main.getY() + 1);

							if (type.equals(Material.DIAMOND_BLOCK)) {

								Location from = main;
								Location to = p.getTargetBlock(null, 200).getLocation();
								Vector mid = TNTManager.getTntVelocity(from,to, 15);
								mid.multiply(1.45);
								TNTManager.spawnTNT(p.getName(), main, idenLoc ,mid, 51, 50);
								s.scheduleSyncDelayedTask(myplugin,new RemoveFired(myplugin, idenLoc),800L); // 40 seconds

							} else if (type.equals(Material.EMERALD_BLOCK)) {

								double radians = Math.toRadians(main.getYaw() + 90 * 0);
								double a = (10 * Math.sin(radians));
								double b = (10 * Math.cos(radians));
								final float newZ1 = (float) (main.getZ() + a);
								final float newX1 = (float) (main.getX() + b);
								final float newZ2 = (float) (main.getZ() + a* -1);
								final float newX2 = (float) (main.getX() + b* -1);

								// 2 blocks 90 degrees (10 blocks away) to the players left/right (see a1 and b1):
								Block l = Bukkit.getWorld(main.getWorld().getName()).getBlockAt((int) newX1, (int) main.getY() - 1,(int) newZ1);
								Block r = Bukkit.getWorld(main.getWorld().getName()).getBlockAt((int) newX2, (int) main.getY() - 1,(int) newZ2);

								Location from = main;
								Location to = p.getTargetBlock(null, 200).getLocation();

								Vector left = TNTManager.getTntVelocity(r.getLocation(), to, 5);
								Vector mid = TNTManager.getTntVelocity(from,to, 5);
								Vector right = TNTManager.getTntVelocity(l.getLocation(), to, 5);

								TNTManager.spawnTNT(p.getName(), from, idenLoc,left, 41, 60);
								TNTManager.spawnTNT(p.getName(), from, idenLoc,mid, 41, 60);
								TNTManager.spawnTNT(p.getName(), from, idenLoc,right, 41, 60);

								s.scheduleSyncDelayedTask(myplugin,new RemoveFired(myplugin, idenLoc),600L); //30  seconds

							} else if (type.equals(Material.GOLD_BLOCK)) {
								double radians = Math.toRadians(main.getYaw() + 90 * 0);
								double a = (6 * Math.sin(radians));
								double b = (6 * Math.cos(radians));
								final float newZ1 = (float) (main.getZ() + a);
								final float newX1 = (float) (main.getX() + b);
								final float newZ2 = (float) (main.getZ() + a* -1);
								final float newX2 = (float) (main.getX() + b* -1);

								// 2 blocks 90 degrees (10 blocks away) to the players left/right (see a1 and b1):
								Block l = Bukkit.getWorld(main.getWorld().getName()).getBlockAt((int) newX1, (int) main.getY() - 1,(int) newZ1);
								Block r = Bukkit.getWorld(main.getWorld().getName()).getBlockAt((int) newX2, (int) main.getY() - 1,(int) newZ2);

								Location from = main;
								Location to = p.getTargetBlock(null, 200).getLocation();

								Vector left = TNTManager.getTntVelocity(r.getLocation(), to, 5);
								Vector right = TNTManager.getTntVelocity(l.getLocation(), to, 5);

								TNTManager.spawnTNT(p.getName(), from, idenLoc,left, 31, 50);
								TNTManager.spawnTNT(p.getName(), from, idenLoc,right, 31, 50);
								s.scheduleSyncDelayedTask(myplugin,new RemoveFired(myplugin, idenLoc),400L); // 20 seconds

							} else if (type.equals(Material.IRON_BLOCK)) {

								double radians = Math.toRadians(main.getYaw() + 90 * 0);
								double a = (2 * Math.sin(radians));
								double b = (2 * Math.cos(radians));
								final float newZ1 = (float) (main.getZ() + a);
								final float newX1 = (float) (main.getX() + b);
								final float newZ2 = (float) (main.getZ() + a* -1);
								final float newX2 = (float) (main.getX() + b* -1);

								// 2 blocks 90 degrees (10 blocks away) to the players left/right (see a1 and b1):
								Block l = Bukkit.getWorld(main.getWorld().getName()).getBlockAt((int) newX1, (int) main.getY() - 1,(int) newZ1);
								Block r = Bukkit.getWorld(main.getWorld().getName()).getBlockAt((int) newX2, (int) main.getY() - 1,(int) newZ2);

								Location from = main;
								Location to = p.getTargetBlock(null, 200).getLocation();

								Vector left = TNTManager.getTntVelocity(r.getLocation(), to, 5);
								Vector right = TNTManager.getTntVelocity(l.getLocation(), to, 5);

								TNTManager.spawnTNT(p.getName(), from, idenLoc,left, 21, 60);
								TNTManager.spawnTNT(p.getName(), from, idenLoc,right, 21, 60);
								s.scheduleSyncDelayedTask(myplugin,new RemoveFired(myplugin, idenLoc),200L); // 10 seconds

							} else if (type.equals(Material.COAL_BLOCK)) {

								Location from = main;

								Location to = p.getTargetBlock(null, 200).getLocation();

								Vector mid = TNTManager.getTntVelocity(from,to, 12);
								mid.multiply(1.2);

								TNTManager.spawnTNT(p.getName(), from, idenLoc,mid, 11, 90);
								s.scheduleSyncDelayedTask(myplugin,new RemoveFired(myplugin, idenLoc), 100L); // 5 seconds

							}

							Bukkit.getWorld(main.getWorld().getName()).createExplosion(main, 0F);
							PLUGIN.turretDelay.add(idenLoc);
							
						} else {
							p.sendMessage(ChatColor.DARK_GRAY+"Reloading turret...");
						}
					}
				}

		 }
	}

	private String objPrefix = (ChatColor.BLACK+"["+ChatColor.DARK_GREEN+"Objective"+ChatColor.BLACK+"] "+ChatColor.RESET);
	
	@EventHandler
	public void addPlayer(PlayerJoinEvent e) {
		String p = e.getPlayer().getName();
		File directory = new File(myplugin.getDataFolder(), "players");
		directory.mkdir();
		File f = new File(directory, p + ".txt");
		if (!f.exists()) {
			try {
				f.createNewFile();
				FileWriter writer = new FileWriter(f);
				writer.write("0,0,0,0,0,0,0");
				// level, exp, kills, assists, deaths, games
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				Bukkit.getLogger().info("[WARNING] Could not create file " + f);
			}
		}
		if (!Game.assists.containsKey(p)) {
        	Game.exp.put(p, Game.getStat(p, 0));
        	Game.kills.put(p, 0);
        	Game.assists.put(p, 0);
        	Game.deaths.put(p, 0);
        	Game.kills.put(p, 0);
        	Game.coins.put(p, Game.getStat(p, 6));
        	if (Game.coins.get(p) == 0) {
        		Game.coins.put(p, 200);
        	}
		}
		
		Player pl = e.getPlayer();
		ScoreBoard.create(pl);
		
		

		
		
		
		if (PLUGIN.gameMode != 1) {
			if (!Game.teamBlue.contains(p) && !Game.teamRed.contains(p)) {
				Game.assignTeam(pl);
				Util.giveInventory(pl);
			} else if (Game.teamBlue.contains(p)) {
				pl.setDisplayName(ChatColor.BLUE +pl.getName()+ChatColor.WHITE);
			} else if (Game.teamRed.contains(p)) {
				pl.setDisplayName(ChatColor.RED +pl.getName()+ChatColor.WHITE);
			}
			if (!Forfeit.timer.containsKey(p)) {
				Util.logInfo("does not contain key");
				if (Game.teamBlue.contains(p)) {
					pl.teleport(WORLD.blueSpawn);
				} else {
					pl.teleport(WORLD.redSpawn);
				}
				
			}
			if (PLUGIN.gameMode == 1) {
				//lobby
				pl.sendMessage(objPrefix + ChatColor.GREEN + "The game will start shortly.");
			} else {
				if (PLUGIN.gameMode == 2) {
				// building
				pl.sendMessage(objPrefix + ChatColor.GREEN+ "Set up defences and prevent the other team from destroying your core!");
			} else if (PLUGIN.gameMode == 3) {
				//battling
				pl.sendMessage(objPrefix + ChatColor.GREEN+ "Destroy the other team's core!");
			} else if (PLUGIN.gameMode == 4) {
				//restarting
				pl.sendMessage(objPrefix + ChatColor.GREEN+ "The game is over.");
				}
			}
		
			if (Forfeit.timer.containsKey(p)) {
				Forfeit.timer.remove(p);
			}
			
		} else {
			pl.setGameMode(GameMode.SURVIVAL);
			e.setJoinMessage(null);
			Timer.update();
			pl.teleport(WORLD.main.getSpawnLocation());
			pl.getInventory().clear();
			Util.messageServer(prefix+ChatColor.GOLD+p+ChatColor.RED+" has joined! "+ChatColor.GOLD+"("+ChatColor.GREEN+Bukkit.getOnlinePlayers().length+"/"+Bukkit.getServer().getMaxPlayers()+ChatColor.GOLD+")");
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDroppedExp(0);
		e.getDrops().clear();
		if (e.getEntity().getLastDamageCause().getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
			e.setDeathMessage(null);
			//Delay the event. This is to give the plugin time to add metadata to the player when an explosion hurts them.
			//(See Label A1 in me.aguywhoskis.event.PlayerHandle.onExplosion)
			Bukkit.getScheduler().scheduleSyncDelayedTask(myplugin,new DelayDeathEvent(myplugin, e), 2L);
		} else {
			Player killed = (Player) e.getEntity();
			if (killed.getKiller() != null) {
				Player killer = killed.getKiller();
				Game.addCoins(killer, 50, "Kill");
				Game.kills.put(killer.getName(), Game.kills.get(killer.getName()) +1);
				ScoreBoard.update(killer);
				Game.deaths.put(killed.getName(), Game.deaths.get(killed.getName()) + 1);
				ScoreBoard.update(killed);
			} else {
				Game.deaths.put(killed.getName(), Game.deaths.get(killed.getName()) + 1);
				ScoreBoard.update(killed);
			}
		}
	}

	public static void delayedDeathEvent(PlayerDeathEvent e) {

		if (e.getEntity() instanceof Player) {
			Player killee = e.getEntity().getPlayer();
			String killeeName = killee.getName();

			Game.deaths.put(killeeName, Game.deaths.get(killeeName) + 1);
			ScoreBoard.update(killee);
			if (killee.getLastDamageCause().getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
				if (killee.hasMetadata("hitShooter")) {
					Player shooter = Bukkit.getServer().getPlayer(killee.getMetadata("hitShooter").get(0).asString());
					Player turretOwner = Bukkit.getServer().getPlayer(killee.getMetadata("hitTurretOwner").get(0).asString());

					String prefix = "&0[&2!&0]&r ";
					String deathMessage = null;

					if (turretOwner != shooter) {

						if (!turretOwner.equals(killee)) {
							Game.assists.put(turretOwner.getName(),Game.assists.get(turretOwner.getName()) + 1);
							Game.addCoins(turretOwner, 25, "Assist");
						}
						Game.kills.put(shooter.getName(),Game.kills.get(shooter.getName()) + 1);
						Game.addCoins(shooter, 50, "Kill");
						
						ScoreBoard.update(turretOwner);
						ScoreBoard.update(shooter);

						deathMessage = ("&3" + killee.getDisplayName()+ " &7has been killed by &3"+ shooter.getDisplayName() +
								" &7using "+ turretOwner.getDisplayName() + "'s &7turret!");

					} else {
						if (turretOwner.getName() == killeeName) {
							deathMessage = "&3" + killee.getDisplayName()+ " &7blew themselves up!";
						} else {
							Game.kills.put(shooter.getName(),Game.kills.get(shooter.getName()) + 1);
							Game.exp.put(shooter.getName(), Game.exp.get(shooter.getName()) +25);
							Game.addCoins(shooter, 50, "Kill");
							deathMessage = (killee.getDisplayName()+ " &7was blown up by &3" + shooter.getDisplayName());
							ScoreBoard.update(shooter);
						}
					}
					prefix = ChatColor.translateAlternateColorCodes('&', prefix);
					deathMessage = ChatColor.translateAlternateColorCodes('&',deathMessage);
					Bukkit.broadcastMessage(prefix + deathMessage);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
			if (e.getEntity() instanceof Player) {
				//Delay the event. This is to give the plugin time to add metadata to the player when an explosion hurts them.
				//(See Label A1 in me.aguywhoskis.event.PlayerHandle.onExplosion)
				Bukkit.getScheduler().scheduleSyncDelayedTask(myplugin,new DelayDamageEvent(myplugin, e), 2L);
			}
		}
	}
	
	public static void DelayedDamageEvent (EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player damagedP = (Player) e.getEntity();
		String damaged = damagedP.getName();
		String shooter = damagedP.getMetadata("hitShooter").get(0).asString();
		
		if (shooter.equals(damagedP.getName())) {
			return;
		}
		
		if (Game.teamBlue.contains(damaged)) {
			if (Game.teamBlue.contains(shooter)) {
				e.setCancelled(true);
			}
		}

		if (Game.teamRed.contains(damaged)) {
			if (Game.teamRed.contains(shooter)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPVP(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof Player) {

				Player da = (Player) e.getEntity();
				Player da2 = (Player) e.getDamager();

				String damaged = da.getName();
				String damager = da2.getName();

				if (PLUGIN.canPvp == false) {
					e.setCancelled(true);
					da2.sendMessage(ChatColor.RED + "PVP is not enabled yet.");

				}

				if (Game.teamBlue.contains(damaged)) {
					if (Game.teamBlue.contains(damager)) {
						e.setCancelled(true);

					}
				}

				if (Game.teamRed.contains(damaged)) {
					if (Game.teamRed.contains(damager)) {
						e.setCancelled(true);
					}
				}
			}
			if (e.getDamager() instanceof Arrow) {
				Arrow a = (Arrow) e.getDamager();
				if (a.getShooter() instanceof Player) {
					if (e.getEntity() instanceof Player) {
	
						Player p = (Player) e.getEntity();
						Player d = (Player) a.getShooter();
	
						String damaged = p.getName();
						String damager = d.getName();
	
						if (Game.teamBlue.contains(damaged)) {
							if (Game.teamBlue.contains(damager)) {
								e.setCancelled(true);
								return;
							}
						}
	
						if (Game.teamRed.contains(damaged)) {
							if (Game.teamRed.contains(damager)) {
								e.setCancelled(true);
								return;
							}
						}
						if (PLUGIN.canPvp == false) {
							e.setCancelled(true);
							d.sendMessage(ChatColor.RED + "PVP is not enabled yet.");
						}
					}
				}
			}
			if (!e.isCancelled()) {
				if (PLUGIN.canPvp == false) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Location to = e.getTo();
		
		Block wallCheck = Bukkit.getWorld(WORLD.map.getName()).getBlockAt(to.getBlockX(),0,to.getBlockZ());
		
		if ((PLUGIN.gameMode == 2 && wallCheck.getType().equals(Material.ENDER_CHEST)) || wallCheck.getTypeId() == 36) {
			
			e.getPlayer().setVelocity(new Vector(0, e.getPlayer().getVelocity().getY(), 0));
			e.getPlayer().teleport(e.getFrom());
			e.getPlayer().sendMessage(ChatColor.RED + "An invisible barrier stops you.");
		}
		if (borderPlayerList.contains(e.getPlayer().getName())) {
			Location current = e.getPlayer().getLocation();
			current.setY(0);
			Block b = e.getPlayer().getWorld().getBlockAt(current);
			b.setType(Material.SPONGE);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if (PLUGIN.gameMode != 1) {
			Player p = e.getPlayer();
			if (Game.teamBlue.contains(p.getName())) {
				e.setRespawnLocation(WORLD.blueSpawn);
			} else {
				e.setRespawnLocation(WORLD.redSpawn);
			}
			Util.giveInventory(p);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (PLUGIN.gameMode != 0) {
			Forfeit.timer.put(e.getPlayer().getName(), 30);
		} else {
			Timer.update();
		}
	}

	@EventHandler
	public void onFoodLoss(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		p.setFoodLevel(20);
		p.setSaturation(20F);
		e.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	//@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
		e.getPlayer().updateInventory();
	}

	//@EventHandler
	public void onPickUp(PlayerPickupItemEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(ServerCommandEvent e) {
		String cmd = e.getCommand();
		if (cmd.contains("save-on") || cmd.contains("save-all")) {
			e.getSender().sendMessage("Please use the in game command '/a save' instead.");
			e.setCommand("");
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		e.setMessage(ChatColor.GRAY+e.getMessage());
	}
	
}
