/*
 * Copyright (C) 2019-2020 Jonas Hardt aka illuminator3
 *
 * This file is part of SpigotGroovy.
 *
 * SpigotGroovy can not be copied and/or distributed without the express permission of illuminator3
 */

package me.illuminator3.sgroovy.api

import lombok.Getter
import me.illuminator3.sgroovy.api.command.Command
import me.illuminator3.sgroovy.core.SpigotGroovy
import org.bukkit.event.Listener

import java.util.logging.Logger

@SuppressWarnings("unused")
abstract class SpigotPlugin
{
    @Getter
    final List<Command> commands = new ArrayList<>()
    @Getter
    final List<EventListener> listeners = new ArrayList<>()

    abstract void onEnable()
    abstract void onDisable()

    protected final void enable()
    {
        this.onEnable()

        for (def command : commands)
        {
            def method = SpigotGroovy.getClass().getDeclaredMethod("registerCommand")

            method.setAccessible(true)

            method.invoke(SpigotPlugin.class, command)
        }

        for (def listener : listeners)
            SpigotGroovy.getInstance().getServer().getPluginManager().registerEvents(listener as Listener, SpigotGroovy.getInstance())
    }

    protected final void disable()
    {
        this.onDisable()
    }

    final void registerCommand(final Command command)
    {
        commands.add(command)
    }

    final void addListener(final EventListener listener)
    {
        listeners.add(listener)
    }

    static final Logger getLogger()
    {
        return SpigotGroovy.getInstance().getLogger()
    }
}