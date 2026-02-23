package net.goldtreeservers.worldguardextraflags.flags.data;

import java.util.concurrent.TimeUnit;

public class PotionEffectDetails {

	private final long endTime;
	private final int amplifier;
	private final boolean ambient;
	private final boolean particles;

	public PotionEffectDetails(long endTime, int amplifier, boolean ambient, boolean particles) {
		this.endTime = endTime;
		this.amplifier = amplifier;
		this.ambient = ambient;
		this.particles = particles;
	}
	
	public long getTimeLeft() {
		return (this.endTime - System.nanoTime());
	}
	
	public int getTimeLeftInTicks() {
		return (int)(this.getTimeLeft() / TimeUnit.MILLISECONDS.toNanos(50L));
	}

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean isParticles() {
        return particles;
    }
}