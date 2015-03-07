package com.helion3.prism.queues;

import java.util.ArrayList;
import java.util.List;

import com.helion3.prism.Prism;
import com.helion3.prism.api.events.Event;

public class RecordingQueueManager extends Thread {
	
	@Override
	public void run() {
		
		while(true){
			
			System.out.println("Recording manager checking queue...");
			
			List<Event> eventsSaveBatch = new ArrayList<Event>();
		
			// Assume we're iterating everything in the queue
			while( !RecordingQueue.getQueue().isEmpty() ){
				
				// Poll the next event, append to list
				Event event = RecordingQueue.getQueue().poll();
				if( event != null ){
					eventsSaveBatch.add(event);
				}
			}
			
			if( eventsSaveBatch.size() > 0 ){
				try {
					Prism.getStorageAdapter().write(eventsSaveBatch);
				} catch (Exception e) {
					// @todo handle failures
					e.printStackTrace();
				}
			}
			
			// Delay next execution
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}