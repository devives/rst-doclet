/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation..
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.devives.samples;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sample interface illustrate different method declarations.
 */
public interface SampleMethodDeclarations<T> {

    /**
     * Simple procedure.
     */
    void procedure();

    void procedureWithoutJavaDoc(String arg1, List<?> arg2); // `No javadoc` is correct.

    /**
     * Procedure with variable arguments.
     *
     * @param args Array of any object.
     */
    void varArgProcedure(Object... args);

    /**
     * Procedure with variable arguments.
     *
     * @param args Array of {@code <T>}.
     */
    void varArgProcedure2(T... args);

    /**
     * Procedure with array arguments.
     *
     * @param args Array of {@code <T>}.
     */
    void arrayProcedure(T[] args);

    /**
     * Simple function with boolean result.
     *
     * @return {@code true} or <code>false</code>.
     */
    boolean functionReturnsPrimitive();


    /**
     * Simple function with Object result.
     *
     * @return {@code true} or <code>false</code>.
     */
    Object functionReturnsObject();

    /**
     * Parametrized function.
     *
     * @param argument First argument
     * @param <R>      type of result
     * @return instance of {@code <R>}.
     */
    <R> R parametrisedFunction(R argument);

    /**
     * Parametrized function 2.
     *
     * @param argument First argument
     * @param <R>      type of result
     * @return instance of {@code <R>}.
     */
    <R extends List> R parametrisedFunction2(R argument);

    /**
     * Parametrized function 3.
     *
     * @param <R> type of result
     * @return instance of {@code <R>}.
     */
    <R extends SampleInterface> List<Collection<SampleInterface2<HashMap<String, R>>>> parametrisedFunction3();

    /**
     * Parametrized function.
     *
     * @param argument1 First argument
     * @param argument2 Second argument
     * @param <R>       type of result
     * @return instance of {@code <R>}.
     */
    <R extends List<String>> R parametrisedFunction3(R argument1, R argument2);

    /**
     * Default function.
     *
     * @param argument Argument
     * @return Return {@code null}.
     */
    default String defaultFunction(String argument) {
        return null;
    }

    /**
     * Method copy source list content into destination list.
     *
     * @param src  Source
     * @param dest Destination
     * @param <T>  Item type
     */
    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        for (int i = 0; i < src.size(); i++)
            dest.set(i, src.get(i));
    }

    /**
     * Method with class type parameter.
     * @param map map
     * @return Some object
     * @param <R> return type
     */
    default <R> R methodWithClassTypeParameter(Map<T, R> map) {
        return null;
    }
}
