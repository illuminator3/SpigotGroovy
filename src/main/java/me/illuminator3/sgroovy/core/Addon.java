/*
 * Copyright (C) 2019-2020 Jonas Hardt aka illuminator3
 *
 * This file is part of SpigotGroovy.
 *
 * SpigotGroovy can not be copied and/or distributed without the express permission of illuminator3
 */

package me.illuminator3.sgroovy.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@AllArgsConstructor
@Data
public class Addon
{
    private final String

            name,
            version,
            author,
            main;

    private final File folder;
}