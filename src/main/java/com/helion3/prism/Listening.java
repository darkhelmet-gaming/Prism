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

public class Listening {
    public final boolean BREAK;
    public final boolean DEATH;
    public final boolean DECAY;
    public final boolean DROP;
    public final boolean GROW;
    public final boolean JOIN;
    public final boolean PICKUP;
    public final boolean PLACE;
    public final boolean QUIT;

    public Listening() {
        BREAK = Prism.getInstance().getConfiguration().getNode("events", "break").getBoolean();
        DEATH = Prism.getInstance().getConfiguration().getNode("events", "death").getBoolean();
        DECAY = Prism.getInstance().getConfiguration().getNode("events", "decay").getBoolean();
        DROP = Prism.getInstance().getConfiguration().getNode("events", "drop").getBoolean();
        GROW = Prism.getInstance().getConfiguration().getNode("events", "grow").getBoolean();
        JOIN = Prism.getInstance().getConfiguration().getNode("events", "join").getBoolean();
        PICKUP = Prism.getInstance().getConfiguration().getNode("events", "pickup").getBoolean();
        PLACE = Prism.getInstance().getConfiguration().getNode("events", "place").getBoolean();
        QUIT = Prism.getInstance().getConfiguration().getNode("events", "quit").getBoolean();
    }
}
