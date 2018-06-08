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

package com.ianbuttimer.tidder.utils;

import java.util.Objects;

/**
 * Container to ease passing around a tuple of three objects.
 * @param <T1>  Type of first element
 * @param <T2>  Type of second element
 * @param <T3>  Type of third element
 */
public class Triplet<T1, T2, T3> {

    public T1 first;
    public T2 second;
    public T3 third;

    public Triplet(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <C1, C2, C3> Triplet<C1, C2, C3> create(C1 first, C2 second, C3 third) {
        return new Triplet<>(first, second, third);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(first, triplet.first) &&
                Objects.equals(second, triplet.second) &&
                Objects.equals(third, triplet.third);
    }

    @Override
    public int hashCode() {

        return Objects.hash(first, second, third);
    }
}
