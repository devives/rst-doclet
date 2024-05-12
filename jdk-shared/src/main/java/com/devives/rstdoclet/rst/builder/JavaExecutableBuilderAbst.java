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
package com.devives.rstdoclet.rst.builder;

import com.devives.html2rst.HtmlUtils;
import com.devives.rst.builder.BlockQuoteBuilder;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.ThrowsTree;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JavaExecutableBuilderAbst<PARENT extends RstNodeBuilder<?, ?, ?, ?>, SELF extends JavaExecutableBuilderAbst<PARENT, SELF>>
        extends JavaMemberBuilderAbst<PARENT, SELF> {
    private final ExecutableElement executableElement_;

    public JavaExecutableBuilderAbst(Directive.Type type, ExecutableElement memberDoc, RstGeneratorContext docContext) {
        super(type, memberDoc, docContext);
        this.executableElement_ = memberDoc;
    }

    @Override
    protected void fillElements(BlockQuoteBuilder<?> bodyBuilder) {
        super.fillElements(bodyBuilder);
        List<? extends DocTree> tags = utils_.getBlockTags(executableElement_);
        ParamTree[] paramTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.PARAM)
                .map(ParamTree.class::cast).toArray(ParamTree[]::new);
        ThrowsTree[] throwsTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.THROWS)
                .map(ThrowsTree.class::cast).toArray(ThrowsTree[]::new);
        ReturnTree[] returnTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.RETURN)
                .map(ReturnTree.class::cast).toArray(ReturnTree[]::new);

        bodyBuilder.fieldList(flb -> {
            for (ParamTree tag : paramTags) {
                flb.item("param " + formatParameterName(tag), ib ->
                        new CommentBuilder(
                                tag,
                                executableElement_,
                                docContext_).buildBody().forEach(ib::addChild));
            }

//            for (TypeParameterElement typeParameterElement : executableElement_.getTypeParameters()) {
//                flb.item("param " + "<" + typeParameterElement.getSimpleName() + ">", ib ->
//                        new CommentBuilder(
//                                tag,
//                                executableMemberDoc_.getEnclosingElement(),
//                                configuration_).buildBody().forEach(ib::addChild));
//            }
//            for (ParamTree tag : executableMemberDoc_.typeParamTags()) {
//                flb.item("param " + "<" + tag.parameterName() + ">", ib ->
//                        new CommentBuilder(
//                                tag,
//                                executableMemberDoc_.containingClass(),
//                                configuration_).buildBody().forEach(ib::addChild));
//            }
            for (ThrowsTree tag : throwsTags) {
                flb.item("throws " + tag.getExceptionName(), ib ->
                        new CommentBuilder(
                                tag,
                                executableElement_,
                                docContext_).buildBody().forEach(ib::addChild));
            }
            for (ReturnTree tag : returnTags) {
                flb.item("return", ib ->
                        new CommentBuilder(
                                tag,
                                executableElement_,
                                docContext_).buildBody().forEach(ib::addChild));
            }
        }).ifTrue(tags.size() > 0, shiftBuilder -> {
            new TagUtils(docContext_).appendTags(bodyBuilder, executableElement_, Arrays.asList(TagUtils.TagName.Author, TagUtils.TagName.See));
        });
    }

    private String formatParameterName(ParamTree tag) {
        return tag.isTypeParameter()
                ? "<" + tag.getName() + ">"
                : tag.getName().toString();
    }

    public JavaExecutableBuilderAbst<PARENT, SELF> fillImports(Map<String, TypeElement> imports) {
        return (JavaExecutableBuilderAbst<PARENT, SELF>) super.fillImports(imports);
    }

    protected String formatThrows(ExecutableElement execMemberDoc) {
        List<? extends DocTree> tags = utils_.getBlockTags(executableElement_);
        ThrowsTree[] throwsTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.THROWS)
                .map(ThrowsTree.class::cast).toArray(ThrowsTree[]::new);

        if (throwsTags.length > 0) {
            return "throws " + Arrays.stream(throwsTags)
                    .map(tag -> tag.getExceptionName().getSignature())
                    .collect(Collectors.joining(", "));
        } else {
            return "";
        }
    }

    protected String formatNameWithParameters(ExecutableElement member, boolean includeAnnotations) {
        StringBuilder htmltree = new StringBuilder();
        htmltree.append(utils_.getSimpleName(member));
        htmltree.append("(");
        String sep = "";
        List<? extends VariableElement> params = member.getParameters();
        TypeMirror rcvrType = member.getReceiverType();
        if (includeAnnotations && rcvrType != null) {
            List<? extends AnnotationMirror> descList = rcvrType.getAnnotationMirrors();
            if (descList.size() > 0) {
                docContext_.getHtmlDocletWriter().addReceiverAnnotations(member, rcvrType, descList, htmltree);
                sep = ", ";
            }
        }
        int paramstart;
        for (paramstart = 0; paramstart < params.size(); paramstart++) {
            htmltree.append(sep);
            VariableElement param = params.get(paramstart);
            if (!param.getSimpleName().toString().startsWith("this$")) {
                if (includeAnnotations) {
                    boolean foundAnnotations = docContext_.getHtmlDocletWriter().addAnnotationInfo(0, member, param, htmltree);
                    if (foundAnnotations) {
                        htmltree.append(" ");
                    }
                }
                htmltree.append(formatExecutableMemberParam(param, utils_, (paramstart == params.size() - 1) && member.isVarArgs()));
                break;
            }
        }

        for (int i = paramstart + 1; i < params.size(); i++) {
            htmltree.append(", ");
            if (includeAnnotations) {
                boolean foundAnnotations = docContext_.getHtmlDocletWriter().addAnnotationInfo(0, member, params.get(i), htmltree);
                if (foundAnnotations) {
                    htmltree.append(" ");
                }
            }
            htmltree.append(formatExecutableMemberParam(params.get(i), utils_, (i == params.size() - 1) && member.isVarArgs()));
        }
        htmltree.append(")");
        String result = HtmlUtils.unescapeLtRtAmpBSlash(htmltree.toString());
        result = HtmlUtils.removeATags(result);
        result = collapseNamespaces(result);
        return result;
    }

    protected String formatExecutableMemberParam(VariableElement param, Utils utils, boolean isVarArg) {
        TypeMirror typeMirror = param.asType();
        String result;
        if (utils.isTypeVariable(typeMirror)) {
            result = utils.getTypeSignature(param.asType(), false, true);
        } else if (utils.isDeclaredType(typeMirror)) {
            result = utils.getTypeSignature(param.asType(), true, false);
        } else {
            result = typeMirror.toString();
        }
        if (isVarArg) {
            result = result.replaceAll("\\[]", "...");
        }
        String name = param.getSimpleName().toString();
        return reformatCommas(result) + " " + name;
    }
}
