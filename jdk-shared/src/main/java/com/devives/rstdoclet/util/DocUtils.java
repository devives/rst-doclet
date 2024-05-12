/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.devives.rstdoclet.util;

import com.devives.rstdoclet.RstDocletComponentFactory;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DocUtils {

    public static String formatTypeName(TypeMirror type, Utils utils) {
        String result = type.toString();
        if (!type.getKind().isPrimitive()) {
            TypeElement typeElement = utils.asTypeElement(type);
            if (typeElement != null){
                Map<String, TypeElement> refClasses = new HashMap<>();
                refClasses.put(utils.getQualifiedTypeName(type), typeElement);
                refClasses.putAll(RstDocletComponentFactory.getInstance().newImportsCollector(utils).collect(typeElement).getImportsMap());
                for (Map.Entry<String, TypeElement> entry : refClasses.entrySet()) {
                    result = result.replace(entry.getKey(), entry.getValue().getSimpleName().toString());
                }
            }
        }
        return result;
    }

    public static String formatAnnotations(TypeElement classDoc) {
        String result = "";
        if (classDoc.getAnnotationMirrors().size() > 0) {
            result = classDoc.getAnnotationMirrors().stream()
                    .filter(an -> !(((TypeElement)an.getAnnotationType().asElement()).getQualifiedName().toString().startsWith("java.lang.annotation.")))
                    .map(a -> "@" + a.getAnnotationType().asElement().getSimpleName().toString())
                    .collect(Collectors.joining(" "));
        }
        return result;
    }

}