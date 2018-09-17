package com.kiwi.mobile.retrograph.model

import io.mockk.*
import io.mockk.impl.annotations.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class ArgumentsTest {

  // region Private Types

  private enum class Enum {
    A, B
  }

  @Suppress("unused")
  class Foo(
    val name: String = "test"
  )

  @Suppress("unused")
  private class TestObject(
    val int: Int = 123,
    val long: Long = 123456789,
    val float: Float = 0.123f,
    val double: Double = 0.123456789,
    val boolean: Boolean = true,
    val string: String = "string",
    val enum: Enum = Enum.B,
    val `null`: Any? = null,
    val emptyObject: Any = Any(),
    val `object`: Foo = Foo(),
    val emptyList: List<Enum> = listOf(),
    val intList: List<Int> = listOf(1, 2, 3),
    val enumList: List<Enum> = listOf(Enum.A, Enum.B),
    val objectList: List<Foo> = listOf(Foo()),
    val emptyArray: Array<Enum> = arrayOf(),
    val intArray: Array<Int> = arrayOf(1, 2, 3),
    val enumArray: Array<Enum> = arrayOf(Enum.A, Enum.B),
    val objectArray: Array<Foo> = arrayOf(Foo())
  )

  // endregion Private Types

  // region Private Properties

  @RelaxedMockK
  private lateinit var mockParent: Field<Any>

  // endregion Private Properties

  // region Public Methods

  @Before
  fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

  @Test
  fun whenCreated_thenEmpty() {
    // given

    val arguments = Arguments(mockParent)

    // when

    val serialized = arguments.toString()

    // then

    assertThat(arguments.isEmpty)
      .isTrue()
    assertThat(arguments.isNotEmpty)
      .isFalse()
    assertThat(serialized)
      .isEqualTo("")
  }

  @Test
  fun whenPrimitive_thenSerialized() {
    // given

    val arguments = Arguments(mockParent)
      .argument("name", "value")

    // when

    val serialized = arguments.toString()

    // then

    assertThat(serialized)
      .isEqualTo("name: \"value\"")
  }

  @Test
  fun whenList_thenSerialized() {
    // given

    val arguments = Arguments(mockParent)
      .argument("name", listOf(1, 2, 3))

    // when

    val serialized = arguments.toString()

    // then

    assertThat(serialized)
      .isEqualTo("name: [1, 2, 3]")
  }

  @Test
  fun whenObject_thenSerialized() {
    // given

    // @formatter:off
    val arguments = Arguments(mockParent)
    val finishedArguments = arguments
      .objectArgument("name")
        .value("int", 123)
        .value("long", 123456789L)
        .value("float", 0.123f)
        .value("double", 0.123456789)
        .value("boolean", true)
        .value("string", "string")
        .value("enum", Enum.B)
        .value("null", null)
        .objectValue("emptyObject")
          .finish()
        .objectValue("object")
          .value("name", "test")
          .finish()
        .listValue("emptyList")
          .finish()
        .listValue("intList")
          .values(1, 2, 3)
          .finish()
        .listValue("enumList")
          .values(Enum.A, Enum.B)
          .finish()
        .listValue("objectList")
          .objectValue()
            .value("name", "test")
            .finish()
          .finish()
        .finish()
    // @formatter:on

    assertThat(finishedArguments)
      .isSameAs(arguments)

    // when

    val serialized = arguments.toString()

    // then

    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "name: { " +
          "int: 123, " +
          "long: 123456789, " +
          "float: 0.123, " +
          "double: 0.123456789, " +
          "boolean: true, " +
          "string: \"string\", " +
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
          "] " +
        "}"
        // @formatter:on
      )
  }

  @Test
  fun whenInstance_thenSerialized() {
    // given

    val arguments = Arguments(mockParent)
      .argumentsOf(TestObject())

    // when

    val serialized = arguments.toString()

    // then

    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "int: 123, " +
        "long: 123456789, " +
        "float: 0.123, " +
        "double: 0.123456789, " +
        "boolean: true, " +
        "string: \"string\", " +
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
        "]"
        // @formatter:on
      )
  }

  @Test
  fun whenFinished_thenReturnsParent() {
    // given

    val field = Arguments(mockParent)

    // when

    val parent = field.finish()

    // then

    assertThat(parent)
      .isSameAs(mockParent)
  }

  // endregion Public Methods

}
