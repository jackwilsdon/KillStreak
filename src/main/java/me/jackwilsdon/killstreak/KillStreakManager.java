package me.jackwilsdon.killstreak;

import java.util.*;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

/**
 * Manages the killstreaks of players
 * @author Jack Wilsdon
 */
public class KillStreakManager {
	private KillStreakPlugin plugin = null;
	private KillStreakChatManager chatManager = null;
	
	/**
	 * Create the KillStreakManager with an instance of KillStreakPlugin
	 * @param plugin An instance of KillStreakPlugin
	 */
	public KillStreakManager(KillStreakPlugin plugin)
	{
		this.plugin = plugin;
		
		ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("KillStreak.messages");
		this.chatManager = new KillStreakChatManager(section, this);
	}
	
	/**
	 * Check whether a player is in the database
	 * @param username The player to check
	 * @return Whether the player is in the database
	 */
	public boolean playerExists(String username)
	{
		return (this.plugin.getConfig().get("PlayerStreaks."+username) != null);
	}
	
	/**
	 * Delete a player from the database
	 * @param username The player to remove
	 */
	public void deletePlayer(String username)
	{
		if (!playerExists(username))
		{
			return;
		}
		
		this.plugin.getConfig().set("PlayerStreaks."+username, null);
		this.plugin.saveConfig();
	}
	
	/**
	 * Reset a player's killstreak
	 * @param username The player to reset
	 */
	public void resetPlayer(String username)
	{
		if (!playerExists(username))
		{
			return;
		}
		
		this.plugin.getConfig().set("PlayerStreaks."+username, 0);
		this.plugin.saveConfig();
	}
	
	/**
	 * Find how many kills a player has
	 * @param username The player to check
	 * @return How many kills the player has
	 */
	public int getKills(String username)
	{
		if (!this.playerExists(username))
		{
			return 0;
		}
		
		return this.plugin.getConfig().getInt("PlayerStreaks."+username);
	}
	
	/**
	 * Set the number of kills the player has
	 * @param username The player to set the kills for
	 * @param kills The number to set the player's kills to
	 */
	public void setKills(String username, int kills)
	{
		this.plugin.getConfig().set("PlayerStreaks."+username, kills);
		this.plugin.saveConfig();
	}
	
	/**
	 * Add kills to a player
	 * @param username The player to add kills to
	 * @param kills The number of kills to add
	 */
	public void addKills(String username, int kills)
	{
		int newKills = this.getKills(username) + kills;
		this.setKills(username, newKills);
	}
	
	/**
	 * Add 1 kill to a player
	 * @param username The player to add 1 kill to
	 */
	public void addKill(String username)
	{
		this.addKills(username, 1);
	}
	
	/**
	 * Get a list of all players
	 * @return A list of all players
	 */
	public Map<String, Integer> getPlayers()
	{
		if (this.plugin.getConfig().getConfigurationSection("PlayerStreaks") == null)
		{
			return new HashMap<String, Integer>();
		}
		
		Map<String, Integer> players = new HashMap<String, Integer>();
		Set<String> pl = this.plugin.getConfig().getConfigurationSection("PlayerStreaks").getKeys(true);
		Iterator<String> it = pl.iterator();
		
		while (it.hasNext())
		{
			String name = it.next();
			int kills = this.plugin.getConfig().getInt("PlayerStreaks."+name);
			players.put(name, kills);
		}
		
		return players;
	}
	
	/**
	 * Get the potion for the specified killstreak
	 * @param kills The killstreak to get the potion for
	 * @return The potion effect from the configuration (null if no potion available)
	 */
	public PotionEffect getPotionEffect(int kills)
	{
		ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("KillStreak.streaks."+kills);
		if (section == null)
		{
			return null;
		}
		
		String type = section.getString("potion");
		int level = section.getInt("level");
		int seconds = section.getInt("seconds", -1);
		
		PotionType pt = PotionType.valueOf(type);
		if (pt == null)
		{
			return null;
		}
		
		Potion potion = new Potion(pt, level);
		PotionEffect effect = potion.getEffects().iterator().next();
		if (seconds > 0) 
		{
		    effect = new PotionEffect(effect.getType(), seconds * 20, effect.getAmplifier(), effect.isAmbient());
		    
		}
		return effect;
	}
	
	/**
	 * Get the potion for the specified user
	 * @param kills The user to get the potion for
	 * @return The potion effect from the configuration (null if no potion available)
	 */
	public PotionEffect getPotionEffect(String username)
	{
		int kills = this.getKills(username);
		return this.getPotionEffect(kills);
	}
	
	/**
	 * Get the list of mobs to add kills for
	 * @return The list of mobs to add kills for
	 */
	public List<String> getMobs()
	{
		List<?> list = this.plugin.getConfig().getList("KillStreak.count-mobs.mobs");
		List<String> output = new ArrayList<String>();
		
		Iterator<?> it = list.iterator();
		while (it.hasNext())
		{
			String i = (String) it.next();
			output.add(i);
		}
		return output;
	}
	
	/**
	 * Check whether mobs should be added to the killstreak
	 * @return Whether mobs should be added to the killstreak
	 */
	public boolean countMobs()
	{
		return this.plugin.getConfig().getBoolean("KillStreak.count-mobs.enabled");
	}
	
	/**
	 * Applies a potion to the player if one is available
	 * @param username The player to apply the potion to
	 */
	public void apply(String username)
	{
		Player player = this.plugin.getServer().getPlayer(username);
		PotionEffect potion = this.getPotionEffect(username);
		
		if (potion == null || player == null)
		{
			return;
		}

		player.removePotionEffect(potion.getType());
		player.addPotionEffect(potion);
	}
	
	/**
	 * Broadcast a message if a powerup is achieved
	 * @param username The user to check for a powerup
	 */
	public void broadcast(String username)
	{
		Player player = this.plugin.getServer().getPlayer(username);
		PotionEffect potion = this.getPotionEffect(username);
		
		if (this.chatManager.broadcastOnPowerup() && potion != null)
		{
			String message = this.chatManager.getBroadcastMessage(username);
			this.plugin.getServer().broadcastMessage(message);
		} else {
			String message = this.chatManager.getMessage(username);
			player.sendMessage(message);
		}
	}
	
	/**
	 * Get the KillStreakChatManager to use for formatting
	 * @return The KillStreakChatManager to use for formatting
	 */
	public KillStreakChatManager getChatManager()
	{
		return this.chatManager;
	}

	/**
	 * Reload the configuration
	 */
	public void reload()
	{
		this.plugin.reloadConfig();
	}
	
	/**
	 * Check whether to reset streak on disconnect
	 * @return Whether to reset streak on disconnect
	 */
	public boolean resetOnDisconnect()
	{
		return this.plugin.getConfig().getBoolean("KillStreak.reset-on-disconnect");
	}
}
