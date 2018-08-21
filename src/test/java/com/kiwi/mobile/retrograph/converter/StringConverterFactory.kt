package com.kiwi.mobile.retrograph.converter

import okhttp3.*

import retrofit2.*

import java.lang.reflect.*

class StringConverterFactory: Converter.Factory() {

  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ) =
    Converter<ResponseBody, String> { it.string() }

  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<Annotation>,
    methodAnnotations: Array<Annotation>,
    retrofit: Retrofit
  ) =
    Converter<String, RequestBody> { value ->
      RequestBody.create(MediaType.parse("text/plain"), value)
    }
}
