.. java:import:: java.util HashMap

.. java:import:: java.util Collection

.. java:import:: java.util List

SampleFieldDeclarations
=======================

.. java:package:: com.devives.samples
   :noindex:

.. java:type:: public class SampleFieldDeclarations<T>

   Sample class illustrate different field declarations.

Fields
------

booleanField
^^^^^^^^^^^^

.. java:field:: public boolean booleanField
   :outertype: SampleFieldDeclarations

   Public field holds primitive value.

genericField1
^^^^^^^^^^^^^

.. java:field:: public T genericField1
   :outertype: SampleFieldDeclarations

   Public field holds generic type object reference.

genericField2
^^^^^^^^^^^^^

.. java:field:: public List<T> genericField2
   :outertype: SampleFieldDeclarations

   Public field holds generic list reference.

genericField3
^^^^^^^^^^^^^

.. java:field:: public List<String> genericField3
   :outertype: SampleFieldDeclarations

   Public field holds string list reference.

genericField4
^^^^^^^^^^^^^

.. java:field:: public List<SampleInterface2<String>> genericField4
   :outertype: SampleFieldDeclarations

   Public field holds object with nested generic types.

genericField5
^^^^^^^^^^^^^

.. java:field:: public List<Collection<SampleInterface2<HashMap<String, T>>>> genericField5
   :outertype: SampleFieldDeclarations

   Public field holds object with nested generic types.

interfaceField
^^^^^^^^^^^^^^

.. java:field:: public SampleInterface interfaceField
   :outertype: SampleFieldDeclarations

   Public field holds an interface reference.

packagePrivateField
^^^^^^^^^^^^^^^^^^^

.. java:field:: Object packagePrivateField
   :outertype: SampleFieldDeclarations

   Package private field.

protectedField
^^^^^^^^^^^^^^

.. java:field:: protected Object protectedField
   :outertype: SampleFieldDeclarations

   Protected field.

publicField
^^^^^^^^^^^

.. java:field:: public Object publicField
   :outertype: SampleFieldDeclarations

   Public field.

Constructors
------------

SampleFieldDeclarations
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SampleFieldDeclarations()
   :outertype: SampleFieldDeclarations
