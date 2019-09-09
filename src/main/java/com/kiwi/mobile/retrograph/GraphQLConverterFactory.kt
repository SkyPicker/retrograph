package com.kiwi.mobile.retrograph

import com.kiwi.mobile.retrograph.annotation.*
import com.kiwi.mobile.retrograph.exception.*
import com.kiwi.mobile.retrograph.model.Response

import okhttp3.*

import retrofit2.*

import java.io.*
import java.lang.reflect.*

class GraphQLConverterFactory:
  Converter.Factory() {

  // region Public Types

  companion object {

    @JvmStatic
    fun create() = GraphQLConverterFactory()
  }

  // endregion Public Types

  // region Private Types

  private class ResponseConverter<T>(
    private val converter: Converter<ResponseBody, Response<T>>
  ):
    Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): T {
      val response = converter.convert(responseBody)
        ?: throw IOException("Response is null")

      if (response.errors.isNotEmpty()) {
        throw GraphQLException(response)
      }

      if (response.data == null) {
        throw GraphQLException(response)
      }

      return response.data
    }
  }

  // endregion Private Types

  // region Public Methods

  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *>? {
    if (!canHandle(annotations)) {
      return null
    }

    val responseType = object: ParameterizedType {
      override fun getActualTypeArguments() = arrayOf(type)
      override fun getOwnerType() = null
      override fun getRawType() = Response::class.java
    }

    val delegate = retrofit.nextResponseBodyConverter<Response<Any>>(
      this, responseType, annotations
    )

    return ResponseConverter(delegate)
  }

  // endregion Public Methods

  // region Private Methods

  private fun canHandle(annotations: Array<Annotation>) =
    annotations.find { it is GraphQL } != null

  // endregion Private Methods
}
