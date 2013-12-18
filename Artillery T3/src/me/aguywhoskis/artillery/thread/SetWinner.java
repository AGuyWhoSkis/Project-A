package me.aguywhoskis.artillery.thread;

import me.aguywhoskis.artillery.util.Game;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;
 
public class SetWinner extends BukkitRunnable {
	private String winner;

	public SetWinner(Plugin myplugin, String winner) {
        this.winner = winner;
    }
 
    public void run() {
    	Game.setWinner(this.winner);
    }
}
