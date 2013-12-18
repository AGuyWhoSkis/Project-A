package me.aguywhoskis.artillery.event;


import java.util.Arrays;
import java.util.List;

import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.ScoreBoard;
import me.aguywhoskis.artillery.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopHandle implements Listener {
	 
    private static Inventory turretInv, obstacleInv;

    private static String prefix = (ChatColor.BLACK+"["+ChatColor.GOLD+"$"+ChatColor.BLACK+"] "+ChatColor.RESET);
    
    public static void initIs() {
    	//setting all names/lores of items in both shops
    	
    	turretInv = Bukkit.getServer().createInventory(null, 9, "Turrets");
    	obstacleInv = Bukkit.getServer().createInventory(null, 9, "Obstacles and tools");
		
    	setIm(4, obstacleInv, Util.book , null, null);
    	
    	ItemStack block = new ItemStack(Material.COBBLESTONE, 8);
    	setIm(8, obstacleInv, block, null, Arrays.asList(ChatColor.GOLD + "Cost: 15", ChatColor.AQUA + "For repairs, walls, etc."));
    	
    	ItemStack wall = new ItemStack(Material.COBBLE_WALL, 8);
    	setIm(7, obstacleInv, wall, null, Arrays.asList(ChatColor.GOLD + "Cost: 15",ChatColor.AQUA + "For quick/easy barriers."));
    	
    	ItemStack cobweb = new ItemStack(Material.WEB, 8);
    	setIm(5, obstacleInv, cobweb, null, Arrays.asList(ChatColor.GOLD + "Cost: 15", ChatColor.AQUA + "Slows down players."));
    	
    	ItemStack ladder = new ItemStack(Material.LADDER, 16);
    	setIm(6, obstacleInv, ladder, null, Arrays.asList(ChatColor.GOLD + "Cost: 10", ChatColor.AQUA + "Your go-to wall climbing tool."));
    	
    	ItemStack diaSword = new ItemStack(Material.DIAMOND_SWORD);
    	setIm(0, obstacleInv, diaSword, null, Arrays.asList(ChatColor.GOLD + "Cost: 50", ChatColor.AQUA + "An upgrade from the iron sword."));
    	
    	ItemStack pick = new ItemStack(Material.STONE_PICKAXE);
    	setIm(1, obstacleInv, pick, null, Arrays.asList(ChatColor.GOLD + "Cost: 15", ChatColor.AQUA + "For quick dissassembling of walls."));
    	
    	ItemStack axe = new ItemStack(Material.STONE_AXE);
    	setIm(3, obstacleInv, axe, null, Arrays.asList(ChatColor.GOLD + "Cost: 10", ChatColor.AQUA + "It's an axe. It cuts wood."));
    	
    	ItemStack shovel = new ItemStack(Material.STONE_SPADE);
    	setIm(2, obstacleInv, shovel, null, Arrays.asList(ChatColor.GOLD + "Cost: 5", ChatColor.AQUA + "For the gopher in us all."));
    	
    	ItemStack c = new ItemStack (Material.COAL_BLOCK);
    	setIm(0, turretInv, c, ChatColor.DARK_GRAY +"Coal Turret", Arrays.asList(ChatColor.GOLD + "Cost: 75", ChatColor.AQUA + "Fires a single shot.", ChatColor.YELLOW +"Cooldown: 5 seconds."));
    	
    	ItemStack i = new ItemStack (Material.IRON_BLOCK);
    	setIm(2, turretInv, i, ChatColor.GRAY + "Iron Turret", Arrays.asList(ChatColor.GOLD + "Cost: 150",ChatColor.AQUA + "Fires a double shot.",ChatColor.YELLOW +"Cooldown: 10 seconds."));
    	
    	ItemStack g = new ItemStack (Material.GOLD_BLOCK);
    	setIm(4, turretInv, g, ChatColor.GOLD+"Gold Turret", Arrays.asList(ChatColor.GOLD + "Cost: 200",ChatColor.AQUA + "Fires a "+ChatColor.ITALIC + "double "+ChatColor.RESET+""+ChatColor.AQUA + "double shot.",ChatColor.YELLOW +"Cooldown: 20 seconds."));
    	
    	ItemStack e = new ItemStack (Material.EMERALD_BLOCK);
    	setIm(6, turretInv, e, ChatColor.GREEN+"Emerald Turret", Arrays.asList(ChatColor.GOLD + "Cost: 250",ChatColor.AQUA + "Fires a "+ChatColor.ITALIC + "triple"+ChatColor.RESET+""+ ChatColor.AQUA+ " triple shot.",ChatColor.YELLOW +"Cooldown: 30 seconds."));
    	
    	ItemStack d = new ItemStack (Material.DIAMOND_BLOCK);
    	setIm(8, turretInv, d, ChatColor.AQUA+"Diamond Turret", Arrays.asList(ChatColor.GOLD + "Cost: 350",ChatColor.AQUA + "Obliterates a large area.",ChatColor.YELLOW +"Cooldown: 40 seconds"));
    }
    
    public static void setIm(int pos, Inventory inv, ItemStack is, String name, List<String> lores) {
    	ItemMeta im = is.getItemMeta();
    	if (name != null) {
    		im.setDisplayName(name);
    	}
    	if (lores != null) {
    		im.setLore(lores);
    	}
    	is.setItemMeta(im);
    	inv.setItem(pos, is);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
    	Player p = (Player) e.getWhoClicked();
    	Inventory topInv = p.getOpenInventory().getTopInventory();
    	Inventory botInv = p.getOpenInventory().getBottomInventory();
    	if (topInv.getName().contains("container") && (botInv.getName().contains("container"))) {
    		//player only has their inventory open
        	if (e.getSlot() > 35) {
        		e.setCancelled(true);
        		//stops players from taking off armour
        	}
    		return;
    	}
    	
    	e.setCancelled(true);
    	
        int cost = 0;
        int coins = Game.coins.get(e.getWhoClicked().getName());
        ItemStack is = null;
        ItemMeta im = null;
        String name = "none";
    	String item = "none";
    	
    	if (e.getCurrentItem().hasItemMeta()) {
    		if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
        		name = e.getCurrentItem().getItemMeta().getDisplayName();
        	}
    	}
        
        if (name.contains("Turrets")) {
        	e.getWhoClicked().openInventory(turretInv);
        	return;
        } else if (name.contains("Obstacles")) {
        	e.getWhoClicked().openInventory(obstacleInv);
        	return;
        }
        
        if (name.contains("Coal")) {
        	cost = 75;
        	is = new ItemStack(Material.COAL_BLOCK);
        	im = is.getItemMeta();
        	im.setDisplayName("Coal Turret");
        	is.setItemMeta(im);
        } else if (name.contains("Iron")) {
        	cost = 150;
        	is = new ItemStack(Material.IRON_BLOCK);
        	im = is.getItemMeta();
        	im.setDisplayName("Iron Turret");
        	is.setItemMeta(im);
        } else if (name.contains("Gold")) {
        	cost = 200;
        	is = new ItemStack(Material.GOLD_BLOCK);
        	im = is.getItemMeta();
        	im.setDisplayName("Gold Turret");
        	is.setItemMeta(im);
        } else if (name.contains("Emerald")) {
        	cost = 250;
        	is = new ItemStack(Material.EMERALD_BLOCK);
        	im = is.getItemMeta();
        	im.setDisplayName("Emerald Turret");
        	is.setItemMeta(im);
        } else if (name.contains("Diamond")) {
        	cost = 350;
        	is = new ItemStack(Material.DIAMOND_BLOCK);
        	im = is.getItemMeta();
        	im.setDisplayName("Diamond Turret");
        	is.setItemMeta(im);
        }
        
        if (name == "none") {
        	 Material type = e.getCurrentItem().getType();
        	 if (type == Material.WEB) {
        		 cost = 30;
        		 is = new ItemStack(Material.WEB, 8);
        		 item = "cobweb";
        	 } else if (type == Material.COBBLE_WALL) {
        		 cost = 30;
        		 is = new ItemStack(Material.COBBLE_WALL, 8);
        		 item = "Cobblestone wall";
        	 } else if (type == Material.COBBLESTONE) {
        		 cost = 25;
        		 is = new ItemStack(Material.COBBLESTONE, 8);
        		 item = "Cobblestone";
        	 } else if (type == Material.LADDER) {
        		 cost = 50;
        		 is = new ItemStack(Material.LADDER, 16);
        		 item = "Ladder";
        	 } else if (type == Material.DIAMOND_SWORD) {
        		 cost = 50;
        		 is = new ItemStack(Material.DIAMOND_SWORD);
        		 item = "Diamond Sword";
        	 } else if (type == Material.STONE_PICKAXE) {
        		 cost = 30;
        		 is = new ItemStack(Material.STONE_PICKAXE);
        		 item = "Stone Pickaxe";
        	 } else if (type == Material.STONE_SPADE) {
        		 cost = 20;
        		 is = new ItemStack(Material.STONE_SPADE);
        		 item = "Stone Spade";
        	 } else if (type == Material.STONE_AXE) {
        		 cost = 15;
        		 is = new ItemStack(Material.STONE_AXE);
        		 item = "Stone Axe";
        	 } else if (type == Material.WRITTEN_BOOK) {
        		 cost = 0;
        		 is = Util.book;
        		 item = "Tutorial Book";
        	 }
        }
        
        int difference = coins - cost;
        if (difference < 0) {
        	//Insufficient funds
        	difference = Math.abs(difference);
        	if (difference > 1) {
        		p.sendMessage(prefix + ChatColor.RED + "That is too expensive! "+ChatColor.GREEN + "You need "+difference + " more coins!");
        	} else {
        		p.sendMessage(prefix + ChatColor.RED + "That is too expensive! "+ChatColor.GREEN + "You need "+difference + " more coin!");
        	}
        } else {
        	//Funds are sufficient
        	if (is != null) {
	        	p.getInventory().addItem(is);
	        	p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
	        	Game.coins.put(p.getName(), Game.coins.get(p.getName()) - cost);
	        	if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
	        		p.sendMessage(prefix + ChatColor.GREEN + "Succesfully purchased "+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.GREEN + "!");	
	        	} else {
	        		p.sendMessage(prefix + ChatColor.GREEN + "Succesfully purchased "+item+ChatColor.GREEN + "!");
	        	}
	        	ScoreBoard.update(p);
        	}
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if (e.getPlayer().getItemInHand().hasItemMeta()) {
    			if (e.getPlayer().getItemInHand().getItemMeta().hasDisplayName()) {
	    			ItemMeta im = e.getPlayer().getItemInHand().getItemMeta();
	    			if (im.hasDisplayName()) {
	    				if (im.getDisplayName().contains("Turret Shop")) {
	    					e.getPlayer().openInventory(turretInv);
	    				} else if (im.getDisplayName().contains("Obstacle")) {
	    					e.getPlayer().openInventory(obstacleInv);
	    				}
	    			}
    			}
    		}
    	}
    }
}
