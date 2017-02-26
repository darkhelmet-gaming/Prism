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
package com.helion3.prism.api.records;

public enum SkipReason {
    /**
     * Block isn't safe and may not be places in the world.
     */
    ILLEGAL_BLOCK,

    /**
     * World or location are missing. Likely if a record was made for
     * a world which no longer exists.
     */
    INVALID_LOCATION,

    /**
     * Data is invalid. Likely due to mods being removed or
     * data being changed between updates.
     */
    INVALID,

    /**
     * Location or target we're attempting to change exists in
     * a state we don't expect. For example, if you try to rollback
     * a block but someone has already placed a new block in that
     * spot.
     */
    OCCUPIED,

    /**
     * Action has yet to be implemented.
     */
    UNIMPLEMENTED,

    /**
     * An unknown error occurred.
     */
    UNKNOWN
}
