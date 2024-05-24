/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 * <p>
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation..
 * <p>
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.devives.samples;

import com.devives.samples.inners.SampleRootInterface;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Sample class illustrate different field declarations.
 *
 * @param <T> Generic type parameter.
 */
public class SampleFieldDeclarations<T> {
    /**
     * Private field
     */
    private Object privateField;
    /**
     * Protected field.
     */
    protected Object protectedField;
    /**
     * Public field.
     */
    public Object publicField;
    /**
     * Package private field.
     */
    Object packagePrivateField;
    /**
     * Public field holds an interface reference.
     */
    public SampleInterface interfaceField;
    /**
     * Public field holds primitive value.
     */
    public boolean booleanField;
    /**
     * Public field holds generic type object reference.
     */
    public T genericField1;
    /**
     * Public field holds generic list reference.
     */
    public List<T> genericField2;
    /**
     * Public field holds string list reference.
     */
    public List<String> genericField3;
    /**
     * Public field holds object with nested generic types.
     */
    public List<SampleInterface2<String>> genericField4;
    /**
     * Public field holds object with nested generic types.
     */
    public List<Collection<SampleInterface2<HashMap<String, T>>>> genericField5;
    /**
     * Field of {@code com.devives.samples.inners.SampleRootInterface} type.
     */
    public SampleRootInterface<?> subpackageField;

}
