package io.github.bananapuncher714.overdrive.implementation.v1_19_R1;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import org.bukkit.Bukkit;

import io.github.bananapuncher714.overdrive.api.NMSHandler;
import net.minecraft.SystemUtils;
import net.minecraft.server.MinecraftServer;

public class Handler_v1_19_R1 implements NMSHandler {
	private static Field nextTick;
	private static boolean is1 = false;
	
	static {
		try {
			nextTick = MinecraftServer.class.getDeclaredField( "ag" );
			nextTick.setAccessible( true );
		} catch ( NoSuchFieldException | SecurityException e ) {
			e.printStackTrace();
		}
		
		is1 = Bukkit.getBukkitVersion().startsWith( "1.19.1" );
	}
	
	protected static long tickLength = 50L;
	
	public Handler_v1_19_R1() {
		try {
			SystemUtils.class.getField( "a" ).set( null, new LongSupplier() {
				@Override
				public long getAsLong() {
					return nanoTime();
				}
			} );
		} catch ( Exception e ) {
			try {
				SystemUtils.class.getField( "a" ).set( null, new CustomTimeSource( this::nanoTime ) );
			} catch ( IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1 ) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public long getTickDuration() {
		return tickLength;
	}

	@Override
	public void setTickDuration( long duration ) {
		tickLength = duration;		
	}
	
	private long nanoTime() {
		if ( needsUpdate() ) {
			long time = System.nanoTime() + ( TimeUnit.MILLISECONDS.toNanos( tickLength - 50L ) );
			
			try {
				long curTick = nextTick.getLong( MinecraftServer.getServer() );
				
				curTick += ( tickLength - 50L );
				
				nextTick.set( MinecraftServer.getServer(), curTick );
			} catch ( IllegalArgumentException | IllegalAccessException e ) {
				e.printStackTrace();
			}
			
			return time;
		} else {
			return System.nanoTime();
		}
	}
	
	private boolean needsUpdate() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for ( int i = 0; i < elements.length; i++ ) {
			StackTraceElement element = elements[ i ];
			int lineNumber = element.getLineNumber();
			// 826 for Spigot 1.15.1 and 1.15
			// 827 for Spigot 1.15.2
			// 936 for Paper 1.15.1 and 1.15
			// 852 for Spigot 1.16.1
			// 849 for Spigot 1.16.3
			// 850 for Spigot 1.16.4 and 1.16.5
			// 969 for Spigot 1.16.5?
			// 1029 and 1030 for Spigot 1.17
			// 1038 for Spigot 1.17.1
			// 1037 for Spigot 1.18.1
			// 1050 for Spigot 1.18.2
			// 1013 for Spigot 1.19
			// 1016 for Spigot 1.19.1
			// Unfortunately, Paper doesn't work since Aikar removed the SystemUtils usage
			if ( ( ( lineNumber == 1013 && !is1 ) || ( lineNumber == 1016 && is1 ) ) &&
					element.getClassName().equalsIgnoreCase( MinecraftServer.class.getName() ) ) {
				return true;
			}
		}
		return false;
	}
}
