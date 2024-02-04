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

/**
 * Sample class illustrate different field declarations.
 *
 * @param <T> Generic type parameter.
 */
public class SampleFieldDeclarations<T> {

    private Object privateField;

    protected Object protectedField;

    public Object publicField;

    Object packagePrivateField;

    public SampleInterface interfaceField;

    public boolean booleanField;

    public T genericField1;

    public List<T> genericField2;

    public List<String> genericField3;

    public List<SampleInterface2<String>> genericField4;

    public List<Collection<SampleInterface2<HashMap<String, T>>>> genericField5;

}
