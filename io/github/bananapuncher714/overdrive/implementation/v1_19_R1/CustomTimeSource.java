package io.github.bananapuncher714.overdrive.implementation.v1_19_R1;

import java.util.function.LongSupplier;

import net.minecraft.util.TimeSource;

public class CustomTimeSource implements TimeSource.a {
	private LongSupplier supplier;
	
	protected CustomTimeSource( LongSupplier supplier ) {
		this.supplier = supplier;
	}
	
	@Override
	public long getAsLong() {
		return supplier.getAsLong();
	}

}
