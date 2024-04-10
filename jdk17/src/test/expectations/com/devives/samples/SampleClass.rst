SampleClass
===========

.. java:package:: com.devives.samples
   :noindex:

@SampleAnnotation

.. java:type:: public class SampleClass extends SampleClassAbst<String> implements SampleInterface, SampleInterface2<String>

   Sample class.

   * `HTML ссылка на индекс пакета. <package-index.html>`__
   * :ref:`Sphinx ссылка на якорь в описании пакета. <internal_sphinx_anchor>`
   * :ref:`Native Sphinx ссылка на якорь в описании пакета. <internal_sphinx_anchor_native>`

   .. seealso::

      | :java:ref:`SampleInterface <com.devives.samples.SampleInterface>`
      | `HTML ссылка на индекс пакета. <package-index.html>`_
      | :ref:`Sphinx ссылка на якорь в описании пакета. <internal_sphinx_anchor>`
      | \ :ref:`Sphinx ссылка на якорь в описании пакета.<internal_sphinx_anchor>`\ 
      | `Sphinx ссылка на инлайн якорь на странице <#this-is-inline-anchor>`_

Constructors
------------

SampleClass
^^^^^^^^^^^

.. java:constructor:: public SampleClass()
   :outertype: SampleClass

Methods
-------

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

   * :java:ref:`SampleClass.inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`SampleClass.inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`This is multiline
     label for reference <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`SampleClass.inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
   * :java:ref:`SampleRootClass <com.devives.samples.inners.SampleRootClass>`
   * :java:ref:`com.devives.samples <com.devives.samples>`
   * :java:ref:`ArrayList <java.util.ArrayList>`

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

      | :java:ref:`SampleClass.inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`SampleClass.inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`This is multiline
         label for reference <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`SampleClass.inlineAnchorExample() <com.devives.samples.SampleClass.inlineAnchorExample()>`
      | :java:ref:`SampleRootClass <com.devives.samples.inners.SampleRootClass>`
      | :java:ref:`com.devives.samples <com.devives.samples>`
      | :java:ref:`ArrayList <java.util.ArrayList>`
