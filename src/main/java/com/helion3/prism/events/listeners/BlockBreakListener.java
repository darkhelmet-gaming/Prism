package com.helion3.prism.events.listeners;

import org.spongepowered.api.block.BlockLoc;
import org.spongepowered.api.event.block.BlockBreakEvent;
import org.spongepowered.api.util.event.Subscribe;

import com.helion3.prism.events.handlers.BlockEvent;
import com.helion3.prism.queues.RecordingQueue;

public class BlockBreakListener {
	
	@Subscribe
	public void onBlockBreak(BlockBreakEvent event) {
		
		BlockLoc blockLoc = event.getBlock();
		String existingBlockId = blockLoc.getType().getId();

		// Build and record the event
		BlockEvent blockBreakEvent = new BlockEvent("block-break", blockLoc.getLocation(), existingBlockId);
		RecordingQueue.add(blockBreakEvent);
		
	}
}