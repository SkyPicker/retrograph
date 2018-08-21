package com.kiwi.mobile.retrograph.model

import io.mockk.*
import io.mockk.impl.annotations.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class OperationTest {

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
  fun whenFieldHasAlias_thenSerialized() {
    // given

    val operation = Operation(mockParent)
      .field("a", "aa")
      .objectField("b", "bb")
      .finish()

    // when

    val serialized = operation.toString()

    // then

    assertThat(operation)
      .isInstanceOf(Operation::class.java)
    assertThat(serialized)
      .isEqualTo("query  { aa: a, bb: b }")
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
