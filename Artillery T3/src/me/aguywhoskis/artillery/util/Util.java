package me.aguywhoskis.artillery.util;

import me.aguywhoskis.artillery.Artillery;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

public class Util {
	
	public static void messageServer(String msg) {
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		Bukkit.broadcastMessage(msg);
	}
	
	static ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
	
	static ItemStack bow = new ItemStack(Material.BOW);
	static ItemStack arrows = new ItemStack(Material.ARROW, 64);
	
	static ItemStack redHelmet = new ItemStack(Material.LEATHER_HELMET);
	static ItemStack redChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
	static ItemStack redLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
	static ItemStack redBoots = new ItemStack(Material.LEATHER_BOOTS);
	
	static ItemStack blueHelmet = new ItemStack(Material.LEATHER_HELMET);
	static ItemStack blueChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
	static ItemStack blueLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
	static ItemStack blueBoots = new ItemStack(Material.LEATHER_BOOTS);
	
	static ItemStack turretShop, obstacleShop;
	
	static ItemStack teamRed, teamBlue, teamNone;
	
	public static ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
	
	Plugin myplugin = Artillery.plugin;
	
	public static void initIs() {
		
		LeatherArmorMeta lam = (LeatherArmorMeta)blueHelmet.getItemMeta();
		lam.setColor(Color.fromRGB(0, 0, 170));
		blueHelmet.setItemMeta(lam);
		
		lam = (LeatherArmorMeta)blueChestplate.getItemMeta();
		lam.setColor(Color.fromRGB(0, 0, 170));
		blueChestplate.setItemMeta(lam);
		
		lam = (LeatherArmorMeta)blueBoots.getItemMeta();
		lam.setColor(Color.fromRGB(0, 0, 170));
		blueBoots.setItemMeta(lam);
		
		lam = (LeatherArmorMeta)blueLeggings.getItemMeta();
		lam.setColor(Color.fromRGB(0, 0, 170));
		blueLeggings.setItemMeta(lam);
		
		lam = (LeatherArmorMeta)redHelmet.getItemMeta();
		lam.setColor(Color.fromRGB(170, 0, 0));
		redHelmet.setItemMeta(lam);
		
		lam = (LeatherArmorMeta)redChestplate.getItemMeta();
		lam.setColor(Color.fromRGB(170, 0, 0));
		redChestplate.setItemMeta(lam);
		
		lam = (LeatherArmorMeta)redBoots.getItemMeta();
		lam.setColor(Color.fromRGB(170, 0, 0));
		redBoots.setItemMeta(lam);
		
		lam = (LeatherArmorMeta)redLeggings.getItemMeta();
		lam.setColor(Color.fromRGB(170, 0, 0));
		redLeggings.setItemMeta(lam);
		
		BookMeta bm = (BookMeta) book.getItemMeta();
		bm.setTitle("Help/Rules");
		bm.setAuthor("AGuyWhoSkis");
		bm.addPage(ChatColor.RED + ""+ ChatColor.BOLD + "Welcome to Artillery! "+ChatColor.RESET + ""+ChatColor.BLACK + "This book will teach you the basics of" +
				" gameplay."+"\n"+ChatColor.GOLD + ChatColor.BOLD +"Basic Gameplay"+"\n"+ChatColor.RESET+""+ChatColor.BLACK+"The objective of the" +
						" game is to destroy all enemy core blocks (beacons). At the beginning of the game, you will be given" +
						" 1 minute to set up");
		bm.addPage("defences. Defences can be purchased via the obstacle shop in your hotbar. They will cost" +
						" exp, which you can obtain by getting kills/kill assists, destroying the core block, and winning the game." +
						" Cannons can also be purchased with the seperate cannon shop.");
		bm.addPage(ChatColor.RED +""+ChatColor.BOLD+"Cannons"+"\n"+ChatColor.RESET + "" + ChatColor.BLACK + "Cannons are a major part of the game." +
				" They shoot tnt in various ways depending on the type of cannon. To use them, stand on the middle block and sneak, looking at the block" +
				" that you want to destroy. To buy a cannon, use the cannon shop.");
		bm.addPage(ChatColor.RED +""+ChatColor.BOLD+"Obstacles"+"\n"+ChatColor.RESET + "" + ChatColor.BLACK + "Obstacles can be used" +
				" to slow the enemy down. Different structures can be set up");
		book.setItemMeta(bm);
		
		
		teamNone = new ItemStack(Material.WOOL, 1);
		ItemMeta im = teamNone.getItemMeta();
		im.setDisplayName(ChatColor.RESET+"You are not on a team.");
		teamNone.setItemMeta(im);
		
		teamBlue = new ItemStack(Material.WOOL, 1, (short) 11);
		im = teamBlue.getItemMeta();
		im.setDisplayName(ChatColor.RESET+"You are on the "+ChatColor.BLUE+"BLUE"+ChatColor.WHITE+" team!");
		teamBlue.setItemMeta(im);
		
		teamRed = new ItemStack(Material.WOOL, 1, (short) 14);
		im = teamRed.getItemMeta();
		im.setDisplayName(ChatColor.RESET+"You are on the "+ChatColor.RED+"RED"+ChatColor.WHITE+" team!");
		teamRed.setItemMeta(im);
		
		turretShop = new ItemStack(Material.DISPENSER);
		im = turretShop.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Turret Shop (Right Click)");
		turretShop.setItemMeta(im);
		
		obstacleShop = new ItemStack(Material.BRICK);
		im = turretShop.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Obstacles/Tools Shop (Right Click)");
		obstacleShop.setItemMeta(im);
		
	}
	
	public static void giveInventory(Player p) {
		Inventory inv = p.getInventory();
		
		inv.setItem(0, ironSword);
		inv.setItem(1, bow);
		inv.setItem(2, arrows);
		inv.setItem(7, obstacleShop);
		inv.setItem(6, turretShop);
		
		if (Game.teamBlue.contains(p.getName())) {
			inv.setItem(8, teamBlue);
			inv.setItem(36, blueBoots);
			inv.setItem(37, blueLeggings);
			inv.setItem(38, blueChestplate);
		} else if (Game.teamRed.contains(p.getName())) {
			inv.setItem(8, teamRed);
			inv.setItem(36, redBoots);
			inv.setItem(37, redLeggings);
			inv.setItem(38, redChestplate);
		} else {
			inv.setItem(8, teamNone);
		}
	}
	
	public static void logInfo(String message) {
		Bukkit.getLogger().info("[Artillery] "+message);
	}
	public static void logSevere(String message) {
		Bukkit.getLogger().severe("[Artillery] "+message);
	}
	public static Location roundLoc(Location loc) {
		Location temp = loc;
		temp.setX(loc.getBlockX());
		temp.setY(loc.getBlockY());
		temp.setZ(loc.getBlockZ());
		temp.setPitch(0F);
		temp.setYaw(0F);
		return temp;
	}
	
	public static com.sk89q.worldedit.BlockVector convertToSk89qBV(Location location) {
		return new com.sk89q.worldedit.BlockVector(location.getX(),location.getY(),location.getZ());
	}
}
