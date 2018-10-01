package com.kiwi.mobile.retrograph.extension

import org.assertj.core.api.Assertions.*

import org.junit.*

class ReflectionExtensionsTest {

  @Test
  fun whenNonListType_thenIsListIsFalse() {
    assertThat(Boolean::class.java.isList)
      .isFalse()
    assertThat(Any::class.java.isList)
      .isFalse()
  }

  @Test
  fun whenListType_thenIsListIsTrue() {
    assertThat(List::class.java.isList)
      .isTrue()
    val stringList = listOf<String>()
    assertThat(stringList.javaClass.isList)
      .isTrue()
  }

  @Test
  fun whenPrimitiveType_thenIsPrimitiveOrWrapperIsTrue() {
    assertThat(Boolean::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(Char::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(String::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(Byte::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(Short::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(Int::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(Long::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(Float::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(Double::class.java.isPrimitiveOrWrapper)
      .isTrue()
  }

  @Test
  fun whenWrapperType_thenIsPrimitiveOrWrapperIsTrue() {
    assertThat(java.lang.Boolean::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Character::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.String::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Byte::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Short::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Integer::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Long::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Float::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Double::class.java.isPrimitiveOrWrapper)
      .isTrue()
    assertThat(java.lang.Void::class.java.isPrimitiveOrWrapper)
      .isTrue()
  }
}