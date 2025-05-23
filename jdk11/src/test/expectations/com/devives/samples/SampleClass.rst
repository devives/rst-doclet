SampleClass
===========

.. java:package:: com.devives.samples
   :noindex:

@SampleAnnotation

.. java:type:: public class SampleClass extends SampleClassAbst<String> implements SampleInterface, SampleInterface2<String>

   .. versionadded:: 0.1.0 Since description.

   .. versionchanged:: 0.1.0 Version description.

   .. deprecated:: Deprecated description.

   Sample class.

   * `HTML ссылка на индекс пакета. <package-index.html>`__
   * :ref:`Sphinx ссылка на якорь в описании пакета. <internal_sphinx_anchor>`
   * :ref:`Native Sphinx ссылка на якорь в описании пакета. <internal_sphinx_anchor_native>`

   .. sectionauthor:: Author name adaress.

   .. seealso::

      | :java:ref:`SampleInterface <com.devives.samples.SampleInterface>`
      | `HTML ссылка на индекс пакета. <package-index.html>`__
      | :ref:`Sphinx ссылка на якорь в описании пакета. <internal_sphinx_anchor>`
      | \ :ref:`Sphinx ссылка на якорь в описании пакета.<internal_sphinx_anchor>`\ 
      | `Sphinx ссылка на инлайн якорь на странице <this-is-inline-anchor>`_

Constructors
------------

SampleClass
^^^^^^^^^^^

.. java:constructor:: public SampleClass()
   :outertype: SampleClass

Methods
-------

codeSampleWithUnderlinesInComment
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void codeSampleWithUnderlinesInComment()
   :outertype: SampleClass

   Code sample with underlines.

   .. parsed-literal::

      private T _field;
       private T field\_;
       public T _get_Field(){
           if (_field == null){
               _field = new T();
           }
           return _field;
       }
       public T get_Field\_(){
           if (field\_ == null){
               field\_ = new T();
           }
           return field\_;
       }

inlineAnchorExample
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void inlineAnchorExample()
   :outertype: SampleClass

   .. _this-text-has-no-matter:

   The problem with inline anchors: ReStructuredText allow latin symbols only. This is inline anchor.

inlineCodeExamples
^^^^^^^^^^^^^^^^^^

.. java:method:: public void inlineCodeExamples()
   :outertype: SampleClass

   Inline code example 1 : ``Object o1 = new SampleClassAbst<String>(){};``

   Inline code example 2 : ``Object o1 = new SampleClassAbst<String>(){};``

   This is an example to show difference in javadoc literal and code tag:

   @Getter
   List<Integer> nums = new ArrayList<>();

   ``@Getter``
   ``List<Integer> nums = new ArrayList<>();``

linkTagsInComment
^^^^^^^^^^^^^^^^^

.. java:method:: public void linkTagsInComment()
   :outertype: SampleClass

   Forth equals lines is correct. See tags illustrate different formats of references.

   * :java:ref:`inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`This is multiline
     label for reference <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`SampleRootClass <com.devives.samples.inners.SampleRootClass>`
   * `com.devives.samples <package-index.html>`__
   * :java:ref:`ArrayList <java.util.ArrayList>`

methodWithInlineTags
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void methodWithInlineTags()
   :outertype: SampleClass

   .. versionadded:: 0.1.0 Since description.

   .. deprecated:: 0.1.0 Deprecated description.

   Some method with inline tags.

multilineCodeExamples
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void multilineCodeExamples()
   :outertype: SampleClass

   This is an example to show usage of HTML character entities while code snippet formatting in Javadocs

   .. parsed-literal::

      public class Application(){
          List<Integer> nums = new ArrayList<>(); 
      }



   This is an example to show usage of javadoc code tag while code snippet formatting in Javadocs

   .. parsed-literal::

      public class Application(){
           List<Integer> nums = new ArrayList<>();
       }



   This is an example to show usage of javadoc code tag for handling '@' character

   .. parsed-literal::

      public class Application(){
          @Getter
          List<Integer> nums = new ArrayList<>(); 
      }



   This is an example to illustrate a basic jQuery code snippet embedded in documentation comments

   .. parsed-literal::

      <script>
           $document.ready(function(){
               console.log("Hello World!);
           })
      </script>



   This is an example to illustrate an HTML code snippet embedded in documentation comments

   .. parsed-literal::

      <html>
       <body>
       <h4>Hello World!</h4>
       </body>
       </html>

seeTagsInComment
^^^^^^^^^^^^^^^^

.. java:method:: public void seeTagsInComment()
   :outertype: SampleClass

   Forth equals lines is correct. See tags illustrate different formats of references.


   .. seealso::

      | :java:ref:`inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`This is multiline
         label for reference <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`SampleRootClass <com.devives.samples.inners.SampleRootClass>`
      | `com.devives.samples <package-index.html>`__
      | :java:ref:`ArrayList <java.util.ArrayList>`
