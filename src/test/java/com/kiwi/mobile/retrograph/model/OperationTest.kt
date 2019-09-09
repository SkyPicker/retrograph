package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.annotation.*

import io.mockk.*
import io.mockk.impl.annotations.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class OperationTest {

  // region Private Types

  enum class Enum {
    A, B
  }

  // TODO: Lists and arrays of enums and primitive types.
  // TODO: template parameter fields.
  // TODO: Kotlin reflection and properties.

  @Suppress("unused")
  class Object(
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val boolean: Boolean,
    val string: String,
    val enum: Enum,
    val `object`: Foo,
    val intList: List<Int>,
    val enumList: List<Enum>,
    val objectList: List<Foo>,
    val intArray: Array<Int>,
    val enumArray: Array<Enum>,
    val objectArray: Array<Foo>
  )

  @Suppress("unused")
  class GenericObject<T>(
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val boolean: Boolean,
    val string: String,
    val enum: Enum,
    val `object`: Foo,
    val intList: List<Int>,
    val enumList: List<Enum>,
    val objectList: List<Foo>,
    val intArray: Array<Int>,
    val enumArray: Array<Enum>,
    val objectArray: Array<Foo>
    //val genericObject: Generic<T>,
    //val templateArgumentObject: T
  )

  @Suppress("unused")
  class TestArguments(
    val int: Int = 99,
    val long: Long = 999999L,
    val float: Float = 99.99f,
    val double: Double = 999.99,
    val boolean: Boolean = true,
    val string: String = "test",
    val enum: Enum = Enum.B,
    val `null`: Any? = null,
    val emptyObject: Any = Any(),
    val `object`: Foo = Foo(),
    val emptyList: List<Int> = listOf(),
    val intList: List<Int> = listOf(1, 2, 3),
    val enumList: List<Enum> = listOf(Enum.A, Enum.B),
    val objectList: List<Foo> = listOf(Foo()),
    val emptyArray: Array<Int> = arrayOf(),
    val intArray: Array<Int> = arrayOf(1, 2, 3),
    val enumArray: Array<Enum> = arrayOf(Enum.A, Enum.B),
    val objectArray: Array<Foo> = arrayOf(Foo())
  )

  @Suppress("unused")
  class Foo(
    val name: String = "test"
  )

  class Bar

  @Suppress("unused")
  class Generic<T>(
    val value: T
  )

  @Suppress("unused")
  class PrimitiveFields<T>(
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val boolean: Boolean,
    val string: String,
    val enum: Enum
  )

  @Suppress("unused")
  class ObjectFields(
    val `object`: Object,
    val genericObject: GenericObject<Foo>
  )

  @Suppress("unused")
  class CollectionFields<T>(
    val intList: List<Int>,
    val enumList: List<Enum>,
    val objectList: List<Foo>,
    val intArray: Array<Int>,
    val enumArray: Array<Enum>,
    val objectArray: Array<Foo>
  )

  @Suppress("unused")
  class InlineFragmentFields<T>(

    @field:InlineFragment
    val `object`: Foo,

    @field:InlineFragment
    val objectList: List<Foo>,

    @field:InlineFragment
    val objectArray: Array<Foo>
  )

  @Suppress("unused")
  class ArgumentFields<T>(
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val `object`: Object
  )

  @Suppress("unused")
  class ArgumentFieldsArguments(
    val int: TestArguments = TestArguments(),
    val `object`: TestArguments = TestArguments()
  )

  // endregion Private Types

  // region Private Properties

  @RelaxedMockK
  private lateinit var mockParent: Document

  // endregion Private Properties

  // region Public Methods

  @Before
  fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

  @Test
  fun whenEmpty_thenSerialized() {
    // given

    val field = Operation(mockParent)

    // when

    val serialized = field.toString()

    // then

    assertThat(serialized)
      .isEqualTo("query {  }")
  }

  @Test
  fun whenNamed_thenSerialized() {
    // given

    val operation = Operation(mockParent, Operation.Type.MUTATION, "test")

    // when

    val serialized = operation.toString()

    // then

    assertThat(serialized)
      .isEqualTo("mutation test {  }")
  }

  @Test
  fun whenHasOnePrimitiveField_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .field("a")

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo("query { a }")
  }

  @Test
  fun whenHasOneObjectField_thenSerialized() {
    // given

    val field = Operation(mockParent)
      .objectField("a")
    val operation = field.finish()

    // when

    val serialized = operation.toString()

    // then

    assertThat(field)
      .isInstanceOf(Field::class.java)
    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo("query { a }")
  }

  @Test
  fun whenHasMultipleFields_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .field("a")
      .objectField("b")
      .finish()
      .field("c")

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo("query { a, b, c }")
  }

  @Test
  fun whenHasInlineFragment_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .inlineFragment("Foo")
      .objectField("b")
      .field("c")
      .finish()
      .finish()

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo("query { ... on Foo { b { c } } }")
  }

  @Test
  fun whenPrimitiveFieldsOf_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<PrimitiveFields<Foo>>()

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "int, " +
          "long, " +
          "float, " +
          "double, " +
          "boolean, " +
          "string, " +
          "enum " +
        "}"
        // @formatter:on
      )
  }

  @Test
  fun whenObjectFieldsOf_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<ObjectFields>()

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "object { " +
            "int, " +
            "long, " +
            "float, " +
            "double, " +
            "boolean, " +
            "string, " +
            "enum, " +
            "object { " +
              "name " +
            "}, " +
            "intList, " +
            "enumList, " +
            "objectList { " +
              "name " +
            "}, " +
            "intArray, " +
            "enumArray, " +
            "objectArray { " +
              "name " +
            "} "          +
          "}, " +
          "genericObject { " +
            "int, " +
            "long, " +
            "float, " +
            "double, " +
            "boolean, " +
            "string, " +
            "enum, " +
            "object { " +
              "name " +
            "}, " +
            "intList, " +
            "enumList, " +
            "objectList { " +
              "name " +
            "}, " +
            "intArray, " +
            "enumArray, " +
            "objectArray { " +
              "name " +
            "} " +
            /*"genericObject { " +
              "value {" +
                "name " +
              "} " +
            "}, " +
            "templateArgumentObject {" +
              "name " +
            "} " +*/
          "} " +
        "}"
        // @formatter:on
      )
  }

  @Test
  fun whenCollectionFieldsOf_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<CollectionFields<Foo>>()

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "intList, " +
          "enumList, " +
          "objectList { " +
            "name " +
          "}, " +
          "intArray, " +
          "enumArray, " +
          "objectArray { " +
            "name " +
          "} " +
        "}"
        // @formatter:on
      )
  }

  @Test
  fun whenInlineFragmentFieldsOf_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<InlineFragmentFields<Foo>>()

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "object { " +
            "... on Foo { " +
              "name " +
            "} " +
          "}, " +
          "objectList { " +
            "... on Foo { " +
              "name " +
            "} " +
          "}, " +
          "objectArray { " +
            "... on Foo { " +
              "name " +
            "} " +
          "} " +
        "}"
        // @formatter:on
      )
  }

  @Test
  fun whenArgumentFieldsOf_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<ArgumentFields<Foo>>(ArgumentFieldsArguments())

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "int( " +
            "int: 99, " +
            "long: 999999, " +
            "float: 99.99, " +
            "double: 999.99, " +
            "boolean: true, " +
            "string: \"test\", " +
            "enum: B, " +
            "null: null, " +
            "emptyObject: {  " +
            "}, " +
            "object: { " +
              "name: \"test\" " +
            "}, " +
            "emptyList: [  ], " +
            "intList: [ 1, 2, 3 ], " +
            "enumList: [ A, B ], " +
            "objectList: [ " +
              "{ " +
                "name: \"test\" " +
              "} " +
            "], " +
            "emptyArray: [  ], " +
            "intArray: [ 1, 2, 3 ], " +
            "enumArray: [ A, B ], " +
            "objectArray: [ " +
              "{ " +
                "name: \"test\" " +
              "} " +
            "] " +
          "), " +
          "long, " +
          "float, " +
          "double, " +
          "object( " +
            "int: 99, " +
            "long: 999999, " +
            "float: 99.99, " +
            "double: 999.99, " +
            "boolean: true, " +
            "string: \"test\", " +
            "enum: B, " +
            "null: null, " +
            "emptyObject: {  " +
            "}, " +
            "object: { " +
              "name: \"test\" " +
            "}, " +
            "emptyList: [  ], " +
            "intList: [ 1, 2, 3 ], " +
            "enumList: [ A, B ], " +
            "objectList: [ " +
              "{ " +
                "name: \"test\" " +
              "} " +
            "], " +
            "emptyArray: [  ], " +
            "intArray: [ 1, 2, 3 ], " +
            "enumArray: [ A, B ], " +
            "objectArray: [ " +
              "{ " +
                "name: \"test\" " +
              "} " +
            "] " +
          ") { " +
            "int, " +
            "long, " +
            "float, " +
            "double, " +
            "boolean, " +
            "string, " +
            "enum, " +
            "object { " +
              "name " +
            "}, " +
            "intList, " +
            "enumList, " +
            "objectList { " +
              "name " +
            "}, " +
            "intArray, " +
            "enumArray, " +
            "objectArray { " +
              "name " +
            "} " +
          "} " +
        "}"
        // @formatter:on
      )
  }

  @Test
  fun whenFieldsHaveAlias_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .field("a", "aa")
      .objectField("b", "bb")
      .finish()
      .field("c")

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo("query { aa: a, bb: b, c }")
  }

  @Test
  fun whenFinished_thenReturnsParent() {
    // given

    val operation = Operation(mockParent)

    // when

    operation.finish()

    // then

    verify {
      mockParent.finish()
    }
  }

  // endregion Public Methods
}
