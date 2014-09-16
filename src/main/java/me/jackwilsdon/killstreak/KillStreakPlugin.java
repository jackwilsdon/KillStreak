package me.jackwilsdon.killstreak;

import org.bukkit.plugin.java.JavaPlugin;

public class KillStreakPlugin extends JavaPlugin {

	@Override	
	public void onEnable()
	{
		this.getLogger().info(this.getDescription().getFullName() + " enabled!");
		this.saveDefaultConfig();
		
		KillStreakManager manager = new KillStreakManager(this);
		KillStreakEventListener ev = new KillStreakEventListener(manager);
		this.getServer().getPluginManager().registerEvents(ev, this);
		
		KillStreakCommandExecutor executor = new KillStreakCommandExecutor(manager);
		this.getCommand("killstreak").setExecutor(executor);
	}

	@Override
	public void onDisable()
	{
		this.getLogger().info(this.getDescription().getFullName() + " disabled!");
	}
}
