package com.kiwi.mobile.retrograph.model

import io.mockk.*
import io.mockk.impl.annotations.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class FieldTest {

  // region Private Types

  private enum class Enum {
    VALUE
  }

  // endregion Private Types

  // region Private Properties

  @RelaxedMockK
  private lateinit var mockParent: SelectionSet<Any>

  // endregion Private Properties

  // region Public Methods

  @Before
  fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

  @Test
  fun whenEmpty_thenSerialized() {
    // given

    val field = Field(mockParent)

    // when

    val serialized = field.toString()

    // then

    assertThat(serialized)
      .isEqualTo("")
  }

  @Test
  fun whenPrimitive_thenSerialized() {
    // given

    val field = Field(mockParent, "test")

    // when

    val serialized = field.toString()

    // then

    assertThat(serialized)
      .isEqualTo("test")
  }

  @Test
  fun whenHasArguments_thenSerialized() {
    // given

    // @formatter:off

    val field = Field(mockParent, "test")
      .arguments()
        .argument("int", 123)
        .argument("long", 123456789L)
        .argument("float", 0.123f)
        .argument("double", 0.123456789)
        .argument("boolean", true)
        .argument("string", "string")
        .argument("null", null)
        .argument("enum", Enum.VALUE)
        .argument("emptyList", listOf<Enum>())
        .argument("list", listOf(Enum.VALUE, Enum.VALUE))
        .argument("emptyArray", arrayOf<Enum>())
        .argument("array", arrayOf(Enum.VALUE, Enum.VALUE))
        .objectArgument("emptyObject")
          .finish()
        .objectArgument("object")
          .value("int", 123)
          .value("long", 123456789L)
          .value("float", 0.123f)
          .value("double", 0.123456789)
          .value("boolean", true)
          .value("string", "string")
          .value("null", null)
          .value("enum", Enum.VALUE)
          .value("emptyList", listOf<Enum>())
          .value("list", listOf(Enum.VALUE, Enum.VALUE))
          .value("emptyArray", arrayOf<Enum>())
          .value("array", arrayOf(Enum.VALUE, Enum.VALUE))
          .objectValue("emptyObject")
             .finish()
        .finish()
      .finish()

    // @formatter:on

    // when

    val serialized = field.toString()

    // then

    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "test( "
          + "int: 123, "
          + "long: 123456789, "
          + "float: 0.123, "
          + "double: 0.123456789, "
          + "boolean: true, "
          + "string: \"string\", "
          + "null: null, "
          + "enum: VALUE, "
          + "emptyList: [], "
          + "list: ["
            + "VALUE, VALUE"
          + "], "
          + "emptyArray: [], "
          + "array: ["
            + "VALUE, VALUE"
          + "], "
          + "emptyObject: {  }, "
          + "object: { "
            + "int: 123, "
            + "long: 123456789, "
            + "float: 0.123, "
            + "double: 0.123456789, "
            + "boolean: true, "
            + "string: \"string\", "
            + "null: null, "
            + "enum: VALUE, "
            + "emptyList: [], "
            + "list: ["
              + "VALUE, VALUE"
            + "], "
            + "emptyArray: [], "
            + "array: ["
              + "VALUE, VALUE"
            + "], "
            + "emptyObject: {  } "
          + "} "
        + ")"
        // @formatter:on
      )
  }

  @Test
  fun whenHasAlias_thenSerialized() {
    // given

    val field = Field(mockParent, "test", "alias")

    // when

    val serialized = field.toString()

    // then

    assertThat(serialized)
      .isEqualTo("alias: test")
  }

  @Test
  fun whenFinished_thenReturnsParent() {
    // given

    val field = Field(mockParent, "test")

    // when

    field.finish()

    // then

    verify {
      mockParent.finish()
    }
  }

  // endregion Public Methods
}

