package com.helion3.prism.api.events;

import org.spongepowered.api.world.Location;

public interface Event {
	
	/**
	 * Returns the parameter name for this {@link Event}.
	 * 
	 * @return String name of the event
	 */
	public String getName();
	
	/**
	 * Returns the location for this {@link Event}.
	 * 
	 * @return Location of the event
	 */
	public Location getLocation();

}