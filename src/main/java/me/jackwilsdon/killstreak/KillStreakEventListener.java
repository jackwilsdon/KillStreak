package me.jackwilsdon.killstreak;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

/**
 * Manages events for KillStreak
 * @author Jack Wilsdon
 */
public class KillStreakEventListener implements Listener {
	private KillStreakManager manager = null;
	
	/**
	 * Pass an instance of KillStreakManager to use
	 * @param manager An instance of KillStreakManager
	 */
	public KillStreakEventListener(KillStreakManager manager)
	{
		this.manager = manager;
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent event)
	{
		Player victim = event.getEntity();
		Player killer = event.getEntity().getKiller();
		
		String message = this.manager.getChatManager().getDeathMessage(victim.getName());
		victim.sendMessage(message);
		this.manager.resetPlayer(victim.getName());

		if (killer instanceof Player)
		{
			this.manager.addKill(killer.getName());
			this.manager.apply(killer.getName());
			this.manager.broadcast(killer.getName());
		}
	}
	
	@EventHandler
	public void entityDeath(EntityDeathEvent event)
	{
		if (!this.manager.countMobs())
		{
			return;
		}
		
		List<String> mobs = this.manager.getMobs();
		Player killer = event.getEntity().getKiller();
		
		if (killer == null)
		{
			return;
		}
		
		if (mobs.contains(event.getEntityType().name()))
		{
			this.manager.addKill(killer.getName());
			this.manager.apply(killer.getName());
			this.manager.broadcast(killer.getName());
		}
	}
	
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if (this.manager.resetOnDisconnect())
		{
			this.manager.resetPlayer(player.getName());;
		}
	}

}
