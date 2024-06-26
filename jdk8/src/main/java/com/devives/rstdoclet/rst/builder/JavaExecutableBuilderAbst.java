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
import com.sun.javadoc.*;
import com.sun.tools.doclets.formats.html.LinkInfoImpl;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.internal.toolkit.Content;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JavaExecutableBuilderAbst<PARENT extends RstNodeBuilder<?, ?, ?, ?>, SELF extends JavaExecutableBuilderAbst<PARENT, SELF>>
        extends JavaMemberBuilderAbst<PARENT, SELF> {
    private final ExecutableMemberDoc executableMemberDoc_;

    public JavaExecutableBuilderAbst(Directive.Type type, ExecutableMemberDoc memberDoc, RstGeneratorContext docContext) {
        super(type, memberDoc, docContext);
        this.executableMemberDoc_ = memberDoc;
    }

    @Override
    protected void fillElements(BlockQuoteBuilder<?> bodyBuilder) {
        super.fillElements(bodyBuilder);
        ParamTag[] paramTags = executableMemberDoc_.paramTags();
        ThrowsTag[] throwsTags = executableMemberDoc_.throwsTags();
        Tag[] returnTags = executableMemberDoc_.tags("@return");

        bodyBuilder.fieldList(flb -> {
            for (ParamTag tag : paramTags) {
                flb.item("param " + tag.parameterName(), ib ->
                        new CommentBuilder(
                                docContext_,
                                executableMemberDoc_,
                                tag
                        ).buildBody().forEach(ib::addChild));
            }

            for (ParamTag tag : executableMemberDoc_.typeParamTags()) {
                flb.item("param " + "<" + tag.parameterName() + ">", ib ->
                        new CommentBuilder(
                                docContext_,
                                executableMemberDoc_,
                                tag
                        ).buildBody().forEach(ib::addChild));
            }
            for (ThrowsTag tag : throwsTags) {
                flb.item("throws " + tag.exceptionName(), ib ->
                        new CommentBuilder(
                                docContext_,
                                executableMemberDoc_,
                                tag).buildBody().forEach(ib::addChild));
            }
            for (Tag tag : returnTags) {
                flb.item("return", ib ->
                        new CommentBuilder(
                                docContext_,
                                executableMemberDoc_,
                                tag).buildBody().forEach(ib::addChild));
            }
        }).ifTrue(executableMemberDoc_.tags().length > 0, shiftBuilder -> {
            new TagUtils(docContext_).appendTags(bodyBuilder, executableMemberDoc_, Arrays.asList(TagUtils.TagName.Author, TagUtils.TagName.See));
        });
    }

    public JavaExecutableBuilderAbst<PARENT, SELF> fillImports(Map<String, ClassDoc> imports) {
        return (JavaExecutableBuilderAbst<PARENT, SELF>) super.fillImports(imports);
    }

    protected static String formatThrows(ExecutableMemberDoc execMemberDoc) {
        if (execMemberDoc.thrownExceptions().length > 0) {
            return "throws " + Arrays.stream(execMemberDoc.thrownExceptions())
                    .map(ClassDoc::name)
                    .collect(Collectors.joining(", "));
        } else {
            return "";
        }
    }

    protected String formatNameWithParameters(ExecutableMemberDoc member, boolean includeAnnotations) {
        Content htmltree = new ContentBuilder();
        htmltree.addContent(member.name());
        htmltree.addContent("(");
        String sep = "";
        Parameter[] params = member.parameters();
        Type rcvrType = member.receiverType();
        if (includeAnnotations && rcvrType instanceof AnnotatedType) {
            AnnotationDesc[] descList = rcvrType.asAnnotatedType().annotations();
            if (descList.length > 0) {
                addReceiverAnnotations(member, rcvrType, descList, htmltree);
                sep = ", ";
            }
        }
        int paramstart;
        for (paramstart = 0; paramstart < params.length; paramstart++) {
            htmltree.addContent(sep);
            Parameter param = params[paramstart];
            if (!param.name().startsWith("this$")) {
                if (includeAnnotations) {
                    boolean foundAnnotations = docContext_.getHtmlDocletWriter().addAnnotationInfo(0, member, param, htmltree);
                    if (foundAnnotations) {
                        htmltree.addContent(" ");
                    }
                }
                htmltree.addContent(formatExecutableMemberParam(param, (paramstart == params.length - 1) && member.isVarArgs()));
                break;
            }
        }

        for (int i = paramstart + 1; i < params.length; i++) {
            htmltree.addContent(", ");
            if (includeAnnotations) {
                boolean foundAnnotations = docContext_.getHtmlDocletWriter().addAnnotationInfo(0, member, params[i], htmltree);
                if (foundAnnotations) {
                    htmltree.addContent(" ");
                }
            }
            htmltree.addContent(formatExecutableMemberParam(params[i], (i == params.length - 1) && member.isVarArgs()));
        }
        htmltree.addContent(")");
        String result = HtmlUtils.unescapeLtRtAmpBSlash(htmltree.toString());
        result = HtmlUtils.removeATags(result);
        result = collapseNamespaces(result);
        return result;
    }

    /**
     * Add the receiver annotations information.
     *
     * @param member   the member to write receiver annotations for.
     * @param rcvrType the receiver type.
     * @param descList list of annotation description.
     * @param tree     the content tree to which the information will be added.
     */
    protected void addReceiverAnnotations(ExecutableMemberDoc member, Type rcvrType,
                                          AnnotationDesc[] descList, Content tree) {
        docContext_.getHtmlDocletWriter().addReceiverAnnotationInfo(member, descList, tree);
        tree.addContent(docContext_.getHtmlDocletWriter().getSpace());
        tree.addContent(rcvrType.typeName());
        LinkInfoImpl linkInfo = new LinkInfoImpl(docContext_.getHtmlConfiguration(),
                LinkInfoImpl.Kind.CLASS_SIGNATURE, rcvrType);
        tree.addContent(docContext_.getHtmlDocletWriter().getTypeParameterLinks(linkInfo));
        tree.addContent(docContext_.getHtmlDocletWriter().getSpace());
        tree.addContent("this");
    }


    protected String formatExecutableMemberParam(Parameter param, boolean isVarArg) {
        String result = param.toString();
        if (isVarArg) {
            result = result.replaceAll("\\[]", "...");
        }
        return result;
    }
}
