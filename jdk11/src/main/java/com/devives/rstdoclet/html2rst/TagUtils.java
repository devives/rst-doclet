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
package com.devives.rstdoclet.html2rst;

import com.devives.rst.builder.BodyBuilders;
import com.devives.rst.builder.RstElementBuilder;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.RstConfiguration;
import com.devives.rstdoclet.html2rst.jdkloans.HtmlDocletWriter;
import com.devives.sphinx.rst.document.directive.Directives;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.SeeTree;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.Element;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TagUtils {

    private final Utils utils_;
    private final HtmlDocletWriter docContext_;

    public TagUtils(HtmlDocletWriter docContext) {
        docContext_ = Objects.requireNonNull(docContext);
        utils_ = docContext.configuration.utils;
    }

    public enum TagName {
        Since,
        Version,
        Deprecated,
        Author,
        See
    }

    /**
     * @see <a href="https://www.tutorialspoint.com/java/java_documentation.htm">The javadoc Tags</a>
     */
    public RstElementBuilder<?, ?, ?> appendTags(BodyBuilders<?, ?, ?, ?> builder, Element doc, Collection<TagName> tagNames) {
        // Tag[] valueTags = doc.tags("@value");
        // Tag[] serialTags = doc.tags("@serial");
        tagNames.forEach(tagName -> {
            switch (tagName) {
                case Author:
                    appendAuthorTags(builder, doc);
                    break;
                case Since:
                    appendSinceTags(builder, doc);
                    break;
                case Version:
                    appendVersionTags(builder, doc);
                    break;
                case Deprecated:
                    appendDeprecatedTags(builder, doc);
                    break;
                case See:
                    appendSeeTags(builder, doc);
                    break;
            }
        });
        return builder;
    }

    public RstElementBuilder<?, ?, ?> appendAuthorTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.AUTHOR);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.SectionAuthor, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendSinceTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.SINCE);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.VersionAdded, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendVersionTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.VERSION);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.VersionChanged, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendDeprecatedTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.DEPRECATED);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.Deprecated, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendSeeTags(BodyBuilders<?, ?, ?, ?> builder, Element doc) {
        List<? extends DocTree> tags = utils_.getBlockTags(doc, DocTree.Kind.SEE);
        return builder.ifTrue(tags.size() > 0, () -> {
            builder.directive(Directives.SeeAlso, seeAlsoBuilder -> {
                seeAlsoBuilder.lineBlock(lineBlockBuilder -> {
                    for (DocTree tag : tags) {
                        lineBlockBuilder.item(ib -> ib.addChild(seeTagToJavaRef((SeeTree) tag, doc)));
                    }
                });
            });
        });
    }

    public InlineElement seeTagToJavaRef(SeeTree see, Element doc) {
        return docContext_.seeTagToContent(doc, see);

        //        return new Text(content.toString());
//
//        DocTree.Kind kind = see.getKind();
//        CommentHelper ch = utils_.getCommentHelper(doc);
//        String tagName = ch.getTagName(see);
//
//        String seeText = utils_.normalizeNewlines(ch.getText(see)).toString();
//        List<? extends DocTree> label;
//        List<? extends DocTree> ref = see.getReference();
//        assert !ref.isEmpty();
//        switch (ref.get(0).getKind()) {
//            case TEXT -> {
//                // @see "Reference"
//                //return Text.of(seeText);
//                return Rst4Sphinx.elements().text("\\ " + seeText + "\\ ");
//            }
//            case START_ELEMENT -> {
//                // @see <a href="...">...</a>
//                return hrefToLink(seeText);
//                //return new RawHtml(replaceDocRootDir(removeTrailingSlash(seeText)));
//            }
//            case REFERENCE -> {
//                // @see reference label...
//                label = ref.subList(1, ref.size());
//            }
//            default ->
//                    throw new IllegalStateException(ref.get(0).getKind().toString());
//        }
//
//        boolean isLinkPlain = kind == LINK_PLAIN;
//        Content labelContent = plainOrCode(isLinkPlain,
//                commentTagsToContent(see, element, label, context));
//
//        // The signature from the @see tag. We will output this text when a label is not specified.
//        Content text = plainOrCode(isLinkPlain,
//                Text.of(Objects.requireNonNullElse(ch.getReferencedSignature(see), "")));
//
//        TypeElement refClass = ch.getReferencedClass(see);
//        Element refMem =       ch.getReferencedMember(see);
//        String refMemName =    ch.getReferencedMemberName(see);
//
//        if (refMemName == null && refMem != null) {
//            refMemName = refMem.toString();
//        }
//        if (refClass == null) {
//            ModuleElement refModule = ch.getReferencedModule(see);
//            if (refModule != null && utils.isIncluded(refModule)) {
//                return getModuleLink(refModule, labelContent.isEmpty() ? text : labelContent);
//            }
//            //@see is not referencing an included class
//            PackageElement refPackage = ch.getReferencedPackage(see);
//            if (refPackage != null && utils.isIncluded(refPackage)) {
//                //@see is referencing an included package
//                if (labelContent.isEmpty())
//                    labelContent = plainOrCode(isLinkPlain,
//                            Text.of(refPackage.getQualifiedName()));
//                return getPackageLink(refPackage, labelContent);
//            } else {
//                // @see is not referencing an included class, module or package. Check for cross links.
//                String refModuleName =  ch.getReferencedModuleName(see);
//                DocLink elementCrossLink = (refPackage != null) ? getCrossPackageLink(refPackage) :
//                        (configuration.extern.isModule(refModuleName))
//                                ? getCrossModuleLink(utils.elementUtils.getModuleElement(refModuleName))
//                                : null;
//                if (elementCrossLink != null) {
//                    // Element cross link found
//                    return links.createExternalLink(elementCrossLink,
//                            (labelContent.isEmpty() ? text : labelContent));
//                } else {
//                    // No cross link found so print warning
//                    messages.warning(ch.getDocTreePath(see),
//                            "doclet.see.class_or_package_not_found",
//                            "@" + tagName,
//                            seeText);
//                    return (labelContent.isEmpty() ? text: labelContent);
//                }
//            }
//        } else if (refMemName == null) {
//            // Must be a class reference since refClass is not null and refMemName is null.
//            if (labelContent.isEmpty()) {
//                TypeMirror referencedType = ch.getReferencedType(see);
//                if (utils.isGenericType(referencedType)) {
//                    // This is a generic type link, use the TypeMirror representation.
//                    return plainOrCode(isLinkPlain, getLink(
//                            new HtmlLinkInfo(configuration, HtmlLinkInfo.Kind.DEFAULT, referencedType)));
//                }
//                labelContent = plainOrCode(isLinkPlain, Text.of(utils.getSimpleName(refClass)));
//            }
//            return getLink(new HtmlLinkInfo(configuration, HtmlLinkInfo.Kind.DEFAULT, refClass)
//                    .label(labelContent));
//        } else if (refMem == null) {
//            // Must be a member reference since refClass is not null and refMemName is not null.
//            // However, refMem is null, so this referenced member does not exist.
//            return (labelContent.isEmpty() ? text: labelContent);
//        } else {
//            // Must be a member reference since refClass is not null and refMemName is not null.
//            // refMem is not null, so this @see tag must be referencing a valid member.
//            TypeElement containing = utils.getEnclosingTypeElement(refMem);
//
//            // Find the enclosing type where the method is actually visible
//            // in the inheritance hierarchy.
//            ExecutableElement overriddenMethod = null;
//            if (refMem.getKind() == ElementKind.METHOD) {
//                VisibleMemberTable vmt = configuration.getVisibleMemberTable(containing);
//                overriddenMethod = vmt.getOverriddenMethod((ExecutableElement)refMem);
//
//                if (overriddenMethod != null)
//                    containing = utils.getEnclosingTypeElement(overriddenMethod);
//            }
//            if (ch.getText(see).trim().startsWith("#") &&
//                    ! (utils.isPublic(containing) || utils.isLinkable(containing))) {
//                // Since the link is relative and the holder is not even being
//                // documented, this must be an inherited link.  Redirect it.
//                // The current class either overrides the referenced member or
//                // inherits it automatically.
//                if (this instanceof ClassWriterImpl writer) {
//                    containing = writer.getTypeElement();
//                } else if (!utils.isPublic(containing)) {
//                    messages.warning(
//                            ch.getDocTreePath(see), "doclet.see.class_or_package_not_accessible",
//                            tagName, utils.getFullyQualifiedName(containing));
//                } else {
//                    messages.warning(
//                            ch.getDocTreePath(see), "doclet.see.class_or_package_not_found",
//                            tagName, seeText);
//                }
//            }
//            if (configuration.currentTypeElement != containing) {
//                refMemName = (utils.isConstructor(refMem))
//                        ? refMemName
//                        : utils.getSimpleName(containing) + "." + refMemName;
//            }
//            if (utils.isExecutableElement(refMem)) {
//                if (refMemName.indexOf('(') < 0) {
//                    refMemName += utils.makeSignature((ExecutableElement) refMem, null, true);
//                }
//                if (overriddenMethod != null) {
//                    // The method to actually link.
//                    refMem = overriddenMethod;
//                }
//            }
//
//            return getDocLink(HtmlLinkInfo.Kind.SEE_TAG, containing,
//                    refMem, (labelContent.isEmpty()
//                            ? plainOrCode(isLinkPlain, Text.of(refMemName))
//                            : labelContent), null, false);
//        }
//
        //
//
//        //String text = new CommentBuilder(see, doc, configuration_).build().serialize().trim();
//        String text = print(see.getReference());
//        if (text.contains("<a")) {
//            return hrefToLink(text);
//        } else if (text.startsWith("\"")) {
//            return Rst4Sphinx.elements().text("\\ " + StringUtils.dequote(text, '\"') + "\\ ");
//        } else if (see.getReference() != null && !see.getReference().isEmpty() && see.getReference().get(0).getKind() == DocTree.Kind.REFERENCE) {
//            //todo JavaMemberRef || JavaTypeRef
//            ReferenceTree referenceTree = (ReferenceTree) see.getReference().get(0);
////            return new JavaMemberRef(referenceTree.getSignature(),utils_);
////            return new JavaTypeRef(utils_.findClass()referenceTree.getSignature());
//            return new Link(referenceTree.getSignature(), referenceTree.getSignature());
//        } else {
//            return Rst4Sphinx.elements().text("\\ " + text + "\\ ");
//        }
    }

    private String print(List<? extends DocTree> reference) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        boolean needSep = true;
        for (DocTree t : reference) {
            if (needSep) sb.append(" ");
            needSep = (first && (t instanceof ReferenceTree));
            first = false;
            sb.append(t);
        }
        return sb.toString();
    }

}
