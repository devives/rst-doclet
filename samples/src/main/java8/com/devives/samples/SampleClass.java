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

/**
 * Sample class.
 * <ul>
 *     <li> <a href="package-index.html">HTML ссылка на индекс пакета.</a>
 *     <li> <a href="internal_sphinx_anchor">Sphinx ссылка на якорь в описании пакета.</a>
 *     <li> <a href="internal_sphinx_anchor_native">Native Sphinx ссылка на якорь в описании пакета.</a>
 * </ul>
 *
 * @see SampleInterface
 * @see <a href="package-index.html">HTML ссылка на индекс пакета.</a>
 * @see <a href="internal_sphinx_anchor">Sphinx ссылка на якорь в описании пакета.</a>
 * @see ":ref:`Sphinx ссылка на якорь в описании пакета.<internal_sphinx_anchor>`"
 * @see <a href="#this-is-inline-anchor">Sphinx ссылка на инлайн якорь на странице</a>
 * @since 0.1.0 Since description.
 * @version 0.1.0 Version description.
 * @author Author name adaress.
 * @deprecated Deprecated description.
 */
@SampleAnnotation
public class SampleClass extends SampleClassAbst<String> implements SampleInterface, SampleInterface2<String> {

    /**
     * {@inheritDoc}
     *
     * @since 0.1.0 Since description.
     * @deprecated 0.1.0 Deprecated description.
     */
    @Override
    public void methodWithInlineTags() {

    }

    /**
     * The problem with inline anchors: ReStructuredText allow latin symbols only. <a id='this-text-has-no-matter'>This is inline anchor</a>.
     */
    public void inlineAnchorExample() {
    }

    /**
     * <p>
     * Inline code example 1 : {@code Object o1 = new SampleClassAbst<String>(){};}
     * <p>
     * Inline code example 2 : <code>Object o1 = new SampleClassAbst&lt;String&gt;(){};</code>
     * <p>
     * This is an example to show difference in javadoc literal and code tag:
     * <p>
     * {@literal @Getter}
     * {@literal List<Integer> nums = new ArrayList<>();}
     * <p>
     * <br>
     * {@code @Getter}
     * {@code List<Integer> nums = new ArrayList<>();}
     * </p>
     */
    public void inlineCodeExamples() {
    }

    /**
     * This is an example to show usage of HTML character entities while code snippet formatting in Javadocs
     * <pre>
     * public class Application(){
     *     {@code List<Integer> nums = new ArrayList<>(); }
     * }
     * </pre>
     * <p>
     * This is an example to show usage of javadoc code tag while code snippet formatting in Javadocs
     * <pre>
     * <code>
     * public class Application(){
     *     List&lt;Integer&gt; nums = new ArrayList&lt;&gt;();
     * }
     * </code>
     * </pre>
     * <p>
     * This is an example to show usage of javadoc code tag for handling '@' character
     * <pre>
     * public class Application(){
     *     {@code @Getter}
     *     {@code List<Integer> nums = new ArrayList<>(); }
     * }
     * </pre>
     * <p>
     * This is an example to illustrate a basic jQuery code snippet embedded in documentation comments
     * <pre>
     * {@code <script>}
     *      $document.ready(function(){
     *          console.log("Hello World!);
     *      })
     * {@code </script>}
     * </pre>
     *
     * <p>
     * This is an example to illustrate an HTML code snippet embedded in documentation comments
     * <pre>{@code
     * <html>
     * <body>
     * <h4>Hello World!</h4>
     * </body>
     * </html>}
     * </pre>
     */
    public void multilineCodeExamples() {
    }

    /**
     * Forth equals lines is correct. See tags illustrate different formats of references.
     *
     * @see #inlineAnchorExample
     * @see #inlineAnchorExample()
     * @see SampleClass#inlineAnchorExample This is multiline
     * label for reference
     * @see SampleClass#inlineAnchorExample()
     * @see com.devives.samples.inners.SampleRootClass
     * @see com.devives.samples
     * @see java.util.ArrayList
     */
    public void seeTagsInComment() {
    }

    /**
     * Forth equals lines is correct. See tags illustrate different formats of references.
     * <ul>
     * <li>{@link #inlineAnchorExample}
     * <li>{@link #inlineAnchorExample()}
     * <li>{@link SampleClass#inlineAnchorExample This is multiline
     * label for reference}
     * <li>{@link SampleClass#inlineAnchorExample()}
     * <li>{@link com.devives.samples.inners.SampleRootClass}
     * <li>{@link com.devives.samples}
     * <li>{@link java.util.ArrayList}
     * </ul>
     */
    public void linkTagsInComment() {
    }

    /**
     * Code sample with underlines.
     * <pre>{@code
     * private T _field;
     * private T field_;
     * public T _get_Field(){
     *     if (_field == null){
     *         _field = new T();
     *     }
     *     return _field;
     * }
     * public T get_Field_(){
     *     if (field_ == null){
     *         field_ = new T();
     *     }
     *     return field_;
     * }
     * }</pre>
     */
    public void codeSampleWithUnderlinesInComment() {
    }

}
