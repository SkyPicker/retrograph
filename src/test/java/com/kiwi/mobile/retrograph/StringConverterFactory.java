package com.kiwi.mobile.retrograph;

import java.lang.annotation.*;
import java.lang.reflect.*;

import retrofit2.*;

import okhttp3.*;

final class StringConverterFactory extends Converter.Factory {
  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(
    Type type, Annotation[] annotations, Retrofit retrofit
  ) {
    return (Converter<ResponseBody, String>) ResponseBody::string;
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(
    Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations,
    Retrofit retrofit
  ) {
    return (Converter<String, RequestBody>) value ->
      RequestBody.create(MediaType.parse("text/plain"), value);
  }
}
