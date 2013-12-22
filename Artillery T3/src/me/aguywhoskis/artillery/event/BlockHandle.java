package me.aguywhoskis.artillery.event;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.aguywhoskis.artillery.Artillery;
import me.aguywhoskis.artillery.thread.AnnounceMessage;
import me.aguywhoskis.artillery.thread.SetWinner;
import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.PLUGIN;
import me.aguywhoskis.artillery.util.TNTManager;
import me.aguywhoskis.artillery.util.WORLD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;

public class BlockHandle implements Listener {
	public static ArrayList<Location> turretBlocks = new ArrayList<Location>();
	Material [] id = {Material.COAL_BLOCK,Material.IRON_BLOCK,Material.GOLD_BLOCK,Material.EMERALD_BLOCK,Material.DIAMOND_BLOCK,Material.BEDROCK,Material.BEACON};
	Plugin myplugin = Artillery.plugin;
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void ObjectPlace (BlockPlaceEvent e) {
		if (e.getItemInHand().hasItemMeta()) {
			String name = e.getItemInHand().getItemMeta().getDisplayName();
			if (name.contains("Shop") || name.contains("team")) {
				e.setCancelled(true);
				e.getPlayer().updateInventory();
				return;
			}
			if (PLUGIN.gameMode != 1) {
				Location loc = e.getBlock().getLocation();
				if (loc.distance(WORLD.blueSpawn) < 12 || loc.distance(WORLD.redSpawn) < 12) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED+"You can't do that here.");
				}
			}
		}
		if (PLUGIN.canBuild == true) {
			Material type = e.getBlock().getType();
			if (type == Material.COAL_BLOCK || type == Material.IRON_BLOCK || type == Material.GOLD_BLOCK || type == Material.EMERALD_BLOCK || type == Material.DIAMOND_BLOCK) {
				//correct block
				Location checkForOtherIdentifiers = checkForBlock(e.getBlock().getLocation(), id, 3);
				//check for turrets within a 3 block radius
				if (checkForOtherIdentifiers != null) {
					//no turrets within 3 blocks of e location
					if (checkForOtherIdentifiers.getBlock().getType().equals(Material.BEDROCK)
							|| checkForOtherIdentifiers.getBlock().getType().equals(Material.BEACON)) {
						//Cancel event to stop players from destroying bedrock/beacons instantly
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + "You can't place that there.");
						e.getPlayer().updateInventory();
						return;
					} else if (TNTManager.registeredLaunchers.containsKey(checkForOtherIdentifiers)) {
						//Cancel event to stop players from destroying other turrets instantly
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + "That is too close to another turret.");
						e.getPlayer().updateInventory();
						return;
					}
				}
				
				Location loc = e.getBlock().getLocation();
				
				File directory = new File(myplugin.getDataFolder(), "cannons");
				File f = null;
				
				Block b = e.getBlock();
				Vector v = new Vector (b.getX(), b.getY(), b.getZ());
				
				//Load appropriate schematic file based on block placed:
				if (b.getType() == Material.DIAMOND_BLOCK) {
					f = new File(directory, "diamond.schematic");
				} else if (b.getType() == Material.EMERALD_BLOCK) {
					f = new File(directory, "emerald.schematic");
				} else if (b.getType() == Material.GOLD_BLOCK) {
					f = new File(directory, "gold.schematic");
				} else if (b.getType() == Material.IRON_BLOCK) {
					f = new File(directory, "iron.schematic");
				} else if (b.getType() == Material.COAL_BLOCK) {
					f = new File(directory, "coal.schematic");
				}
				
				try {
					World w = e.getPlayer().getLocation().getWorld();
					BlockHandle.loadSchematic(w, f, v, e.getPlayer().getName());
					
					TNTManager.registeredLaunchers.put(loc, e.getPlayer().getName());
					//Load schematic into world
				} catch (DataException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}	
		}
	}
	
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		
		Location loc = e.getBlock().getLocation();
		if (PLUGIN.turretDelay.contains(loc)) {
			String owner = TNTManager.registeredLaunchers.get(loc);
			String breaker = e.getPlayer().getName();
			
			if (owner.equals(breaker)) return;
			
				if (Game.teamBlue.contains(breaker)) {
					if (Game.teamBlue.contains(owner)) {
						e.setCancelled(true);
						Bukkit.getPlayer(breaker).sendMessage(ChatColor.RED + "Don't break your own team's turret!");
					}
				} else if (Game.teamRed.contains(breaker)) {
					if (Game.teamRed.contains(owner)) {
						e.setCancelled(true);
						Bukkit.getPlayer(breaker).sendMessage(ChatColor.RED + "Don't break your own team's turret!");
					}
				}
			PLUGIN.turretDelay.remove(loc);
			return;
		}
		if (loc.getWorld().equals(WORLD.map)) {
			if (loc.distance(WORLD.blueSpawn) < 20 || loc.distance(WORLD.redSpawn) < 20) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED+"You can't do that here.");
			}	
		}
		if (e.getPlayer().getWorld().equals(WORLD.main) && !e.getPlayer().isOp()) {
			e.setCancelled(true);
		}
		Block b = e.getBlock();
		if (b.getType().equals(Material.BEACON)) {
			if (PLUGIN.gameMode == 3) {
				Location idenLoc = b.getLocation();
				Player p = e.getPlayer();
				
				if (WORLD.blueCore.contains(idenLoc)) {
					if (Game.teamRed.contains(p.getName())) {
						if (WORLD.blueCore.size() == 1) {
							if (!Game.winnerIsLocked) {
								//Final blue beacon was destroyed
								BukkitScheduler s = Bukkit.getScheduler();
								s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin,p.getDisplayName()+" &7Destroyed the last &1BLUE&7 core block!"), 80L);
								s.scheduleSyncDelayedTask(myplugin,  new SetWinner(myplugin, "red"), 100L);
								for (Player player:Bukkit.getOnlinePlayers()) {
									player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 1F);
								}
							}
						} else {
							Bukkit.getScheduler().scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin,p.getDisplayName()+" &7Destroyed a &1BLUE&7 core block! ("+(WORLD.blueCore.size()-1)+" left)"), 20L);
							for (Player player:Bukkit.getOnlinePlayers()) {
								player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
							}
						}
						WORLD.blueCore.remove(idenLoc);
					} else {
						e.setCancelled(true);
						p.sendMessage(ChatColor.RED + "Don't destroy your own core!");
						return;
					}
				} else if (WORLD.redCore.contains(idenLoc)) {
					if (Game.teamBlue.contains(p.getName())) {
						if (WORLD.redCore.size() == 1) {
							if (!Game.winnerIsLocked) {
								//Final red beacon was destroyed
								BukkitScheduler s = Bukkit.getScheduler();
								s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin,p.getDisplayName()+" &7Destroyed the &4RED&7 core block!"), 80L);
								s.scheduleSyncDelayedTask(myplugin,  new SetWinner(myplugin, "blue"), 100L);
								for (Player player:Bukkit.getOnlinePlayers()) {
									player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 1F);
								}
							}
						} else {
							Bukkit.getScheduler().scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin,p.getDisplayName()+" &7Destroyed a &4RED&7 core block! ("+(WORLD.redCore.size()-1)+" left)"), 20L);
							for (Player player:Bukkit.getOnlinePlayers()) {
								player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
							}
						}
						WORLD.redCore.remove(idenLoc);
					} else {
						e.setCancelled(true);
						p.sendMessage(ChatColor.RED + "Don't destroy your own core!");
						return;
					}
				}
			} else {
				e.setCancelled(true);
			}
		}
	}
	
	public static Location checkForBlock(Location loc, Material[] id, int r) {
	
		int X = loc.getBlockX();
		int Y = loc.getBlockY();
		int Z = loc.getBlockZ();
		
		int x = (int) (X -r);
		int y = (int) (Y -r);
		int z = (int) (Z -r);
		
		int bx = x;
		int bz = z;
		
		World w = Bukkit.getWorld(loc.getWorld().getName());
		for (int i=0; i<r*2+1; i++) {
			for (int j=0; j<r*2+1; j++) {
				for (int k=0; k<r*2+1; k++) {
					for (Material m : id) {
						if (w.getBlockAt(x, y, z).getType() == m) {
							if (x != X || y != Y || z != Z) {
								return new Location(loc.getWorld(), x,y,z);
							}
						}	
					}
					x++;
				}
				z++;
				x = bx;
			}
			z = bz;
			x = bx;
			y++;
		}
		return null;
	}
	
	public static ArrayList<Location> getBlocksNear(Location loc, Material[] id, int r) {
		
		ArrayList<Location> locs = new ArrayList<Location>();
		
		int X = loc.getBlockX();
		int Y = loc.getBlockY();
		int Z = loc.getBlockZ();
		
		int x = (int) (X -r);
		int y = (int) (Y -r);
		int z = (int) (Z -r);
		
		int bx = x;
		int bz = z;
		
		World w = Bukkit.getWorld(loc.getWorld().getName());
		for (int i=0; i<r*2+1; i++) {
			for (int j=0; j<r*2+1; j++) {
				for (int k=0; k<r*2+1; k++) {
					for (Material m : id) {
						if (w.getBlockAt(x, y, z).getType() == m) {
							if (x != X || y != Y || z != Z) {
								locs.add(new Location(loc.getWorld(), x,y,z));
							}
						}	
					}
					x++;
				}
				z++;
				x = bx;
			}
			z = bz;
			x = bx;
			y++;
		}
		return locs;
	}
	
	
	@SuppressWarnings("deprecation")
	public static void loadSchematic(World world, File file, Vector origin, String owner) throws DataException, IOException {
		
		EditSession es = new EditSession(new BukkitWorld(world), 25);
		CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
		cc.setOrigin(origin);
		try {
			cc.paste(es, origin, true);	
			ArrayList<Location> ccBlocks = getBlocksNear(new Location(world, origin.getX(), origin.getY(), origin.getZ()), new Material[]{Material.CARPET, Material.WOOL}, 1);
			if (Game.teamBlue.contains(owner)) {
				for (Location l:ccBlocks) {
					//11
					Block b = world.getBlockAt(l);
					b.setData((byte) 11);
				}
			} else if (Game.teamRed.contains(owner)) {
				for (Location l:ccBlocks) {
					//14
					Block b = world.getBlockAt(l);
					b.setData((byte) 14);
				}
			}

			
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}
	
	//TODO: Implement way of adding all turret blocks to array, cancel any turret "teamkilling"
	public static void addCC(CuboidClipboard cc) {
		
	}
}

