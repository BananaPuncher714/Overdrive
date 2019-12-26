package io.github.bananapuncher714.overdrive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bananapuncher714.overdrive.api.NMSHandler;

public class Overdrive extends JavaPlugin {
	private NMSHandler handler;
	
	@Override
	public void onEnable() {
		handler = ReflectionUtil.getNewNMSHandler();
		
		if ( handler == null ) {
			getLogger().severe( ReflectionUtil.VERSION + " is not supported! Disabling Overdrive..." );
			setEnabled( false );
		} else {
			getLogger().info( "Detected version " + ReflectionUtil.VERSION );
		}
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( args.length == 1 ) {
			int tickrate = 20;
			try {
				tickrate = Integer.valueOf( args[ 0 ] );
			} catch ( Exception exception ) {
				sender.sendMessage( ChatColor.RED + "Please provide an integer!" );
				return false;
			}
			double tickSpeed = 1000.0 / tickrate;
			handler.setTickDuration( ( int ) tickSpeed );
			sender.sendMessage( ChatColor.WHITE + "Tickrate set to " + ChatColor.YELLOW + tickrate + ChatColor.WHITE + " ticks per second" );
		} else {
			double tickrate = 1000.0 / handler.getTickDuration();
			sender.sendMessage( ChatColor.WHITE + "Current tickrate is " + ChatColor.YELLOW + ( ( int ) tickrate ) + ChatColor.WHITE + "(" + ChatColor.YELLOW + handler.getTickDuration() + "ms" + ChatColor.WHITE + ") ticks per second." );
		}
		return true;
	}
	
	public NMSHandler getHandler() {
		return handler;
	}
}
