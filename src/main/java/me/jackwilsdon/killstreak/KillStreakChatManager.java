package me.jackwilsdon.killstreak;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
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

		String message = String.format("%s%s%s&f has a killstreak of %s%d&f and has been rewarded the powerup &e%s&f!",
                this.getPrefix(), this.getUsernameColor(), username, this.getKillStreakColor(), streak, powerup);

		return this.parseColors(message);
	}

	public String getMessage(String username)
	{
		int streak = this.manager.getKills(username);
		PotionEffect potion = this.manager.getPotionEffect(streak);

        String suffix = "!";

		if (potion != null)
		{
			suffix = String.format(" and have been rewarded the powerup &e%s&f!", potion.getType().getName());
		}

        String message = String.format("%sYou now have a killstreak of %s%d&f%s",
                this.getPrefix(), this.getKillStreakColor(), streak, suffix);

        return this.parseColors(message);
	}

	public String getDeathMessage(String username)
	{
		int streak = this.manager.getKills(username);

        String message = String.format("%sYour killstreak was %s%d", this.getPrefix(), this.getKillStreakColor(), streak);

		return this.parseColors(message);
	}

	public String getStreak(String username, boolean self)
	{
		int streak = this.manager.getKills(username);

		String prefix = "Your killstreak is ";

		if (!self)
		{
            prefix = String.format("%s%s has a killstreak of ", this.getUsernameColor(), username);
		}

        String message = String.format("%s%s%s%d", this.getPrefix(), prefix, this.getKillStreakColor(), streak);

		return this.parseColors(message);
	}

	public String getStreak(String username)
	{
		return this.getStreak(username, true);
	}
}
