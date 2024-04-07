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
package com.devives.rstdoclet.rst.builder;

import com.devives.rst.builder.BlockQuoteBuilder;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.RstConfiguration;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.HtmlUtils;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.html2rst.jdkloans.ContentBuilder;
import com.devives.rstdoclet.html2rst.jdkloans.HtmlLinkInfo;
import com.sun.source.doctree.*;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JavaExecutableBuilderAbst<PARENT extends RstNodeBuilder<?, ?, ?, ?>, SELF extends JavaExecutableBuilderAbst<PARENT, SELF>>
        extends JavaMemberBuilderAbst<PARENT, SELF> {
    private final ExecutableElement executableElement_;

    public JavaExecutableBuilderAbst(Directive.Type type, ExecutableElement memberDoc, RstConfiguration configuration) {
        super(type, memberDoc, configuration);
        this.executableElement_ = memberDoc;
    }

    @Override
    protected void fillElements(BlockQuoteBuilder<?> bodyBuilder) {
        super.fillElements(bodyBuilder);
        DocCommentTree docCommentTree = configuration_.utils.getDocCommentTree(executableElement_);
        List<? extends DocTree> tags = configuration_.utils.getBlockTags(docCommentTree);
        //ParamTree[] paramTags = docCommentTree.paramTags();
        ParamTree[] paramTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.PARAM).toArray(ParamTree[]::new);
        //ThrowsTree[] throwsTags = executableMemberDoc_.throwsTags();
        ThrowsTree[] throwsTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.THROWS).toArray(ThrowsTree[]::new);
        //DocTree[] returnTags = executableMemberDoc_.tags("@return");
        ReturnTree[] returnTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.RETURN).toArray(ReturnTree[]::new);

        bodyBuilder.fieldList(flb -> {
            for (ParamTree tag : paramTags) {
                flb.item("param " + formatParameterName(tag), ib ->
                        new CommentBuilder(
                                tag,
                                executableElement_.getEnclosingElement(),
                                configuration_).buildBody().forEach(ib::addChild));
            }

            for (TypeParameterElement typeParameterElement : executableElement_.getTypeParameters()) {
//                flb.item("param " + "<" + typeParameterElement.getSimpleName() + ">", ib ->
//                        new CommentBuilder(
//                                tag,
//                                executableMemberDoc_.getEnclosingElement(),
//                                configuration_).buildBody().forEach(ib::addChild));
            }
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
                                executableElement_.getEnclosingElement(),
                                configuration_).buildBody().forEach(ib::addChild));
            }
            for (ReturnTree tag : returnTags) {
                flb.item("return", ib ->
                        new CommentBuilder(
                                tag,
                                executableElement_.getEnclosingElement(),
                                configuration_).buildBody().forEach(ib::addChild));
            }
        }).ifTrue(tags.size() > 0, shiftBuilder -> {
            new TagUtils(configuration_, docContext_).appendTags(bodyBuilder, executableElement_, Arrays.asList(TagUtils.TagName.Author, TagUtils.TagName.See));
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
        DocCommentTree docCommentTree = configuration_.utils.getDocCommentTree(executableElement_);
        List<? extends DocTree> tags = configuration_.utils.getBlockTags(docCommentTree);
        ThrowsTree[] throwsTags = tags.stream().filter(tag -> tag.getKind() == DocTree.Kind.THROWS).toArray(ThrowsTree[]::new);

        if (throwsTags.length > 0) {
            return "throws " + Arrays.stream(throwsTags)
                    .map(tag -> tag.getExceptionName().getSignature())
                    .collect(Collectors.joining(", "));
        } else {
            return "";
        }
    }

    protected String formatNameWithParameters(ExecutableElement member, boolean includeAnnotations) {
        Content htmltree = new ContentBuilder();
        htmltree.add(configuration_.utils.getSimpleName(member));
        htmltree.add("(");
        String sep = "";
        List<? extends VariableElement> params = member.getParameters();
        TypeMirror rcvrType = member.getReceiverType();
        if (includeAnnotations && rcvrType != null) {
            List<? extends AnnotationMirror> descList = rcvrType.getAnnotationMirrors();
            if (descList.size() > 0) {
                addReceiverAnnotations(member, rcvrType, descList, htmltree);
                sep = ", ";
            }
        }
        int paramstart;
        for (paramstart = 0; paramstart < params.size(); paramstart++) {
            htmltree.add(sep);
            VariableElement param = params.get(paramstart);
            if (!param.getSimpleName().toString().startsWith("this$")) {
                if (includeAnnotations) {
                    boolean foundAnnotations = docContext_.addAnnotationInfo(0, member, param, htmltree);
                    if (foundAnnotations) {
                        htmltree.add(" ");
                    }
                }
                htmltree.add(formatExecutableMemberParam(param, configuration_.utils, (paramstart == params.size() - 1) && member.isVarArgs()));
                break;
            }
        }

        for (int i = paramstart + 1; i < params.size(); i++) {
            htmltree.add(", ");
            if (includeAnnotations) {
                boolean foundAnnotations = docContext_.addAnnotationInfo(0, member, params.get(i), htmltree);
                if (foundAnnotations) {
                    htmltree.add(" ");
                }
            }
            htmltree.add(formatExecutableMemberParam(params.get(i), configuration_.utils, (i == params.size() - 1) && member.isVarArgs()));
        }
        htmltree.add(")");
        String result = HtmlUtils.unescapeLtRtAmpBSlash(htmltree.toString());
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
    protected void addReceiverAnnotations(ExecutableElement member, TypeMirror rcvrType,
                                          List<? extends AnnotationMirror> descList, Content tree) {
        docContext_.addReceiverAnnotationInfo(member, descList, tree);
        tree.add(docContext_.getSpace());
        //todo
        //tree.add(rcvrType.typeName());
        HtmlLinkInfo linkInfo = new HtmlLinkInfo(configuration_,
                HtmlLinkInfo.Kind.CLASS_SIGNATURE, rcvrType);
        tree.add(docContext_.getTypeParameterLinks(linkInfo));
        tree.add(docContext_.getSpace());
        tree.add("this");
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
