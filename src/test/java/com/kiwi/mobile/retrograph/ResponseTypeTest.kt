package com.kiwi.mobile.retrograph

import com.google.common.reflect.*

import com.kiwi.mobile.retrograph.model.*

import org.assertj.core.api.Assertions.*

import org.junit.*

class ResponseTypeTest {

  @Test
  fun abstractTypeEqualsTypeTokenType() {
    val abstractType = ResponseType(String::class.java)
    val typeTokenType = object: TypeToken<Response<String>>() {}.type

    assertThat(abstractType)
      .isEqualTo(typeTokenType)
  }
}
