.. java:import:: java.util Map

SampleClass17
=============

.. java:package:: com.devives.samples
   :noindex:

@SampleAnnotation

.. java:type:: public class SampleClass17 extends SampleClassAbst<String> implements SampleInterface, SampleInterface2<String>

   Sample class with java 17 javadoc features.

Constructors
------------

SampleClass17
^^^^^^^^^^^^^

.. java:constructor:: public SampleClass17()
   :outertype: SampleClass17

Methods
-------

getLinkWithTypeParametersInReturn
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Map<String, Object> getLinkWithTypeParametersInReturn()
   :outertype: SampleClass17

   Link with label in return.

   :return: :java:ref:`Map <java.util.Map>`

seeTagsInComment
^^^^^^^^^^^^^^^^

.. java:method:: public void seeTagsInComment()
   :outertype: SampleClass17

   JavaDoc 17 allow generics in ``@see`` tag.


   .. seealso::

      | :java:ref:`ArrayList <java.util.ArrayList>`
