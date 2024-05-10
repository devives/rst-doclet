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
package com.devives.rstdoclet.util;

import com.sun.javadoc.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ImportsCollectorImpl {

    private final Map<String, ClassDoc> map_ = new HashMap<>();
    private int counter_ = 0;

    public ImportsCollectorImpl collect(Doc doc) {
        if (doc instanceof ExecutableMemberDoc) {
            return collect((ExecutableMemberDoc) doc);
        } else if (doc instanceof FieldDoc) {
            return collect((FieldDoc) doc);
        } else if (doc instanceof ClassDoc) {
            return collect((ClassDoc) doc);
        } else {
            return this;
        }
    }

    public ImportsCollectorImpl collect(ExecutableMemberDoc execMemberDoc) {
        for (AnnotationDesc annotationDesc : execMemberDoc.annotations()) {
            collect(annotationDesc.annotationType());
        }
        for (Parameter parameter : execMemberDoc.parameters()) {
            collect(parameter.type());
        }
        for (com.sun.javadoc.Type type : execMemberDoc.thrownExceptionTypes()) {
            collect(type);
        }
        for (ThrowsTag throwsTag : execMemberDoc.throwsTags()) {
            collect(throwsTag.exceptionType());
        }
        if (execMemberDoc instanceof MethodDoc) {
            collect(((MethodDoc) execMemberDoc).returnType());
        }
        return this;
    }

    public ImportsCollectorImpl collect(FieldDoc fieldDoc) {
        for (AnnotationDesc annotationDesc : fieldDoc.annotations()) {
            if (isImportRequired(annotationDesc.annotationType())) {
                collect(annotationDesc.annotationType());
            }
        }
        if (isImportRequired(fieldDoc.type())) {
            collect(fieldDoc.type());
        }
        return this;
    }

    public ImportsCollectorImpl collect(com.sun.javadoc.Type type) {
        if (counter_ > 20) {
            // Prevent stack overflow on `java.lang.Enum<E extends java.lang.Enum<E>>`
            return this;
        } else {
            counter_++;
        }
        if (isImportRequired(type)) {
            Optional.ofNullable(type.asClassDoc()).ifPresent(classDoc -> {
                collect(type.asClassDoc());
            });
            Optional.ofNullable(type.asParameterizedType()).ifPresent(parameterizedType -> {
                for (com.sun.javadoc.Type pType : parameterizedType.typeArguments()) {
                    collect(pType);
                }
            });
            Optional.ofNullable(type.asTypeVariable()).ifPresent(typeVariable -> {
                for (com.sun.javadoc.Type pType : typeVariable.bounds()) {
                    collect(pType);
                }
            });
        }
        counter_--;
        return this;
    }


    public ImportsCollectorImpl collect(ClassDoc classDoc) {
        return collect(classDoc, false);
    }

    public ImportsCollectorImpl collect(ClassDoc classDoc, boolean withSuperClasses) {
        if (counter_ > 20) {
            // Prevent stack overflow on `java.lang.Enum<E extends java.lang.Enum<E>>`
            return this;
        } else {
            counter_++;
        }
        map_.put(classDoc.qualifiedTypeName(), classDoc);
        if (withSuperClasses) {
            for (ClassDoc pInterfaceDesc : classDoc.interfaces()) {
                collect(pInterfaceDesc);
            }
            if (classDoc.superclassType() != null) {
                collect(classDoc.superclassType());
            }
        }
//        for (AnnotationDesc pAnnotationDesc : classDoc.annotations()) {
//            collect(pAnnotationDesc.annotationType());
//        }
        for (com.sun.javadoc.Type type : classDoc.typeParameters()) {
            collect(type);
        }
        counter_--;
        return this;
    }

    private boolean isImportRequired(Type type) {
        if (type == null || type.isPrimitive()) {
            return false;
        }
        ClassDoc classDoc = type.asClassDoc();
        if (classDoc != null && classDoc.containingPackage() != null) {
            return true;
            //return !classDoc.containingPackage().name().equals(Object.class.getPackage().getName());
        }
        return false;
    }

    public Map<String, ClassDoc> getImportsMap() {
        return map_;
    }
}
