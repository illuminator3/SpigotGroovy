/*
 * Copyright (C) 2019-2020 Jonas Hardt aka illuminator3
 *
 * This file is part of SpigotGroovy.
 *
 * SpigotGroovy can not be copied and/or distributed without the express permission of illuminator3
 */

package me.illuminator3.sgroovy.api.command

import me.illuminator3.sgroovy.api.entity.CommandSender

interface Command
{
    void run(final CommandSender sender, final String[] args);

    String getName();
    String getDescription();
    List<String> getAliases();
}