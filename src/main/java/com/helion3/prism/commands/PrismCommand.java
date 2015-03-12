/**
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
package com.helion3.prism.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

final public class PrismCommand implements CommandCallable {

    private Optional<String> shortDescription = Optional.of("Prism parent command.");
    private Optional<String> help = Optional.of("todo"); // @todo

    /**
     * Command has been called.
     */
    @Override
    public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
        
        // @todo this all is pending better command registry, even if home-rolled
        
        String[] args = arguments.split(" ");

        if (args.length > 0) {
            
            // build the new args without this level command
            String newArgs = "";
            for(int i=1;i<args.length;i++){
                newArgs += (i == args.length - 1) ? args[i] : args[i] + " ";
            }
            
            if (args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("lookup")) {
                LookupCommand subcmb = new LookupCommand();
                subcmb.call(source, newArgs, parents);
            }
        }
        
        return true;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public String getUsage() {
        return "/prism ?";
    }
    
    @Override
    public Optional<String> getHelp() {
        return help;
    }
    
    @Override
    public Optional<String> getShortDescription() {
        return shortDescription;
    }
    
    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return new ArrayList<String>();
    }
}