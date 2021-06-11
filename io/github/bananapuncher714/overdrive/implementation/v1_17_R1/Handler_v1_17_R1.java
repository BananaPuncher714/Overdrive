package io.github.bananapuncher714.overdrive.implementation.v1_17_R1;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import io.github.bananapuncher714.overdrive.api.NMSHandler;
import net.minecraft.SystemUtils;
import net.minecraft.server.MinecraftServer;

public class Handler_v1_17_R1 implements NMSHandler {
	private static Field nextTick;
	
	static {
		try {
			nextTick = MinecraftServer.class.getDeclaredField( "ao" );
			nextTick.setAccessible( true );
		} catch ( NoSuchFieldException | SecurityException e ) {
			e.printStackTrace();
		}
	}
	
	protected static long tickLength = 50L;
	
	public Handler_v1_17_R1() {
		SystemUtils.a = this::nanoTime;
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
			// 1029 for Spigot 1.17
			// Unfortunately, Paper doesn't work since Aikar removed the SystemUtils usage
			if ( ( lineNumber == 1029 ) && element.getClassName().equalsIgnoreCase( MinecraftServer.class.getName() ) ) {
				return true;
			}
		}
		return false;
	}
}