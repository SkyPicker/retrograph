package com.kiwi.mobile.retrograph.extension

import com.google.gson.reflect.*

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
}