package com.kiwi.mobile.retrograph.model

import com.kiwi.mobile.retrograph.annotation.*

import io.mockk.*
import io.mockk.impl.annotations.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class OperationTest {

  // region Private Types

  @Suppress("unused")
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
  )

  @Suppress("unused")
  class Foo(
    val name: String = "test"
  )

  @Suppress("unused")
  class Bar

  @Suppress("unused")
  class FieldArguments(
    val argument: String = "test"
  )

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
  class PrimitiveFieldsArguments<T>(
    val int: FieldArguments = FieldArguments(),
    val long: FieldArguments = FieldArguments(),
    val float: FieldArguments = FieldArguments(),
    val double: FieldArguments = FieldArguments(),
    val boolean: FieldArguments = FieldArguments(),
    val string: FieldArguments = FieldArguments(),
    val enum: FieldArguments = FieldArguments()
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
  class CollectionFieldsArguments(
    val intList: FieldArguments = FieldArguments(),
    val enumList: FieldArguments = FieldArguments(),
    val objectList: FieldArguments = FieldArguments(),
    val intArray: FieldArguments = FieldArguments(),
    val enumArray: FieldArguments = FieldArguments(),
    val objectArray: FieldArguments = FieldArguments()
  )

  @Suppress("unused")
  class ObjectFields(
    val `object`: Object,
    val genericObject: GenericObject<Foo>
  )

  @Suppress("unused")
  class ObjectFieldsArguments(
    val `object`: FieldArguments = FieldArguments(),
    val genericObject: FieldArguments = FieldArguments()
  )

  @Suppress("unused")
  class ParentObjectFieldsArguments(
    val `object`: ObjectArguments = ObjectArguments(),
    val genericObject: ObjectArguments = ObjectArguments()
  )

  @Suppress("unused")
  class ObjectArguments(
    val argument: String = "test",
    val int: FieldArguments = FieldArguments(),
    val long: FieldArguments = FieldArguments(),
    val float: FieldArguments = FieldArguments(),
    val double: FieldArguments = FieldArguments(),
    val boolean: FieldArguments = FieldArguments(),
    val string: FieldArguments = FieldArguments(),
    val enum: FieldArguments = FieldArguments(),
    val `object`: FooObjectArguments = FooObjectArguments(),
    val intList: FieldArguments = FieldArguments(),
    val enumList: FieldArguments = FieldArguments(),
    val objectList: FooObjectArguments = FooObjectArguments(),
    val intArray: FieldArguments = FieldArguments(),
    val enumArray: FieldArguments = FieldArguments(),
    val objectArray: FooObjectArguments = FooObjectArguments()
  )

  @Suppress("unused")
  class FooObjectArguments(
    val argument: String = "test",
    val name: FieldArguments = FieldArguments()
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

    // @formatter:off
    val operation = Operation(mockParent)
      .inlineFragment("Foo")
        .objectField("b")
          .field("c")
            .finish()
          .finish()
        .finish()
    // @formatter:on

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
  fun whenPrimitiveFieldsOfWithArguments_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<PrimitiveFields<Foo>>(PrimitiveFieldsArguments<Foo>())

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "int(argument: \"test\"), " +
          "long(argument: \"test\"), " +
          "float(argument: \"test\"), " +
          "double(argument: \"test\"), " +
          "boolean(argument: \"test\"), " +
          "string(argument: \"test\"), " +
          "enum(argument: \"test\") " +
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
  fun whenCollectionFieldsOfWithArguments_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<CollectionFields<Foo>>(CollectionFieldsArguments())

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "intList(argument: \"test\"), " +
          "enumList(argument: \"test\"), " +
          "objectList(argument: \"test\") { " +
            "name " +
          "}, " +
          "intArray(argument: \"test\"), " +
          "enumArray(argument: \"test\"), " +
          "objectArray(argument: \"test\") { " +
            "name " +
          "} " +
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
          "} " +
        "}"
        // @formatter:on
      )
  }

  @Test
  fun whenObjectFieldsOfWithArguments_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<ObjectFields>(ObjectFieldsArguments())

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "object(argument: \"test\") { " +
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
          "genericObject(argument: \"test\") { " +
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
  fun whenObjectFieldsOfWithChildArguments_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf<ObjectFields>(ParentObjectFieldsArguments())

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "query { " +
          "object(argument: \"test\") { " +
            "int(argument: \"test\"), " +
            "long(argument: \"test\"), " +
            "float(argument: \"test\"), " +
            "double(argument: \"test\"), " +
            "boolean(argument: \"test\"), " +
            "string(argument: \"test\"), " +
            "enum(argument: \"test\"), " +
            "object(argument: \"test\") { " +
              "name(argument: \"test\") " +
            "}, " +
            "intList(argument: \"test\"), " +
            "enumList(argument: \"test\"), " +
            "objectList(argument: \"test\") { " +
              "name(argument: \"test\") " +
            "}, " +
            "intArray(argument: \"test\"), " +
            "enumArray(argument: \"test\"), " +
            "objectArray(argument: \"test\") { " +
              "name(argument: \"test\") " +
            "} "          +
          "}, " +
          "genericObject(argument: \"test\") { " +
            "int(argument: \"test\"), " +
            "long(argument: \"test\"), " +
            "float(argument: \"test\"), " +
            "double(argument: \"test\"), " +
            "boolean(argument: \"test\"), " +
            "string(argument: \"test\"), " +
            "enum(argument: \"test\"), " +
            "object(argument: \"test\") { " +
              "name(argument: \"test\") " +
            "}, " +
            "intList(argument: \"test\"), " +
            "enumList(argument: \"test\"), " +
            "objectList(argument: \"test\") { " +
              "name(argument: \"test\") " +
            "}, " +
            "intArray(argument: \"test\"), " +
            "enumArray(argument: \"test\"), " +
            "objectArray(argument: \"test\") { " +
              "name(argument: \"test\") " +
            "} " +
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
