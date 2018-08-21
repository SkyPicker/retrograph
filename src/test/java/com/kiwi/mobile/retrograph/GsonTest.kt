package com.kiwi.mobile.retrograph

import com.google.gson.*
import com.google.gson.reflect.*
import com.kiwi.mobile.retrograph.model.*
import org.assertj.core.api.Assertions.*

import org.junit.*

class GsonTest {

  private companion object {
    private const val GRAPH_QL_BODY = """{ "data": { "default" : "Hi" } }"""
  }

  @Test
  fun whenDeserialize_thenDefaultsAreSet() {
    val gson = Gson()

    val response = gson.fromJson<Response<String>>(
      GRAPH_QL_BODY,
      object: TypeToken<Response<String>>() {}.type
    )

    assertThat(response)
      .isNotNull
    assertThat(response.data)
      .isNotNull
      .isNotEmpty
      .containsKey("default")
      .containsValue("Hi")
    assertThat(response.errors)
      .isNotNull
      .isEmpty()
  }
}
