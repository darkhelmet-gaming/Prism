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

package com.helion3.prism;

public final class Listening {

    public final boolean blockBreak = Prism.getInstance().getConfiguration().getNode("events", "break").getBoolean();
    public final boolean blockDecay = Prism.getInstance().getConfiguration().getNode("events", "decay").getBoolean();
    public final boolean blockGrow = Prism.getInstance().getConfiguration().getNode("events", "grow").getBoolean();
    public final boolean blockPlace = Prism.getInstance().getConfiguration().getNode("events", "place").getBoolean();
    public final boolean entityDeath = Prism.getInstance().getConfiguration().getNode("events", "death").getBoolean();
    public final boolean commandExecute = Prism.getInstance().getConfiguration().getNode("events", "command").getBoolean();
    public final boolean itemDrop = Prism.getInstance().getConfiguration().getNode("events", "drop").getBoolean();
    public final boolean itemInsert = Prism.getInstance().getConfiguration().getNode("events", "insert").getBoolean();
    public final boolean itemPickup = Prism.getInstance().getConfiguration().getNode("events", "pickup").getBoolean();
    public final boolean itemRemove = Prism.getInstance().getConfiguration().getNode("events", "remove").getBoolean();
    public final boolean playerDisconnect = Prism.getInstance().getConfiguration().getNode("events", "quit").getBoolean();
    public final boolean playerJoin = Prism.getInstance().getConfiguration().getNode("events", "join").getBoolean();
}