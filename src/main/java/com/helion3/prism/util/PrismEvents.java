/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.helion3.prism.util;

import com.google.common.collect.Lists;
import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.api.records.BlockResult;
import com.helion3.prism.api.records.EntityResult;
import com.helion3.prism.api.records.ResultComplete;
import org.spongepowered.api.registry.CatalogRegistryModule;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PrismEvents {

    private PrismEvents() {
    }

    public static final PrismEvent BLOCK_BREAK = PrismEvent.of("break", "Block Break", "broke", BlockResult.class);

    public static final PrismEvent BLOCK_DECAY = PrismEvent.of("decay", "Block Decay", "decayed", BlockResult.class);

    public static final PrismEvent BLOCK_GROW = PrismEvent.of("grow", "Block Grow", "grew", BlockResult.class);

    public static final PrismEvent BLOCK_PLACE = PrismEvent.of("place", "Block Place", "placed", BlockResult.class);

    public static final PrismEvent ENTITY_DEATH = PrismEvent.of("death", "Entity Death", "killed", EntityResult.class);

    public static final PrismEvent COMMAND_EXECUTE = PrismEvent.of("command", "Command Execute", "executed", ResultComplete.class);

    public static final PrismEvent INVENTORY_CLOSE = PrismEvent.of("close", "Inventory Close", "closed", ResultComplete.class);

    public static final PrismEvent INVENTORY_OPEN = PrismEvent.of("open", "Inventory Open", "opened", ResultComplete.class);

    public static final PrismEvent ITEM_DROP = PrismEvent.of("drop", "Item Drop", "dropped", ResultComplete.class);

    public static final PrismEvent ITEM_INSERT = PrismEvent.of("insert", "Item Insert", "inserted", ResultComplete.class);

    public static final PrismEvent ITEM_PICKUP = PrismEvent.of("pickup", "Item Pickup", "picked up", ResultComplete.class);

    public static final PrismEvent ITEM_REMOVE = PrismEvent.of("remove", "Item Remove", "removed", ResultComplete.class);

    public static final PrismEvent PLAYER_DISCONNECT = PrismEvent.of("disconnect", "Player Disconnect", "left", ResultComplete.class);

    public static final PrismEvent PLAYER_JOIN = PrismEvent.of("join", "Player Join", "joined", ResultComplete.class);

    public static final CatalogRegistryModule<PrismEvent> REGISTRY_MODULE = new
        CatalogRegistryModule<PrismEvent>() {
            private final List<PrismEvent> prismEvents = Lists.newArrayList(
                BLOCK_BREAK,
                BLOCK_DECAY,
                BLOCK_GROW,
                BLOCK_PLACE,
                ENTITY_DEATH,
                COMMAND_EXECUTE,
                INVENTORY_CLOSE,
                INVENTORY_OPEN,
                ITEM_DROP,
                ITEM_INSERT,
                ITEM_PICKUP,
                ITEM_REMOVE,
                PLAYER_DISCONNECT,
                PLAYER_JOIN
            );
            private final Map<String, PrismEvent> prismEventMap = new HashMap<String, PrismEvent>() {{
              prismEvents.forEach(prismEvent -> put(prismEvent.getId(), prismEvent));
            }};

            @Nonnull
            @Override
            public Optional<PrismEvent> getById(@Nonnull String id) {
                return Optional.ofNullable(prismEventMap.get(id));
            }

            @Nonnull
            @Override
            public Collection<PrismEvent> getAll() {
                return prismEvents;
            }
        };

}