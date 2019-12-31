/*
 * Copyright (C) 2019-2020 Jonas Hardt aka illuminator3
 *
 * This file is part of SpigotGroovy.
 *
 * SpigotGroovy can not be copied and/or distributed without the express permission of illuminator3
 */

package me.illuminator3.sgroovy.core;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.var;
import me.illuminator3.sgroovy.api.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class SpigotGroovy
    extends JavaPlugin
{
    @Getter
    private final List<Addon> addons = new ArrayList<>();
    @Getter
    private final List<Addon> enabledAddons = new ArrayList<>();
    private final Logger log = getLogger();

    @Getter
    private final File addonFolder = new File(getDataFolder(), "addons");

    @Override
    public void onLoad()
    {
        InstanceHelper.set(getClass(), this);
    }

    @Override
    public void onEnable()
    {
        if (!addonFolder.exists())
            addonFolder.mkdirs();

        this.addons.addAll(Arrays.asList(AddonLoader.loadAddons(addonFolder)));

        for (var addon : addons)
        {
            log.config("Enabling addon " + addon.getName() + " v" + addon.getVersion() + " by " + addon.getAuthor() + " which main is " + addon.getMain());

            if (AddonLoader.enable(addon))
                enabledAddons.add(addon);
        }
    }

    @Override
    public void onDisable()
    {
        if (!addonFolder.exists())
            addonFolder.mkdirs();

        for (var addon : enabledAddons)
        {
            log.config("Disabling addon " + addon.getName() + " v" + addon.getVersion() + " by " + addon.getAuthor() + " which main is " + addon.getMain());

            AddonLoader.disable(addon);
        }
    }

    @SneakyThrows
    private void registerCommand(@NotNull final Command command)
    {
        final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

        bukkitCommandMap.setAccessible(true);

        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

        commandMap.register(command.getName(), new org.bukkit.command.Command(command.getName(), command.getDescription(), "", command.getAliases())
        {
            @Override
            public boolean execute(CommandSender commandSender, String s, String[] strings)
            {
                command.run(new me.illuminator3.sgroovy.api.entity.CommandSender()
                {
                    @Override
                    public String getName()
                    {
                        return commandSender.getName();
                    }

                    @Override
                    public void sendMessage(final String msg)
                    {
                        commandSender.sendMessage(msg);
                    }
                }, strings);

                return false;
            }
        });
    }

    public static SpigotGroovy getInstance()
    {
        return (SpigotGroovy) InstanceHelper.get(SpigotGroovy.class);
    }
}