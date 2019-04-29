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

package com.helion3.prism.configuration.category;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class LimitCategory {

    @Setting(value = "maximum-actionable")
    private int maximumActionable = 10000;

    @Setting(value = "maximum-lookup")
    private int maximumLookup = 1000;

    @Setting(value = "maximum-radius")
    private int maximumRadius = 100;

    public int getMaximumActionable() {
        return maximumActionable;
    }

    public void setMaximumActionable(int maximumActionable) {
        this.maximumActionable = maximumActionable;
    }

    public int getMaximumLookup() {
        return maximumLookup;
    }

    public void setMaximumLookup(int maximumLookup) {
        this.maximumLookup = maximumLookup;
    }

    public int getMaximumRadius() {
        return maximumRadius;
    }

    public void setMaximumRadius(int maximumRadius) {
        this.maximumRadius = maximumRadius;
    }
}