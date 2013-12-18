package me.aguywhoskis.artillery.thread;

import me.aguywhoskis.artillery.util.Game;

import org.bukkit.scheduler.BukkitRunnable;


public class ChangeMode extends BukkitRunnable {

    public void run() {
        Game.changeMode();
    }
}
