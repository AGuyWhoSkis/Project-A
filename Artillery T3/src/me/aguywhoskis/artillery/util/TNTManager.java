package me.aguywhoskis.artillery.util;

import java.util.ArrayList;
import java.util.HashMap;

import me.aguywhoskis.artillery.Artillery;
import me.aguywhoskis.artillery.thread.AnnounceMessage;
import me.aguywhoskis.artillery.thread.RemoveMetaData;
import me.aguywhoskis.artillery.thread.SetWinner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class TNTManager implements Listener {

	public static HashMap<Integer, Object[]> tracker = new HashMap<Integer, Object[]>();
	public static HashMap<Location, String> registeredLaunchers = new HashMap<Location, String>();

	// Code for type (second integer in HashMap): Type,Data
	/**
	 * Coal: 1 Iron: 2 Gold: 3 Emerald: 4 Diamond: 5
	 * 
	 * Stage: 1-2
	 * 
	 * Ex: 42 is emerald in stage 2 (won't spawn more)
	 **/

	static Plugin myplugin = Artillery.plugin;

	public static void spawnTNT(String owner, Location loc, Location source, Vector v, int typeData, int ticks) {
		TNTPrimed tnt = Bukkit.getServer().getWorld(loc.getWorld().getName())
				.spawn(loc, TNTPrimed.class);
		tnt.setVelocity(v);
		((TNTPrimed) tnt).setFuseTicks(ticks);
		tnt.setMetadata("sourceworld", new FixedMetadataValue(myplugin, source.getWorld().getName()));
		tnt.setMetadata("sourcex",new FixedMetadataValue(myplugin, source.getBlockX()));
		tnt.setMetadata("sourcey",new FixedMetadataValue(myplugin, source.getBlockY()));
		tnt.setMetadata("sourcez",new FixedMetadataValue(myplugin, source.getBlockZ()));

		tnt.setMetadata("shotBy", new FixedMetadataValue(myplugin, owner));
		Location iden = source;
		iden.setPitch(0F);
		iden.setYaw(0F);
		tnt.setMetadata("turretOwner", new FixedMetadataValue(myplugin,
				registeredLaunchers.get(iden)));
		tnt.setMetadata("typeData", new FixedMetadataValue(myplugin, typeData));
	}

	Material[] id = { Material.BEACON };
	Material[] id2 = { Material.WOOL };

	String prefix = "&0[&2!&0]&r ";

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosion(EntityExplodeEvent e) {

		if (e.getEntity() != null) {
			Entity tnt = e.getEntity();
			if (tnt.getType().equals(EntityType.PRIMED_TNT)) {
				if (tnt.hasMetadata("shotBy")) {
					String p = tnt.getMetadata("shotBy").get(0).asString();
					int typeData = tnt.getMetadata("typeData").get(0).asInt();
					Location source = new Location(Bukkit.getWorld(tnt.getMetadata("sourceworld").get(0).asString()), tnt.getMetadata("sourcex").get(0).asInt(), tnt.getMetadata("sourcey").get(0).asInt(), tnt.getMetadata("sourcez").get(0).asInt());
					String turretOwner = TNTManager.registeredLaunchers.get(source);
					// Label A1
					for (Entity en : tnt.getNearbyEntities(8, 8, 8)) {
						if (en instanceof Player) {
							Player vic = (Player) en;

							if (vic.getLocation().distance(tnt.getLocation()) < 8) {
								if (vic.getLastDamageCause().getCause() != null) {
									if (vic.getLastDamageCause().getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
										vic.setMetadata("hitTypeData",new FixedMetadataValue(myplugin,typeData));
										vic.setMetadata("hitShooter",new FixedMetadataValue(myplugin, p));
										vic.setMetadata("hitTurretOwner",new FixedMetadataValue(myplugin,turretOwner));
										Bukkit.getScheduler().scheduleSyncDelayedTask(myplugin,new RemoveMetaData(myplugin, p), 4L);
									}
								}
							}
						}
					}

					Location loc = e.getLocation();

					String[] split = Integer.toString(typeData).split("");
					int data = Integer.parseInt(split[2]);
					int type = Integer.parseInt(split[1]);

					if (e.blockList() != null) {
						ArrayList<Block> toRemove = new ArrayList<Block>();
						for (Block bl : e.blockList()) {
							if (bl.getType() == Material.BEACON) {

								Player shooter = Bukkit.getServer().getPlayer(p);
								Player turretOwn = Bukkit.getServer().getPlayer(turretOwner);

								
								if (WORLD.blueCore.contains(bl.getLocation())) {
									if (Game.teamRed.contains(shooter.getName())) {
										//blue core destroyed by red
										if (WORLD.blueCore.size() == 1) {
											if (!Game.winnerIsLocked) {
												BukkitScheduler s = Bukkit.getScheduler();

												if (turretOwn != shooter) {
													s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()
															+ " &7Destroyed the &1BLUE&7 core block using "+ turretOwn.getDisplayName()+ "'s turret!"),80L);
												} else {
													s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()+ " &7Destroyed the &4RED&7 core block!"),80L);
												}

												s.scheduleSyncDelayedTask(myplugin,new SetWinner(myplugin, "red"),100L);
												for (Player player : Bukkit.getOnlinePlayers()) {
													player.playSound(player.getLocation(),Sound.ENDERDRAGON_DEATH,1F, 1F);
												}
												WORLD.blueCore.remove(bl.getLocation());
											}
										} else {
											BukkitScheduler s = Bukkit.getScheduler();

											if (turretOwn != shooter) {
												s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()
														+ " &7Destroyed a &1BLUE&7 core block using "+ turretOwn.getDisplayName()+ "'s turret!"),20L);
											} else {
												s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()+ " &7Destroyed a &1BLUE&7 core block!"),20L);
											}

											for (Player player : Bukkit.getOnlinePlayers()) {
												player.playSound(player.getLocation(),Sound.ENDERDRAGON_GROWL,1F, 1F);
											}
											WORLD.blueCore.remove(bl.getLocation());
										}
									} else if (Game.teamBlue.contains(shooter.getName())) {
										// cancel
										toRemove.add(bl);
										shooter.sendMessage(ChatColor.RED+ "Don't destroy your own core!");
									}
								} else if (WORLD.redCore.contains(bl.getLocation())) {
									
									if (Game.teamBlue.contains(shooter.getName())) {
										if (WORLD.blueCore.size() == 1) {
											if (!Game.winnerIsLocked) {
												BukkitScheduler s = Bukkit.getScheduler();

												if (turretOwn != shooter) {
													s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()
															+ " &7Destroyed the &4RED&7 core block using "+ turretOwn.getDisplayName()+ "'s turret!"),80L);
												} else {
													s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()+ " &7Destroyed the &4RED&7 core block!"),80L);
												}

												s.scheduleSyncDelayedTask(myplugin,new SetWinner(myplugin, "blue"),100L);
												for (Player player : Bukkit.getOnlinePlayers()) {
													player.playSound(player.getLocation(),Sound.ENDERDRAGON_DEATH,1F, 1F);
												}
												WORLD.redCore.remove(bl.getLocation());
											}
										} else {
											BukkitScheduler s = Bukkit.getScheduler();

											if (turretOwn != shooter) {
												s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()
														+ " &7Destroyed a &4RED&7 core block using "+ turretOwn.getDisplayName()+ "'s turret!"),20L);
											} else {
												s.scheduleSyncDelayedTask(myplugin,new AnnounceMessage(myplugin,shooter.getDisplayName()+ " &7Destroyed a &4RED&7 core block!"),20L);
											}
											for (Player player : Bukkit.getOnlinePlayers()) {
												player.playSound(player.getLocation(),Sound.ENDERDRAGON_GROWL,1F, 1F);
											}
											WORLD.redCore.remove(bl.getLocation());
										}

									} else if (Game.teamRed.contains(shooter.getName())) {
										// cancel
										toRemove.add(bl);
										shooter.sendMessage(ChatColor.RED+ "Don't destroy your own core!");
									}
								}
							} else if (bl.getType().equals(Material.COAL_BLOCK)|| bl.getType().equals(Material.IRON_BLOCK)
									|| bl.getType().equals(Material.GOLD_BLOCK)|| bl.getType().equals(Material.EMERALD_BLOCK)
									|| bl.getType().equals(Material.DIAMOND_BLOCK)) {
								if (TNTManager.registeredLaunchers.containsKey(bl.getLocation())) {
									// turret was destroyed
									String shooterTeam = "none";
									String owner = registeredLaunchers.get(bl.getLocation());
									String ownerTeam = "none";

									if (Game.teamBlue.contains(p)) {
										shooterTeam = "blue";
									} else if (Game.teamRed.contains(p)) {
										shooterTeam = "red";
									}
									if (Game.teamBlue.contains(owner)) {
										ownerTeam = "blue";
									} else if (Game.teamRed.contains(owner)) {
										ownerTeam = "red";
									}
									
									Player shooter = Bukkit.getPlayer(p);
									Player own = Bukkit.getPlayer(owner);
									if (ownerTeam == shooterTeam) {
										if (owner != p) {
											toRemove.add(bl);
											Bukkit.getPlayer(p).sendMessage(ChatColor.RED+ "Don't break your own team's turret!");
										} else {
											Util.messageServer(prefix+ shooter.getDisplayName()+ " blew up their own turret!");
										}

									} else {
										Game.addCoins(Bukkit.getPlayer(p), 30,"Turret Kill");
										Util.messageServer(prefix +shooter.getDisplayName()+ " blew up the turret of "+own.getDisplayName()+"!");
									}
								}
								// }
							} else {
								if (bl.getLocation().distance(WORLD.blueSpawn) < 20 || bl.getLocation().distance(WORLD.redSpawn) < 20) {
									toRemove.add(bl);
								}
							}
							
						}
						
						for (Block b: toRemove) {
							e.blockList().remove(b);
						}
						
						if (data == 1) { // first explosion
							if (type == 3) {
								// gold
								Vector vec1 = new Vector(0.15, 1, 0);
								Vector vec2 = new Vector(-0.15, 1, 0);

								spawnTNT(p, loc, source, vec1, 32, 80);
								spawnTNT(p, loc, source, vec2, 32, 80);

							} else if (type == 4) {
								// emerald

								Vector vec1 = new Vector(-0.2, 1, 0.1);
								Vector vec2 = new Vector(0, 1, -0.2);
								Vector vec3 = new Vector(0.2, 1, 0.1);

								spawnTNT(p, loc, source, vec1, 42, 80);
								spawnTNT(p, loc, source, vec2, 42, 80);
								spawnTNT(p, loc, source, vec3, 42, 80);

							} else if (type == 5) {
								// diamond
								Vector vec1 = new Vector(0.055, 1, 0);
								Vector vec2 = new Vector(0.04, 1, 0.02);
								Vector vec3 = new Vector(0.03, 1, 0.03);
								Vector vec4 = new Vector(0.02, 1, 0.04);
								Vector vec5 = new Vector(0, 01, 0.055);
								Vector vec6 = new Vector(-0.02, 1, 0.04);
								Vector vec7 = new Vector(-0.03, 1, 0.03);
								Vector vec8 = new Vector(-0.04, 1, 0.02);
								Vector vec9 = new Vector(-0.055, 1, 0);
								Vector vec10 = new Vector(-0.04, 1, -0.02);
								Vector vec11 = new Vector(-0.03, 1, -0.03);
								Vector vec12 = new Vector(-0.02, 1, -0.04);
								Vector vec13 = new Vector(0, 1, -0.055);
								Vector vec14 = new Vector(0.02, 1, -0.04);
								Vector vec15 = new Vector(0.03, 1, -0.03);
								Vector vec16 = new Vector(0.04, 1, -0.02);

								spawnTNT(p, loc, source, vec1, 52, 80);
								spawnTNT(p, loc, source, vec2, 52, 80);
								spawnTNT(p, loc, source, vec3, 52, 80);
								spawnTNT(p, loc, source, vec4, 52, 80);
								spawnTNT(p, loc, source, vec5, 52, 80);
								spawnTNT(p, loc, source, vec6, 52, 80);
								spawnTNT(p, loc, source, vec7, 52, 80);
								spawnTNT(p, loc, source, vec8, 52, 80);
								spawnTNT(p, loc, source, vec9, 52, 80);
								spawnTNT(p, loc, source, vec10, 52, 80);
								spawnTNT(p, loc, source, vec11, 52, 80);
								spawnTNT(p, loc, source, vec12, 52, 80);
								spawnTNT(p, loc, source, vec13, 52, 80);
								spawnTNT(p, loc, source, vec14, 52, 80);
								spawnTNT(p, loc, source, vec15, 52, 80);
								spawnTNT(p, loc, source, vec16, 52, 80);
							}
						}
					}
				}
			}
		}
	}

	public static double GRAVITY = 0.115;

	public static Vector getTntVelocity(Location fromLoc, Location toLoc,
			int heightGain) {
		Vector from = fromLoc.toVector();
		Vector to = toLoc.toVector();

		// Block locations
		int endGain = to.getBlockY() - from.getBlockY();
		double horizDist = fromLoc.distance(toLoc);

		// Height gain
		int gain = heightGain;
		double maxGain = (gain > (endGain + gain) ? gain : (endGain + gain));

		// Solve quadratic equation for velocity
		double a = -horizDist * horizDist / (4 * maxGain);
		double b = horizDist;
		double c = -endGain;
		double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);

		// Vertical velocity
		double veloY = Math.sqrt(maxGain * GRAVITY);

		// Horizontal velocity
		double vH = veloY / slope;

		// Calculate horizontal direction
		int distX = to.getBlockX() - from.getBlockX();
		int distZ = to.getBlockZ() - from.getBlockZ();
		double mag = Math.sqrt(distX * distX + distZ * distZ);
		double dirX = distX / mag;
		double dirZ = distZ / mag;

		// Horizontal velocity components
		double veloX = vH * dirX;
		double veloZ = vH * dirZ;

		// Actual velocity
		Vector velocity = new Vector(veloX, veloY, veloZ);

		return velocity;
	}

}
