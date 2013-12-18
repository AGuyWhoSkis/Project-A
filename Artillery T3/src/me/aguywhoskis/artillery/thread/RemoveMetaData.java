 package me.aguywhoskis.artillery.thread;


import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
 
public class RemoveMetaData extends BukkitRunnable {
	private String p;
	private Plugin myplugin;

	public RemoveMetaData(Plugin myplugin, String player) {
        this.p = player;
        this.myplugin = myplugin;
    }
 
    public void run() {
        try {
        	Bukkit.getServer().getPlayer(p).setMetadata("hitTurretOwner", new FixedMetadataValue(myplugin, null));
        	Bukkit.getServer().getPlayer(p).setMetadata("hitShooter", new FixedMetadataValue(myplugin, null));
        	Bukkit.getServer().getPlayer(p).setMetadata("hitTypeData", new FixedMetadataValue(myplugin, null));
        } catch(NullPointerException e) {}
    }
}
