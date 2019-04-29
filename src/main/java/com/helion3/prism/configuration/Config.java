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

package com.helion3.prism.configuration;

import com.helion3.prism.configuration.category.DefaultCategory;
import com.helion3.prism.configuration.category.EventCategory;
import com.helion3.prism.configuration.category.GeneralCategory;
import com.helion3.prism.configuration.category.LimitCategory;
import com.helion3.prism.configuration.category.StorageCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Config {

    @Setting(value = "default")
    private DefaultCategory defaultCategory = new DefaultCategory();

    @Setting(value = "event")
    private EventCategory eventCategory = new EventCategory();

    @Setting(value = "general")
    private GeneralCategory generalCategory = new GeneralCategory();

    @Setting(value = "limit")
    private LimitCategory limitCategory = new LimitCategory();

    @Setting(value = "storage")
    private StorageCategory storageCategory = new StorageCategory();

    public DefaultCategory getDefaultCategory() {
        return defaultCategory;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public GeneralCategory getGeneralCategory() {
        return generalCategory;
    }

    public LimitCategory getLimitCategory() {
        return limitCategory;
    }

    public StorageCategory getStorageCategory() {
        return storageCategory;
    }
}