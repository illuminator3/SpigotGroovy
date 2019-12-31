/*
 * Copyright (C) 2019-2020 Jonas Hardt aka illuminator3
 *
 * This file is part of SpigotGroovy.
 *
 * SpigotGroovy can not be copied and/or distributed without the express permission of illuminator3
 */

package me.illuminator3.sgroovy.core;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import lombok.SneakyThrows;
import lombok.var;
import me.illuminator3.sgroovy.api.SpigotPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AddonLoader
{
    @NotNull
    @Contract("null -> new")
    public static Addon[] loadAddons(final File folder)
    {
        if (folder == null || !folder.isDirectory() || folder.listFiles() == null || Objects.requireNonNull(folder.listFiles()).length == 0)
            return new Addon[] {};

        var addons = new ArrayList<Addon>();
        var subFolders = folder.listFiles(File::isDirectory);

        if (subFolders == null || subFolders.length == 0)
            return new Addon[] {};

        for (final File addonFolder : subFolders)
        {
            try
            {
                var file = new File(folder, String.valueOf(new File(addonFolder.getName(), "addon.xml")));

                var dbFactory = DocumentBuilderFactory.newInstance();
                var dBuilder = dbFactory.newDocumentBuilder();
                var doc = dBuilder.parse(file);

                doc.getDocumentElement().normalize();

                var list = doc.getElementsByTagName("addon");

                String
                        name = "",
                        version = "",
                        author = "",
                        main = "";

                for (int i = 0 ; i < list.getLength() ; i++)
                {
                    var element = (Element) list.item(i);

                    name = element.getElementsByTagName("name").item(0).getTextContent();
                    version = element.getElementsByTagName("version").item(0).getTextContent();
                    author = element.getElementsByTagName("author").item(0).getTextContent();
                    main = element.getElementsByTagName("main").item(0).getTextContent();
                }

                addons.add(new Addon(name, version, author, main, addonFolder));
            } catch (final ParserConfigurationException | SAXException | IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }

        return addons.toArray(new Addon[] {});
    }

    @SneakyThrows
    public static boolean enable(@NotNull final Addon addon)
    {
        var engine = new GroovyScriptEngine("");

        var clazzObj = engine.run(addon.getFolder() + "/" + addon.getMain() + ".groovy", new Binding());

        if (clazzObj instanceof SpigotPlugin)
        {
            var method = clazzObj.getClass().getDeclaredMethod("enable");

            method.setAccessible(true);

            method.invoke(AddonLoader.class);

            return true;
        }
        else
        {
            SpigotGroovy.getInstance().getLogger().warning("Main class isn't a SpigotPlugin");

            return false;
        }
    }

    @SuppressWarnings("deprecation")
    @SneakyThrows
    public static void disable(@NotNull final Addon addon)
    {
        var addonFolder = addon.getFolder();

        var engine = new GroovyScriptEngine(String.valueOf(addonFolder.toURL()));

        var clazzObj = engine.run(addon.getMain(), new Binding());

        if (clazzObj instanceof SpigotPlugin)
        {
            var method = clazzObj.getClass().getDeclaredMethod("disable");

            method.setAccessible(true);

            method.invoke(AddonLoader.class);
        }
        else
        {
            SpigotGroovy.getInstance().getLogger().warning("Main class isn't a SpigotPlugin");
        }
    }
}