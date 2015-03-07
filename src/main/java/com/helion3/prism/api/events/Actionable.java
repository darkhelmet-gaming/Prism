package com.helion3.prism.api.events;

public interface Actionable {
	
	/**
	 * Reverse the end result of this event.
	 */
	public void undo();
	
	/**
	 * Re-apply the effect(s) of this event.
	 */
	public void redo();

}