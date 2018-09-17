package com.kiwi.mobile.retrograph.model

import io.mockk.*
import io.mockk.impl.annotations.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class FieldTest {

  // region Private Types

  private enum class Enum {
    A, B
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
    val finishedField = field
      .arguments()
        .argument("int", 123)
        .argument("long", 123456789L)
        .argument("float", 0.123f)
        .argument("double", 0.123456789)
        .argument("boolean", true)
        .argument("string", "string")
        .argument("enum", Enum.B)
        .argument("null", null)
          .objectArgument("emptyObject")
          .finish()
        .objectArgument("object")
          .value("name", "test")
          .finish()
        .listArgument("emptyList")
          .finish()
        .listArgument("intList")
          .values(1, 2, 3)
          .finish()
        .listArgument("enumList")
          .values(Enum.A, Enum.B)
          .finish()
        .listArgument("objectList")
          .objectValue()
            .value("name", "test")
            .finish()
          .finish()
        .finish()
    // @formatter:on

    assertThat(finishedField)
      .isSameAs(field)

    // when

    val serialized = field.toString()

    // then

    assertThat(serialized)
      .isEqualTo(
        // @formatter:off
        "test( " +
          "int: 123, " +
          "long: 123456789, " +
          "float: 0.123, " +
          "double: 0.123456789, " +
          "boolean: true, " +
          "string: \"string\", " +
          "enum: B, " +
          "null: null, " +
          "emptyObject: {  }, " +
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
        ")"
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

