package com.helion3.prism.queues;

import java.util.concurrent.LinkedBlockingQueue;

import com.helion3.prism.api.events.Event;

public class RecordingQueue {
	
	private static final LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
	
	/**
	 * Adds a new Event to the recording queue.
	 * 
	 * @param event Event to be queued for database write
	 */
	public static void add(final Event event) {

		if( event == null ){
			throw new IllegalArgumentException("Null event given to Prism recording queue.");
		}
		
        queue.add( event );

    }
	
	/**
	 * Returns all unsaved events in the queue.
	 * 
	 * @return Current unsaved {@link Event} queue
	 */
	public static LinkedBlockingQueue<Event> getQueue(){
		return queue;
	}
}