package me.jackwilsdon.killstreak;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

public class KillStreakChatManager {
	private ConfigurationSection config = null;
	private KillStreakManager manager = null;

	public KillStreakChatManager(ConfigurationSection config, KillStreakManager manager)
	{
		this.config = config;
		this.manager = manager;
	}

	public boolean broadcastOnPowerup()
	{
		return this.config.getBoolean("broadcast-on-powerup");
	}

	public String getPrefix()
	{
		return this.config.getString("message-tag");
	}

	private ChatColor getKillStreakColor()
	{
		String colorString = this.config.getString("killstreak-color");
		String colorCode = colorString.substring(1);
		return ChatColor.getByChar(colorCode);
	}

	private ChatColor getUsernameColor()
	{
		String colorString = this.config.getString("username-color");
		String colorCode = colorString.substring(1);
		return ChatColor.getByChar(colorCode);
	}

	public String parseColors(String message)
	{
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public String getBroadcastMessage(String username)
	{
		int streak = this.manager.getKills(username);
		PotionEffect effect = this.manager.getPotionEffect(streak);

		if (effect == null)
		{
			return null;
		}

		String powerup = effect.getType().getName();

		StringBuilder base = new StringBuilder();
		base.append(this.getPrefix());
		base.append(this.getUsernameColor()+username+"&f");
		base.append(" has a killstreak of ");
		base.append(this.getKillStreakColor()+Integer.toString(streak)+"&f");
		base.append(" and has been rewarded the powerup ");
		base.append("&e"+powerup+"&f!");

		return this.parseColors(base.toString());
	}

	public String getMessage(String username)
	{
		int streak = this.manager.getKills(username);
		PotionEffect potion = this.manager.getPotionEffect(streak);

		StringBuilder base = new StringBuilder();
		base.append(this.getPrefix());
		base.append("You now have a killstreak of ");
		base.append(this.getKillStreakColor()+Integer.toString(streak)+"&f");

		if (potion != null)
		{
			base.append(" and have been rewarded the powerup ");
			base.append("&e"+potion.getType().getName()+"&f!");
		} else {
			base.append("!");
		}

		return this.parseColors(base.toString());
	}

	public String getDeathMessage(String username)
	{
		int streak = this.manager.getKills(username);

		StringBuilder base = new StringBuilder();
		base.append(this.getPrefix());
		base.append("Your killstreak was ");
		base.append(this.getKillStreakColor()+Integer.toString(streak));

		return this.parseColors(base.toString());
	}

	public String getStreak(String username, boolean self)
	{
		int streak = this.manager.getKills(username);

		StringBuilder base = new StringBuilder();
		base.append(this.getPrefix());
		if (self)
		{
			base.append("Your killstreak is ");
		} else {
			base.append(this.getUsernameColor()+username+" has a killstreak of ");
		}
		base.append(this.getKillStreakColor()+Integer.toString(streak));

		return this.parseColors(base.toString());
	}

	public String getStreak(String username)
	{
		return this.getStreak(username, true);
	}
}
