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
 * Container to ease passing around a tuple of four objects.
 * @param <T1>  Type of first element
 * @param <T2>  Type of second element
 * @param <T3>  Type of third element
 * @param <T4>  Type of fourth element
 */
public class Quartet<T1, T2, T3, T4> {

    public T1 first;
    public T2 second;
    public T3 third;
    public T4 fourth;

    public Quartet(T1 first, T2 second, T3 third, T4 fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <C1, C2, C3, C4> Quartet<C1, C2, C3, C4> create(C1 first, C2 second, C3 third, C4 fourth) {
        return new Quartet<>(first, second, third, fourth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quartet<?, ?, ?, ?> quartet = (Quartet<?, ?, ?, ?>) o;
        return Objects.equals(first, quartet.first) &&
                Objects.equals(second, quartet.second) &&
                Objects.equals(third, quartet.third) &&
                Objects.equals(fourth, quartet.fourth);
    }

    @Override
    public int hashCode() {

        return Objects.hash(first, second, third, fourth);
    }
}
