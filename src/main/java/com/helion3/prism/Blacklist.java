package com.helion3.prism;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;

public class Blacklist {
    private final List<String> blocks = new ArrayList<String>();
    private final List<String> players = new ArrayList<String>();

    /**
     * A block type to the black list.
     *
     * @param block BlockType
     */
    public void add(BlockType block) {
        addBlock(block.getId());
    }

    /**
     * Add a block type string to the black list.
     *
     * @param block String Block type string.
     */
    public void addBlock(String blockType) {
        blocks.add(blockType);
    }

    /**
     * Add a player uuid string to the black list.
     *
     * @param block String Player uuid string.
     */
    public void addPlayer(String uuid) {
        blocks.add(uuid);
    }

    /**
     * Get if blacklist contains a given BlockType.
     *
     * @param blockType BlockType
     * @return boolean If blacklist contains block type.
     */
    public boolean contains(BlockType blockType) {
        return containsBlock(blockType.toString());
    }

    /**
     * Check if the blacklist contains an object. Checks for
     * accepted types and passes of to their proper handlers.
     *
     * @param object Object
     * @return boolean If object is a known type and in the blacklist
     */
    public boolean contains(Object object) {
        boolean contains = false;

        if (object instanceof Player) {
            contains = contains((Player) object);
        }
        else if (object instanceof BlockType) {
            contains = contains((BlockType) object);
        }
        else if (object instanceof String) {

        }

        return contains;
    }

    /**
     * Get if blacklist contains a player's UUID.
     *
     * @param player
     * @return boolean If blacklist contains player uuid.
     */
    public boolean contains(Player player) {
        return players.contains(player.getUniqueId().toString());
    }

    /**
     * Get if blacklist contains a given block type.
     *
     * @param blockType String block type string
     * @return boolean If blacklist contains block type.
     */
    public boolean containsBlock(String blockType) {
        return blocks.contains(blockType);
    }
}
