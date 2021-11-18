/*
 * Copyright (C) 2018  Ian Buttimer
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ianbuttimer.tidderish.event;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
public @interface EventMode {

    int MODE_NA = 0;
    int NEW_REQUEST = 1;
    int UPDATE_REQUEST = 2;

    /** Defines the allowed constants for this element */
    int[] value() default {
            MODE_NA, NEW_REQUEST, UPDATE_REQUEST
    };

    /** Defines whether the constants can be used as a flag, or just as an enum (the default) */
    boolean flag() default false;
}

