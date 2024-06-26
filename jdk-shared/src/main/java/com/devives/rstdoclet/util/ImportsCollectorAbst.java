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

import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public abstract class ImportsCollectorAbst implements ImportsCollector {

    private final Map<String, TypeElement> map_ = new HashMap<>();
    private final Utils utils_;
    private final Set<String> processingNames_ = new HashSet<>();

    public ImportsCollectorAbst(Utils utils) {
        utils_ = utils;
    }

    public ImportsCollector collect(Element element) {
        if (element instanceof ExecutableElement) {
            return collect((ExecutableElement) element);
        } else if (element instanceof VariableElement) {
            return collect((VariableElement) element);
        } else if (element instanceof TypeElement) {
            return collect((TypeElement) element);
        } else {
            return this;
        }
    }

    public ImportsCollector collect(ExecutableElement executableElement) {
        for (AnnotationMirror annotationDesc : executableElement.getAnnotationMirrors()) {
            collect(annotationDesc.getAnnotationType().asElement());
        }
        for (VariableElement parameter : executableElement.getParameters()) {
            if (!parameter.asType().getKind().isPrimitive()) {
                collect(parameter.asType());
            }
        }
        for (TypeMirror type : executableElement.getThrownTypes()) {
            collect(utils_.asTypeElement(type));
        }
//        for (ThrowsTree throwsTag : execMemberDoc.throwsTags()) {
//            collect(throwsTag.exceptionType());
//        }
        if (executableElement.getReturnType().getKind() != TypeKind.VOID
                && !executableElement.getReturnType().getKind().isPrimitive()) {
            collect(executableElement.getReturnType());
        }
        List<? extends TypeParameterElement> typeParameters = executableElement.getTypeParameters();
        if (!typeParameters.isEmpty()) {
            for (TypeParameterElement pType : typeParameters) {
                collect(pType.asType());
                utils_.getBounds(pType).forEach(this::collect);
            }
        }

        return this;
    }

    public ImportsCollector collect(VariableElement variableElement) {
        for (AnnotationMirror annotationDesc : variableElement.getAnnotationMirrors()) {
            if (isImportRequired(annotationDesc.getAnnotationType())) {
                collect(annotationDesc.getAnnotationType().asElement());
            }
        }
        if (isImportRequired(variableElement.asType())) {
            collect(variableElement.asType());
        }
        return this;
    }

    public ImportsCollector collect(TypeMirror typeMirror) {
        String fullyQualifiedName = utils_.getTypeName(typeMirror, true);
        if (processingNames_.contains(fullyQualifiedName)) {
            // Prevent stack overflow on `java.lang.Enum<E extends java.lang.Enum<E>>`
            return this;
        }
        processingNames_.add(fullyQualifiedName);
        try {
            if (isImportRequired(typeMirror)) {
                Optional.ofNullable(utils_.asTypeElement(typeMirror)).ifPresent(typeElem -> {
                    collect(typeElem);
                    List<? extends TypeParameterElement> typeParameters = typeElem.getTypeParameters();
                    if (!typeParameters.isEmpty()) {
                        collectTypeParameters(typeMirror);
                        for (TypeParameterElement pType : typeParameters) {
                            utils_.getBounds(pType).forEach(this::collect);
                        }
                    }
                });
            }
        } finally {
            processingNames_.remove(fullyQualifiedName);
        }
        return this;
    }

    protected abstract void collectTypeParameters(TypeMirror typeMirror);

    public ImportsCollector collect(TypeElement typeElement) {
        return collect(typeElement, false);
    }

    public ImportsCollector collect(TypeElement typeElement, boolean withSuperClasses) {
        map_.put(typeElement.getQualifiedName().toString(), typeElement);
        for (AnnotationMirror pAnnotationMirror : typeElement.getAnnotationMirrors()) {
            collect(pAnnotationMirror.getAnnotationType());
        }

        List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();

        for (TypeParameterElement pType : typeParameters) {
            utils_.getBounds(pType).forEach(this::collect);
        }

        if (withSuperClasses) {
            for (TypeMirror superTypeMirror : typeElement.getInterfaces()) {
                collect(superTypeMirror);
            }
            if (typeElement.getSuperclass() != null) {
                collect(typeElement.getSuperclass());
            }
        }

        return this;
    }

    private boolean isImportRequired(TypeMirror typeMirror) {
        if (typeMirror == null || typeMirror.getKind().isPrimitive()) {
            return false;
        }
        TypeElement classDoc = utils_.asTypeElement(typeMirror);
        if (classDoc != null && classDoc.getEnclosedElements() != null) {
            return true;
            //return !classDoc.containingPackage().name().equals(Object.class.getPackage().getName());
        }
        return false;
    }

    public Map<String, TypeElement> getImportsMap() {
        return map_;
    }
}
