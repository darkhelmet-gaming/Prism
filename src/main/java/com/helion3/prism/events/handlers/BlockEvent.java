package com.helion3.prism.events.handlers;

import org.spongepowered.api.world.Location;

import com.helion3.prism.api.events.Event;

public class BlockEvent implements Event {
	
	private final String eventName;
	private final Location location;
	private final String existingBlockId;
	
	/**
	 * Represents an event which occurs to a specific block. Defaults
	 * the replacement block to an empty string.
	 * 
	 * @param eventName Prism parameter name for this event.
	 * @param location Location of the block affected.
	 * @param existingBlockId Minecraft ID for the existing block.
	 */
	public BlockEvent( String eventName, Location location, String existingBlockId ){
		this(eventName,location,existingBlockId,"");
	}
	
	/**
	 * Represents an event which occurs to a specific block.
	 * 
	 * @param eventName Prism parameter name for this event.
	 * @param location Location of the block affected.
	 * @param existingBlockId Minecraft ID for the existing block.
	 * @param replacementBlockId Minecraft ID for the replacement block.
	 */
	public BlockEvent( String eventName, Location location, String existingBlockId, String replacementBlockId ){
		this.eventName = eventName;
		this.location = location;
		this.existingBlockId = existingBlockId;
	}
	
	/**
	 * Returns the parameter name for this {@link Event}.
	 * 
	 * @return String name of the event
	 */
	@Override
	public String getName(){
		return eventName;
	}
	
	/**
	 * Returns the location for this {@link Event}.
	 * 
	 * @return Location of the event
	 */
	@Override
	public Location getLocation(){
		return location;	
	}
	
	/**
	 * Returns the existing block affected by this {@link Event}.
	 * 
	 * @return Minecraft block ID
	 */
	public String getExistingBlockId(){
		return existingBlockId;
	}
}