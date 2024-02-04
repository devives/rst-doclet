/*
 * Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.devives.rstdoclet.html2rst.jdkloans;

import com.devives.rst.Rst;
import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.ConfigurationImpl;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.sun.javadoc.*;
import com.sun.tools.doclets.formats.html.markup.HtmlAttr;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import com.sun.tools.doclets.internal.toolkit.util.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for the Html Format Code Generation specific to JavaDoc.
 * This Class contains methods related to the Html Code Generation which
 * are used extensively while generating the entire documentation.
 *
 * <p><b>This is NOT part of any supported API.
 * If you write code that depends on this, you do so at your own risk.
 * This code and its internal interfaces are subject to change or
 * deletion without notice.</b>
 *
 * @author Atul M Dambalkar
 * @author Robert Field
 * @author Bhavesh Patel (Modified)
 * @since 1.2
 */
public class DocContext {

    /**
     * Relative path from the file getting generated to the destination
     * directory. For example, if the file getting generated is
     * "java/lang/Object.html", then the path to the root is "../..".
     * This string can be empty if the file getting generated is in
     * the destination directory.
     */
    public final DocPath pathToRoot;
    public final Doc parentDoc;

    /**
     * The global configuration information for this run.
     */
    public final ConfigurationImpl configuration;

    /**
     * To check whether the repeated annotations is documented or not.
     */
    private boolean isAnnotationDocumented = false;

    /**
     * To check whether the container annotations is documented or not.
     */
    private boolean isContainerDocumented = false;

    /**
     * Constructor to construct the HtmlStandardWriter object.
     *
     * @param parentDoc     .
     * @param configuration .
     */
    public DocContext(Doc parentDoc, ConfigurationImpl configuration) {
        this.parentDoc = parentDoc;
        this.configuration = configuration;
        this.pathToRoot = DocPath.empty;
    }

    /**
     * Return "&#38;nbsp;", non-breaking space.
     */
    public Content getSpace() {
        return RawHtml.nbsp;
    }

    /**
     * Convert the name to a valid HTML name.
     *
     * @param name the name that needs to be converted to valid HTML name.
     * @return a valid HTML name string.
     */
    public String getName(String name) {
        StringBuilder sb = new StringBuilder();
        char ch;
        /* The HTML 4 spec at http://www.w3.org/TR/html4/types.html#h-6.2 mentions
         * that the name/id should begin with a letter followed by other valid characters.
         * The HTML 5 spec (draft) is more permissive on names/ids where the only restriction
         * is that it should be at least one character long and should not contain spaces.
         * The spec draft is @ http://www.w3.org/html/wg/drafts/html/master/dom.html#the-id-attribute.
         *
         * For HTML 4, we need to check for non-characters at the beginning of the name and
         * substitute it accordingly, "_" and "$" can appear at the beginning of a member name.
         * The method substitutes "$" with "Z:Z:D" and will prefix "_" with "Z:Z".
         */
        for (int i = 0; i < name.length(); i++) {
            ch = name.charAt(i);
            switch (ch) {
                case '(':
                case ')':
                case '<':
                case '>':
                case ',':
                    sb.append('-');
                    break;
                case ' ':
                case '[':
                    break;
                case ']':
                    sb.append(":A");
                    break;
                // Any appearance of $ needs to be substituted with ":D" and not with hyphen
                // since a field name "P$$ and a method P(), both valid member names, can end
                // up as "P--". A member name beginning with $ needs to be substituted with
                // "Z:Z:D".
                case '$':
                    if (i == 0)
                        sb.append("Z:Z");
                    sb.append(":D");
                    break;
                // A member name beginning with _ needs to be prefixed with "Z:Z" since valid anchor
                // names can only begin with a letter.
                case '_':
                    if (i == 0)
                        sb.append("Z:Z");
                    sb.append(ch);
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }


    public Content getHyperLink(DocLink link, Content label) {
        return getHyperLink(link, label, "", "");
    }

    public Content getHyperLink(DocLink link,
                                Content label, boolean strong,
                                String stylename, String title, String target) {
        Content body = label;
        if (strong) {
            body = HtmlTree.SPAN(HtmlStyle.typeNameLink, body);
        }
        if (stylename != null && stylename.length() != 0) {
            HtmlTree t = new HtmlTree(HtmlTag.FONT, body);
            t.addAttr(HtmlAttr.CLASS, stylename);
            body = t;
        }
        HtmlTree l = HtmlTree.A(link.toString(), body);
        if (title != null && title.length() != 0) {
            l.addAttr(HtmlAttr.TITLE, title);
        }
        if (target != null && target.length() != 0) {
            l.addAttr(HtmlAttr.TARGET, target);
        }
        return l;
    }

    public Content getHyperLink(DocLink link,
                                Content label, String title, String target) {
        HtmlTree anchor = HtmlTree.A(link.toString(), label);
        if (title != null && title.length() != 0) {
            anchor.addAttr(HtmlAttr.TITLE, title);
        }
        if (target != null && target.length() != 0) {
            anchor.addAttr(HtmlAttr.TARGET, target);
        }
        return anchor;
    }

    /**
     * Replace {&#064;docRoot} tag used in options that accept HTML text, such
     * as -header, -footer, -top and -bottom, and when converting a relative
     * HREF where commentTagsToString inserts a {&#064;docRoot} where one was
     * missing.  (Also see DocRootTaglet for {&#064;docRoot} tags in doc
     * comments.)
     * <p>
     * Replace {&#064;docRoot} tag in htmlstr with the relative path to the
     * destination directory from the directory where the file is being
     * written, looping to handle all such tags in htmlstr.
     * <p>
     * For example, for "-d docs" and -header containing {&#064;docRoot}, when
     * the HTML page for source file p/C1.java is being generated, the
     * {&#064;docRoot} tag would be inserted into the header as "../",
     * the relative path from docs/p/ to docs/ (the document root).
     * <p>
     * Note: This doc comment was written with '&amp;#064;' representing '@'
     * to prevent the inline tag from being interpreted.
     */
    public String replaceDocRootDir(String htmlstr) {
        // Return if no inline tags exist
        int index = htmlstr.indexOf("{@");
        if (index < 0) {
            return htmlstr;
        }
        Matcher docrootMatcher = docrootPattern.matcher(htmlstr);
        if (!docrootMatcher.find()) {
            return htmlstr;
        }
        StringBuilder buf = new StringBuilder();
        int prevEnd = 0;
        do {
            int match = docrootMatcher.start();
            // append htmlstr up to start of next {@docroot}
            buf.append(htmlstr.substring(prevEnd, match));
            prevEnd = docrootMatcher.end();
            if (configuration.docrootparent.length() > 0 && htmlstr.startsWith("/..", prevEnd)) {
                // Insert the absolute link if {@docRoot} is followed by "/..".
                buf.append(configuration.docrootparent);
                prevEnd += 3;
            } else {
                // Insert relative path where {@docRoot} was located
                buf.append(pathToRoot.isEmpty() ? "." : pathToRoot.getPath());
            }
            // Append slash if next character is not a slash
            if (prevEnd < htmlstr.length() && htmlstr.charAt(prevEnd) != '/') {
                buf.append('/');
            }
        } while (docrootMatcher.find());
        buf.append(htmlstr.substring(prevEnd));
        return buf.toString();
    }

    //where:
    // Note: {@docRoot} is not case sensitive when passed in w/command line option:
    private static final Pattern docrootPattern =
            Pattern.compile(Pattern.quote("{@docroot}"), Pattern.CASE_INSENSITIVE);

    /**
     * Returns a TagletWriter that knows how to write HTML.
     *
     * @return a TagletWriter that knows how to write HTML.
     */
    public TagletWriter getTagletWriterInstance(boolean isFirstSentence) {
        return new TagletWriterImpl(this, isFirstSentence);
    }

    /**
     * Return the type parameters for the given class.
     *
     * @param linkInfo the information about the link.
     * @return the type for the given class.
     */
    public Content getTypeParameterLinks(LinkInfoImpl linkInfo) {
        LinkFactoryImpl factory = new LinkFactoryImpl(this);
        return factory.getTypeParameterLinks(linkInfo, false);
    }


    /**
     * Return the link to the given class.
     *
     * @param linkInfo the information about the link.
     * @return the link for the given class.
     */
    public Content getLink(LinkInfoImpl linkInfo) {
        LinkFactoryImpl factory = new LinkFactoryImpl(this);
        return factory.getLink(linkInfo);
    }

    /*************************************************************
     * Return a class cross link to external class documentation.
     * The name must be fully qualified to determine which package
     * the class is in.  The -link option does not allow users to
     * link to external classes in the "default" package.
     *
     * @param qualifiedClassName the qualified name of the external class.
     * @param refMemName the name of the member being referenced.  This should
     * be null or empty string if no member is being referenced.
     * @param label the label for the external link.
     * @param strong true if the link should be strong.
     * @param style the style of the link.
     * @param code true if the label should be code font.
     */
    public Content getCrossClassLink(String qualifiedClassName, String refMemName,
                                     Content label, boolean strong, String style,
                                     boolean code) {
        String className = "";
        String packageName = qualifiedClassName == null ? "" : qualifiedClassName;
        int periodIndex;
        while ((periodIndex = packageName.lastIndexOf('.')) != -1) {
            className = packageName.substring(periodIndex + 1, packageName.length()) +
                    (className.length() > 0 ? "." + className : "");
            Content defaultLabel = new StringContent(className);
            if (code)
                defaultLabel = HtmlTree.CODE(defaultLabel);
            packageName = packageName.substring(0, periodIndex);
            if (getCrossPackageLink(packageName) != null) {
                //The package exists in external documentation, so link to the external
                //class (assuming that it exists).  This is definitely a limitation of
                //the -link option.  There are ways to determine if an external package
                //exists, but no way to determine if the external class exists.  We just
                //have to assume that it does.
                DocLink link = configuration.extern.getExternalLink(packageName, pathToRoot,
                        className + ".html", refMemName);
                return getHyperLink(link,
                        (label == null) || label.isEmpty() ? defaultLabel : label,
                        strong, style,
                        configuration.getText("doclet.Href_Class_Or_Interface_Title", packageName),
                        "");
            }
        }
        return null;
    }

    public DocLink getCrossPackageLink(String pkgName) {
        return configuration.extern.getExternalLink(pkgName, pathToRoot,
                DocPaths.PACKAGE_SUMMARY.getPath());
    }

    /**
     * Return the link for the given member.
     *
     * @param context the id of the context where the link will be printed.
     * @param doc     the member being linked to.
     * @param label   the label for the link.
     * @param strong  true if the link should be strong.
     * @return the link for the given member.
     */
    public Content getDocLink(LinkInfoImpl.Kind context, MemberDoc doc, String label,
                              boolean strong) {
        return getDocLink(context, doc.containingClass(), doc, label, strong);
    }

    /**
     * Return the link for the given member.
     *
     * @param context  the id of the context where the link will be printed.
     * @param classDoc the classDoc that we should link to.  This is not
     *                 necessarily equal to doc.containingClass().  We may be
     *                 inheriting comments.
     * @param doc      the member being linked to.
     * @param label    the label for the link.
     * @param strong   true if the link should be strong.
     * @return the link for the given member.
     */
    public Content getDocLink(LinkInfoImpl.Kind context, ClassDoc classDoc, MemberDoc doc,
                              String label, boolean strong) {
        return getDocLink(context, classDoc, doc, label, strong, false);
    }


    /**
     * Return the link for the given member.
     *
     * @param context    the id of the context where the link will be printed.
     * @param classDoc   the classDoc that we should link to.  This is not
     *                   necessarily equal to doc.containingClass().  We may be
     *                   inheriting comments.
     * @param doc        the member being linked to.
     * @param label      the label for the link.
     * @param strong     true if the link should be strong.
     * @param isProperty true if the doc parameter is a JavaFX property.
     * @return the link for the given member.
     */
    public Content getDocLink(LinkInfoImpl.Kind context, ClassDoc classDoc, MemberDoc doc,
                              String label, boolean strong, boolean isProperty) {
        return getDocLink(context, classDoc, doc, new StringContent(check(label)), strong, isProperty);
    }

    String check(String s) {
        if (s.matches(".*[&<>].*")) throw new IllegalArgumentException(s);
        return s;
    }

    public Content getDocLink(LinkInfoImpl.Kind context, ClassDoc classDoc, MemberDoc doc,
                              Content label, boolean strong, boolean isProperty) {
        if (!(doc.isIncluded() ||
                Util.isLinkable(classDoc, configuration))) {
            return label;
        } else if (doc instanceof ExecutableMemberDoc) {
            ExecutableMemberDoc emd = (ExecutableMemberDoc) doc;
            return getLink(new LinkInfoImpl(configuration, context, classDoc)
                    .label(label).where(getName(getAnchor(emd, isProperty))).strong(strong));
        } else if (doc instanceof MemberDoc) {
            return getLink(new LinkInfoImpl(configuration, context, classDoc)
                    .label(label).where(getName(doc.name())).strong(strong));
        } else {
            return label;
        }
    }

    public String getAnchor(ExecutableMemberDoc emd, boolean isProperty) {
        if (isProperty) {
            return emd.name();
        }
        StringBuilder signature = new StringBuilder(emd.signature());
        StringBuilder signatureParsed = new StringBuilder();
        int counter = 0;
        for (int i = 0; i < signature.length(); i++) {
            char c = signature.charAt(i);
            if (c == '<') {
                counter++;
            } else if (c == '>') {
                counter--;
            } else if (counter == 0) {
                signatureParsed.append(c);
            }
        }
        return emd.name() + signatureParsed;
    }

    public Content seeTagToContent(SeeTag see) {
        String tagName = see.name();
        if (!(tagName.startsWith("@link") || tagName.equals("@see"))) {
            return new ContentBuilder();
        }

        String seeText = replaceDocRootDir(StringUtils.normalizeNewlines(see.text()));

        //Check if @see is an href or "string"
        if (seeText.contains("<a")) {
            return new StringContent(TagUtils.hrefToLink(seeText).serialize());
        } else if (seeText.startsWith("\"")) {
            return new StringContent(Rst.elements().inlineLiteral(seeText).serialize());
        }

        return new StringContent(StringUtils.surroundWithHiddenSpace(TagUtils.seeTagToJavaRef(see).serialize()));
//        //Check if @see is an href or "string"
//        if (seetext.startsWith("<") || seetext.startsWith("\"")) {
//            return new RawHtml(seetext);
//        }
//
//        boolean plain = tagName.equalsIgnoreCase("@linkplain");
//        Content label = plainOrCode(plain, new RawHtml(see.label()));
//
//        //The text from the @see tag.  We will output this text when a label is not specified.
//        Content text = plainOrCode(plain, new RawHtml(seetext));
//
//        ClassDoc refClass = see.referencedClass();
//        String refClassName = see.referencedClassName();
//        MemberDoc refMem = see.referencedMember();
//        String refMemName = see.referencedMemberName();
//
//        if (refClass == null) {
//            //@see is not referencing an included class
//            PackageDoc refPackage = see.referencedPackage();
//            if (refPackage != null && refPackage.isIncluded()) {
//                //@see is referencing an included package
//                if (label.isEmpty())
//                    label = plainOrCode(plain, new StringContent(refPackage.name()));
//                return getPackageLink(refPackage, label);
//            } else {
//                //@see is not referencing an included class or package.  Check for cross links.
//                Content classCrossLink;
//                DocLink packageCrossLink = getCrossPackageLink(refClassName);
//                if (packageCrossLink != null) {
//                    //Package cross link found
//                    return getHyperLink(packageCrossLink,
//                            (label.isEmpty() ? text : label));
//                } else if ((classCrossLink = getCrossClassLink(refClassName,
//                        refMemName, label, false, "", !plain)) != null) {
//                    //Class cross link found (possibly to a member in the class)
//                    return classCrossLink;
//                } else {
//                    //No cross link found so print warning
//                    configuration.getDocletSpecificMsg().warning(see.position(), "doclet.see.class_or_package_not_found",
//                            tagName, seetext);
//                    return (label.isEmpty() ? text : label);
//                }
//            }
//        } else if (refMemName == null) {
//            // Must be a class reference since refClass is not null and refMemName is null.
//            if (label.isEmpty()) {
//                label = plainOrCode(plain, new StringContent(refClass.name()));
//            }
//            return getLink(new LinkInfoImpl(configuration, LinkInfoImpl.Kind.DEFAULT, refClass)
//                    .label(label));
//        } else if (refMem == null) {
//            // Must be a member reference since refClass is not null and refMemName is not null.
//            // However, refMem is null, so this referenced member does not exist.
//            return (label.isEmpty() ? text : label);
//        } else {
//            // Must be a member reference since refClass is not null and refMemName is not null.
//            // refMem is not null, so this @see tag must be referencing a valid member.
//            ClassDoc containing = refMem.containingClass();
//            if (see.text().trim().startsWith("#") &&
//                    !(containing.isPublic() ||
//                            Util.isLinkable(containing, configuration))) {
//                // Since the link is relative and the holder is not even being
//                // documented, this must be an inherited link.  Redirect it.
//                // The current class either overrides the referenced member or
//                // inherits it automatically.
//                if (parentDoc instanceof ClassDoc) {
//                    containing = (ClassDoc) parentDoc;
//                } else if (!containing.isPublic()) {
//                    configuration.getDocletSpecificMsg().warning(
//                            see.position(), "doclet.see.class_or_package_not_accessible",
//                            tagName, containing.qualifiedName());
//                } else {
//                    configuration.getDocletSpecificMsg().warning(
//                            see.position(), "doclet.see.class_or_package_not_found",
//                            tagName, seetext);
//                }
//            }
//            if (configuration.currentcd != containing) {
//                refMemName = (refMem instanceof ConstructorDoc) ?
//                        refMemName : containing.name() + "." + refMemName;
//            }
//            if (refMem instanceof ExecutableMemberDoc) {
//                if (refMemName.indexOf('(') < 0) {
//                    refMemName += ((ExecutableMemberDoc) refMem).signature();
//                }
//            }
//
//            text = plainOrCode(plain, new StringContent(refMemName));
//
//            return getDocLink(LinkInfoImpl.Kind.SEE_TAG, containing,
//                    refMem, (label.isEmpty() ? text : label), false);
//        }
    }

    private Content plainOrCode(boolean plain, Content body) {
        return (plain || body.isEmpty()) ? body : HtmlTree.CODE(body);
    }

    /**
     * Converts inline tags and text to text strings, expanding the
     * inline tags along the way.  Called wherever text can contain
     * an inline tag, such as in comments or in free-form text arguments
     * to non-inline tags.
     *
     * @param holderTag       specific tag where comment resides
     * @param doc             specific doc where comment resides
     * @param tags            array of text tags and inline tags (often alternating)
     *                        present in the text of interest for this doc
     * @param isFirstSentence true if text is first sentence
     */
    public Content commentTagsToContent(Tag holderTag, Doc doc, Tag[] tags,
                                        boolean isFirstSentence) {
        Content result = new ContentBuilder();
        boolean textTagChange = false;
        // Array of all possible inline tags for this javadoc run
        configuration.tagletManager.checkTags(doc, tags, true);
        for (int i = 0; i < tags.length; i++) {
            Tag tagelem = tags[i];
            String tagName = tagelem.name();
            if (tagelem instanceof SeeTag) {
                result.addContent(seeTagToContent((SeeTag) tagelem));
            } else if (!tagName.equals("Text")) {
                boolean wasEmpty = result.isEmpty();
                Content output;
                if (configuration.docrootparent.length() > 0
                        && tagelem.name().equals("@docRoot")
                        && ((tags[i + 1]).text()).startsWith("/..")) {
                    // If Xdocrootparent switch ON, set the flag to remove the /.. occurrence after
                    // {@docRoot} tag in the very next Text tag.
                    textTagChange = true;
                    // Replace the occurrence of {@docRoot}/.. with the absolute link.
                    output = new StringContent(configuration.docrootparent);
                } else {
                    output = TagletWriter.getInlineTagOuput(
                            configuration.tagletManager, holderTag,
                            tagelem, getTagletWriterInstance(isFirstSentence));
                }
                if (output != null)
                    result.addContent(output);
                if (wasEmpty && isFirstSentence && tagelem.name().equals("@inheritDoc") && !result.isEmpty()) {
                    break;
                } else {
                    continue;
                }
            } else {
                String text = tagelem.text();
                //If Xdocrootparent switch ON, remove the /.. occurrence after {@docRoot} tag.
                if (textTagChange) {
                    text = text.replaceFirst("/..", "");
                    textTagChange = false;
                }
                //This is just a regular text tag.  The text may contain html links (<a>)
                //or inline tag {@docRoot}, which will be handled as special cases.
                // Относительные ссылки не перемэпируем.
                // text = redirectRelativeLinks(tagelem.holder(), text);

                // Replace @docRoot only if not represented by an instance of DocRootTaglet,
                // that is, only if it was not present in a source file doc comment.
                // This happens when inserted by the doclet (a few lines
                // above in this method).  [It might also happen when passed in on the command
                // line as a text argument to an option (like -header).]
                text = replaceDocRootDir(text);
                if (isFirstSentence) {
                    text = removeNonInlineHtmlTags(text);
                }
                text = Util.replaceTabs(configuration, text);
                text = Util.normalizeNewlines(text);
                result.addContent(new RawHtml(text));
            }
        }
        return result;
    }

    static final Set<String> blockTags = new HashSet<String>();

    static {
        for (HtmlTag t : HtmlTag.values()) {
            if (t.blockType == HtmlTag.BlockType.BLOCK)
                blockTags.add(t.value);
        }
    }

    public static String removeNonInlineHtmlTags(String text) {
        final int len = text.length();

        int startPos = 0;                     // start of text to copy
        int lessThanPos = text.indexOf('<');  // position of latest '<'
        if (lessThanPos < 0) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        main:
        while (lessThanPos != -1) {
            int currPos = lessThanPos + 1;
            if (currPos == len)
                break;
            char ch = text.charAt(currPos);
            if (ch == '/') {
                if (++currPos == len)
                    break;
                ch = text.charAt(currPos);
            }
            int tagPos = currPos;
            while (isHtmlTagLetterOrDigit(ch)) {
                if (++currPos == len)
                    break main;
                ch = text.charAt(currPos);
            }
            if (ch == '>' && blockTags.contains(com.sun.tools.javac.util.StringUtils.toLowerCase(text.substring(tagPos, currPos)))) {
                result.append(text, startPos, lessThanPos);
                startPos = currPos + 1;
            }
            lessThanPos = text.indexOf('<', currPos);
        }
        result.append(text.substring(startPos));

        return result.toString();
    }

    private static boolean isHtmlTagLetterOrDigit(char ch) {
        return ('a' <= ch && ch <= 'z') ||
                ('A' <= ch && ch <= 'Z') ||
                ('1' <= ch && ch <= '6');
    }

    /**
     * Return the string representations of the annotation types for
     * the given doc.
     *
     * @param indent    the number of extra spaces to indent the annotations.
     * @param descList  the array of {@link AnnotationDesc}.
     * @param linkBreak if true, add new line between each member value.
     * @return an array of strings representing the annotations being
     * documented.
     */
    private List<Content> getAnnotations(int indent, AnnotationDesc[] descList, boolean linkBreak) {
        return getAnnotations(indent, descList, linkBreak, true);
    }

    /**
     * Return the string representations of the annotation types for
     * the given doc.
     * <p>
     * A {@code null} {@code elementType} indicates that all the
     * annotations should be returned without any filtering.
     *
     * @param indent                     the number of extra spaces to indent the annotations.
     * @param descList                   the array of {@link AnnotationDesc}.
     * @param linkBreak                  if true, add new line between each member value.
     * @param isJava5DeclarationLocation the type of targeted element (used for filtering
     *                                   type annotations from declaration annotations)
     * @return an array of strings representing the annotations being
     * documented.
     */
    public List<Content> getAnnotations(int indent, AnnotationDesc[] descList, boolean linkBreak,
                                        boolean isJava5DeclarationLocation) {
        List<Content> results = new ArrayList<Content>();
        ContentBuilder annotation;
        for (int i = 0; i < descList.length; i++) {
            AnnotationTypeDoc annotationDoc = descList[i].annotationType();
            // If an annotation is not documented, do not add it to the list. If
            // the annotation is of a repeatable type, and if it is not documented
            // and also if its container annotation is not documented, do not add it
            // to the list. If an annotation of a repeatable type is not documented
            // but its container is documented, it will be added to the list.
            if (!Util.isDocumentedAnnotation(annotationDoc) &&
                    (!isAnnotationDocumented && !isContainerDocumented)) {
                continue;
            }
            /* TODO: check logic here to correctly handle declaration
             * and type annotations.
            if  (Util.isDeclarationAnnotation(annotationDoc, isJava5DeclarationLocation)) {
                continue;
            }*/
            annotation = new ContentBuilder();
            isAnnotationDocumented = false;
            LinkInfoImpl linkInfo = new LinkInfoImpl(configuration,
                    LinkInfoImpl.Kind.ANNOTATION, annotationDoc);
            AnnotationDesc.ElementValuePair[] pairs = descList[i].elementValues();
            // If the annotation is synthesized, do not print the container.
            if (descList[i].isSynthesized()) {
                for (int j = 0; j < pairs.length; j++) {
                    AnnotationValue annotationValue = pairs[j].value();
                    List<AnnotationValue> annotationTypeValues = new ArrayList<AnnotationValue>();
                    if (annotationValue.value() instanceof AnnotationValue[]) {
                        AnnotationValue[] annotationArray =
                                (AnnotationValue[]) annotationValue.value();
                        annotationTypeValues.addAll(Arrays.asList(annotationArray));
                    } else {
                        annotationTypeValues.add(annotationValue);
                    }
                    String sep = "";
                    for (AnnotationValue av : annotationTypeValues) {
                        annotation.addContent(sep);
                        annotation.addContent(annotationValueToContent(av));
                        sep = " ";
                    }
                }
            } else if (isAnnotationArray(pairs)) {
                // If the container has 1 or more value defined and if the
                // repeatable type annotation is not documented, do not print
                // the container.
                if (pairs.length == 1 && isAnnotationDocumented) {
                    AnnotationValue[] annotationArray =
                            (AnnotationValue[]) (pairs[0].value()).value();
                    List<AnnotationValue> annotationTypeValues = new ArrayList<AnnotationValue>();
                    annotationTypeValues.addAll(Arrays.asList(annotationArray));
                    String sep = "";
                    for (AnnotationValue av : annotationTypeValues) {
                        annotation.addContent(sep);
                        annotation.addContent(annotationValueToContent(av));
                        sep = " ";
                    }
                }
                // If the container has 1 or more value defined and if the
                // repeatable type annotation is not documented, print the container.
                else {
                    addAnnotations(annotationDoc, linkInfo, annotation, pairs,
                            indent, false);
                }
            } else {
                addAnnotations(annotationDoc, linkInfo, annotation, pairs,
                        indent, linkBreak);
            }
            annotation.addContent(linkBreak ? DocletConstants.NL : "");
            results.add(annotation);
        }
        return results;
    }

    /**
     * Return the configuation for this doclet.
     *
     * @return the configuration for this doclet.
     */
    public Configuration configuration() {
        return configuration;
    }

    public Doc getParentDoc() {
        return parentDoc;
    }

    /**
     * Add the annotation types of the executable receiver.
     *
     * @param method   the executable to write the receiver annotations for.
     * @param descList list of annotation description.
     * @param htmltree the documentation tree to which the annotation info will be
     *                 added
     */
    public void addReceiverAnnotationInfo(ExecutableMemberDoc method, AnnotationDesc[] descList,
                                          Content htmltree) {
        addAnnotationInfo(0, method, descList, false, htmltree);
    }

    /**
     * Add the annotatation types for the given doc and parameter.
     *
     * @param indent the number of spaces to indent the parameters.
     * @param doc    the doc to write annotations for.
     * @param param  the parameter to write annotations for.
     * @param tree   the content tree to which the annotation types will be added
     */
    public boolean addAnnotationInfo(int indent, Doc doc, Parameter param,
                                     Content tree) {
        return addAnnotationInfo(indent, doc, param.annotations(), false, tree);
    }

    /**
     * Adds the annotation types for the given doc.
     *
     * @param indent   the number of extra spaces to indent the annotations.
     * @param doc      the doc to write annotations for.
     * @param descList the array of {@link AnnotationDesc}.
     * @param htmltree the documentation tree to which the annotation info will be
     *                 added
     */
    private boolean addAnnotationInfo(int indent, Doc doc,
                                      AnnotationDesc[] descList, boolean lineBreak, Content htmltree) {
        List<Content> annotations = getAnnotations(indent, descList, lineBreak);
        String sep = "";
        if (annotations.isEmpty()) {
            return false;
        }
        for (Content annotation : annotations) {
            htmltree.addContent(sep);
            htmltree.addContent(annotation);
            sep = " ";
        }
        return true;
    }

    /**
     * Add annotation to the annotation string.
     *
     * @param annotationDoc the annotation being documented
     * @param linkInfo      the information about the link
     * @param annotation    the annotation string to which the annotation will be added
     * @param pairs         annotation type element and value pairs
     * @param indent        the number of extra spaces to indent the annotations.
     * @param linkBreak     if true, add new line between each member value
     */
    private void addAnnotations(AnnotationTypeDoc annotationDoc, LinkInfoImpl linkInfo,
                                ContentBuilder annotation, AnnotationDesc.ElementValuePair[] pairs,
                                int indent, boolean linkBreak) {
        linkInfo.label = new StringContent("@" + annotationDoc.name());
        annotation.addContent(getLink(linkInfo));
        if (pairs.length > 0) {
            annotation.addContent("(");
            for (int j = 0; j < pairs.length; j++) {
                if (j > 0) {
                    annotation.addContent(",");
                    if (linkBreak) {
                        annotation.addContent(DocletConstants.NL);
                        int spaces = annotationDoc.name().length() + 2;
                        for (int k = 0; k < (spaces + indent); k++) {
                            annotation.addContent(" ");
                        }
                    }
                }
                annotation.addContent(getDocLink(LinkInfoImpl.Kind.ANNOTATION,
                        pairs[j].element(), pairs[j].element().name(), false));
                annotation.addContent("=");
                AnnotationValue annotationValue = pairs[j].value();
                List<AnnotationValue> annotationTypeValues = new ArrayList<AnnotationValue>();
                if (annotationValue.value() instanceof AnnotationValue[]) {
                    AnnotationValue[] annotationArray =
                            (AnnotationValue[]) annotationValue.value();
                    annotationTypeValues.addAll(Arrays.asList(annotationArray));
                } else {
                    annotationTypeValues.add(annotationValue);
                }
                annotation.addContent(annotationTypeValues.size() == 1 ? "" : "{");
                String sep = "";
                for (AnnotationValue av : annotationTypeValues) {
                    annotation.addContent(sep);
                    annotation.addContent(annotationValueToContent(av));
                    sep = ",";
                }
                annotation.addContent(annotationTypeValues.size() == 1 ? "" : "}");
                isContainerDocumented = false;
            }
            annotation.addContent(")");
        }
    }

    /**
     * Check if the annotation contains an array of annotation as a value. This
     * check is to verify if a repeatable type annotation is present or not.
     *
     * @param pairs annotation type element and value pairs
     * @return true if the annotation contains an array of annotation as a value.
     */
    private boolean isAnnotationArray(AnnotationDesc.ElementValuePair[] pairs) {
        AnnotationValue annotationValue;
        for (int j = 0; j < pairs.length; j++) {
            annotationValue = pairs[j].value();
            if (annotationValue.value() instanceof AnnotationValue[]) {
                AnnotationValue[] annotationArray =
                        (AnnotationValue[]) annotationValue.value();
                if (annotationArray.length > 1) {
                    if (annotationArray[0].value() instanceof AnnotationDesc) {
                        AnnotationTypeDoc annotationDoc =
                                ((AnnotationDesc) annotationArray[0].value()).annotationType();
                        isContainerDocumented = true;
                        if (Util.isDocumentedAnnotation(annotationDoc)) {
                            isAnnotationDocumented = true;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Content annotationValueToContent(AnnotationValue annotationValue) {
        if (annotationValue.value() instanceof Type) {
            Type type = (Type) annotationValue.value();
            if (type.asClassDoc() != null) {
                LinkInfoImpl linkInfo = new LinkInfoImpl(configuration,
                        LinkInfoImpl.Kind.ANNOTATION, type);
                linkInfo.label = new StringContent((type.asClassDoc().isIncluded() ?
                        type.typeName() :
                        type.qualifiedTypeName()) + type.dimension() + ".class");
                return getLink(linkInfo);
            } else {
                return new StringContent(type.typeName() + type.dimension() + ".class");
            }
        } else if (annotationValue.value() instanceof AnnotationDesc) {
            List<Content> list = getAnnotations(0,
                    new AnnotationDesc[]{(AnnotationDesc) annotationValue.value()},
                    false);
            ContentBuilder buf = new ContentBuilder();
            for (Content c : list) {
                buf.addContent(c);
            }
            return buf;
        } else if (annotationValue.value() instanceof MemberDoc) {
            return getDocLink(LinkInfoImpl.Kind.ANNOTATION,
                    (MemberDoc) annotationValue.value(),
                    ((MemberDoc) annotationValue.value()).name(), false);
        } else {
            return new StringContent(annotationValue.toString());
        }
    }

}
