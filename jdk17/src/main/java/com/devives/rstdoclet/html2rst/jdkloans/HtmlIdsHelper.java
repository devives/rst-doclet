package com.devives.rstdoclet.html2rst.jdkloans;

import jdk.javadoc.internal.doclets.formats.html.HtmlIds;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlId;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.Map;

public class HtmlIdsHelper {


    /**
     * Returns an id for a parameter, such as a component of a record.
     *
     * <p>Warning: this may not be unique on the page if used when there are
     * other like-named parameters.
     *
     * @param paramName the parameter name
     * @return the id
     * @see HtmlIds#forParam(String)
     */
    static HtmlId forParam(String paramName) {
        return HtmlId.of("param-" + paramName);
    }

    /**
     * Returns an id for a fragment of text, such as in an {@code @index} tag,
     * using a map of counts to ensure the id is unique.
     *
     * @param text the text
     * @param counts the map of counts
     *
     * @return the id
     * @see HtmlIds#forText(String)
     */
    static HtmlId forText(String text, Map<String, Integer> counts) {
        String base = text.replaceAll("\\s+", "");
        int count = counts.compute(base, (k, v) -> v == null ? 0 : v + 1);
        return HtmlId.of(count == 0 ? base : base + "-" + count);
    }

    /**
     * Returns an id for a property, suitable for use when the simple name
     * will be unique within the page, such as in the page for the
     * declaration of the enclosing class or interface.
     *
     * <p>Warning: the name may not be unique if a field with the same
     * name is also being documented in the same class.
     *
     * @param element the element
     *
     * @return the id
     *
     * @see #forMember(VariableElement)
     */
    static HtmlId forProperty(ExecutableElement element) {
        return HtmlId.of(element.getSimpleName().toString());
    }

    /**
     * Returns an id for an executable element, suitable for use when the
     * simple name and argument list will be unique within the page, such as
     * in the page for the declaration of the enclosing class or interface.
     *
     * @param utils
     * @param element the element
     * @return the id
     */
    static HtmlId forMember(Utils utils, ExecutableElement element) {
        String a = element.getSimpleName()
                + utils.makeSignature(element, null, true, true);
        // utils.makeSignature includes spaces
        return HtmlId.of(a.replaceAll("\\s", ""));
    }
}
