package me.jackwilsdon.killstreak;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles and executes commands for KillStreak
 * @author Jack Wilsdon
 */
public class KillStreakCommandExecutor implements CommandExecutor {
	private KillStreakManager manager = null;
	
	/**
	 * Pass an instance of KillStreakManager to use
	 * @param manager An instance of KillStreakManager
	 */
	public KillStreakCommandExecutor(KillStreakManager manager)
	{
		this.manager = manager;
	}
	
	/**
	 * Check if the CommandSender is a player
	 * @param sender The CommandSender to check
	 * @return True if the sender is a player, false if the sender is not a player
	 */
	private boolean isPlayer(CommandSender sender)
	{
		return sender instanceof Player;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		if (this.isPlayer(sender))
		{
			if (args.length == 0)
			{
				String message = this.manager.getChatManager().getStreak(sender.getName());
				sender.sendMessage(message);
			}
			
			if (args.length == 1)
			{
				String username = args[1];
				if (!this.manager.playerExists(username))
				{
					String message = this.manager.getChatManager().getPrefix()+"That player does not exist!";
					sender.sendMessage(this.manager.getChatManager().parseColors(message));
					return true;
				}
				
				String message = this.manager.getChatManager().getStreak(username, false);
				sender.sendMessage(message);
			}
		} else {
			if (args.length == 0)
			{
				String message = "Usage: /"+label+" <username>";
				sender.sendMessage(message);
			}
			
			if (args.length == 1)
			{
				String username = args[1];
				if (!this.manager.playerExists(username))
				{
					sender.sendMessage("That player does not exist!");
					return true;
				}
				
				String message = this.manager.getChatManager().getStreak(username, false);
				sender.sendMessage(message);
			}
		}

		return true;
	}
}
