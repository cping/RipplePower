package org.ripple.power.config;

public interface Interruptible {

	public void setCancelled(boolean cancelled);

	public void interrupt();

}
