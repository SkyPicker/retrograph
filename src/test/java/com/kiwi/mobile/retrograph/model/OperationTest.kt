package com.kiwi.mobile.retrograph.model

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
  class Object<T>(
    val int: Int,
    val long: Long,
    val double: Double,
    val boolean: Boolean,
    val string: String,
    val enum: Enum,
    //val intList: List<Int>,
    //val enumList: List<Enum>,
    //val objectList: List<Item>
    val intArray: Array<Int>,
    //val enumArray: Array<Enum>,
    val objectArray: Array<Item>,
    val genericObject: Generic<T>,
    val templateArgumentObject: T
  )

  @Suppress("unused")
  class Item(
    val name: String
  )

  @Suppress("unused")
  class Generic<T>(
    val value: T
  )

  @Suppress("unused")
  class Foo<T>(
    val int: Int,
    val long: Long,
    val double: Double,
    val boolean: Boolean,
    val string: String,
    val enum: Enum,
    //val intList: List<Int>,
    //val enumList: List<Enum>,
    //val objectList: List<Item>
    val intArray: Array<Int>,
    //val enumArray: Array<Enum>,
    val objectArray: Array<Item>,
    val `object`: Object<String>,
    val genericObject: Generic<T>,
    val templateArgumentObject: T
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
      .isEqualTo("query  {  }")
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
      .isEqualTo("query  { a }")
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
      .isEqualTo("query  { a }")
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
      .isEqualTo("query  { a, b, c }")
  }

  @Test
  fun whenFieldsOfObject_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .fieldsOf("a", Foo::class.java)

    /*val operation = Operation(mockParent)
      .fieldsOf("a", Foo::class)*/

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo(
        "query  { a { int, long, double, boolean, string, enum, intArray, objectArray { name },"
          + " object { int, long, double, boolean, string, enum, intArray, objectArray { name },"
          + " genericObject { value }, templateArgumentObject }, genericObject { value },"
          + " templateArgumentObject } }"
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
      .isEqualTo("query  { aa: a, bb: b, c }")
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
