/*
 * Copyright (C) 2019-2020 Jonas Hardt aka illuminator3
 *
 * This file is part of SpigotGroovy.
 *
 * SpigotGroovy can not be copied and/or distributed without the express permission of illuminator3
 */

package me.illuminator3.sgroovy.core;

import java.util.HashMap;
import java.util.Map;

public class InstanceHelper
{
    private static final Map<Class<?>, Object> instances = new HashMap<>();

    public static void set(final Class<?> clazz, final Object instance)
    {
        instances.put(clazz, instance);
    }

    public static Object get(final Class<?> clazz)
    {
        return instances.get(clazz);
    }
}