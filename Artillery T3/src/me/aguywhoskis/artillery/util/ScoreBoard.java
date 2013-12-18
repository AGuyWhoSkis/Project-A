package me.aguywhoskis.artillery.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreBoard {
	
	

	
	@SuppressWarnings("unused")
	public static void create(Player p) {
	    Scoreboard sb = (Scoreboard) Bukkit.getScoreboardManager().getNewScoreboard();
	    Objective objective = sb.registerNewObjective("player-stats", "dummy");
	    objective.setDisplayName("Stats");
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    Score kills = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN +"Kills: "));
	    Score deaths = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Deaths: "));
	    Score exp = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Exp: "));
	    Score coins = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Coins: "));
	    Score assists = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Assists: "));
	    p.setScoreboard(sb);
		ScoreBoard.update(p);
	}
	
	public static void delete(Player p) {
		p.setScoreboard(null);
		
	}
	
	public static void updateAll() {
        for (Player p :Bukkit.getOnlinePlayers()) {
        	update(p);
        }
	}
	public static void update(Player p) {
		Score kills = p.getScoreboard().getObjective("player-stats").getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN +"Kills: "));
		Score deaths = p.getScoreboard().getObjective("player-stats").getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN +"Deaths: "));
		Score exp = p.getScoreboard().getObjective("player-stats").getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN +"Exp: "));
		Score coins = p.getScoreboard().getObjective("player-stats").getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN +"Coins: "));
		Score assists = p.getScoreboard().getObjective("player-stats").getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN +"Assists: "));
		assists.setScore(Game.assists.get(p.getName()));
		kills.setScore(Game.kills.get(p.getName()));
		deaths.setScore(Game.deaths.get(p.getName()));
		exp.setScore(Game.exp.get(p.getName()));
		coins.setScore(Game.coins.get(p.getName()));
	}
}
